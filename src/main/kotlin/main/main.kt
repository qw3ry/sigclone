package main

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import main.output.BigCloneEvalFormatter
import main.output.RememberingPrinter
import main.runners.RunnerType
import main.runners.analyzer.AnalyzingW2vRunner
import main.runners.createRunner
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import java.io.File
import java.io.OutputStream
import java.io.PrintStream

fun main(args: Array<String>) {
  System.err.println(args.asSequence().joinToString { it })
  Main().subcommands(W2vAnalysis(), CloneDetection(), W2vModel()).main(args)
}

abstract class CommonOptions(name: String? = null, help: String? = null)
  : CliktCommand(name = name, help = help ?: "", printHelpOnEmptyArgs = true) {
  protected val sourceDirectory: List<File> by argument(name = "source code", help = "The source code to analyze. Can be given as a list of files, or as list of folders.").file(
      mustExist = true,
      canBeFile = true,
      canBeDir = true,
      mustBeWritable = true,
      mustBeReadable = true
  ).multiple(required = true)
  protected val filterMainAndOverride by option("-f", "--filter-main-override", help = "This option controls, whether main methods, and methods marked as @Override, are filtered out prior to the clone detection").flag("--no-filter", default = true)
  protected val splitIdentifier by option("-s", "--split", help = "This flag controls, whether identifiers are split at word boundaries.").flag("--no-split", default = true)
  protected val minSize by option("--min-size", help = "The minimum number of lines, that a function should span to be considered.").int().default(6)
}

class Main : CliktCommand() {
  override fun run() = Unit
}

class W2vAnalysis : CommonOptions(name = "create-w2v", help =
"""
  This command creates a word2vec model that can be used by a later clone analysis.
  For the parameters, please see also https://deeplearning4j.org/docs/latest/deeplearning4j-nlp-word2vec
""".trimIndent()) {
  private val output by argument(name = "model", help = "Where to save the created word2vec model").file(canBeDir = false)
  private val windowSize by option("-w", "--window-size", help = "The window size for the word2vec training.").int().default(5)
  private val layerSize by option("-l", "--layer-size", help = "The layer size for the word2vec training.").int().default(100)

  override fun run() {
    val analyzer = AnalyzingW2vRunner(System.out, filterMainAndOverride, minSize, layerSize, windowSize)
    analyzer.analyzeOnly(sourceDirectory)
    analyzer.saveModel(output)
  }
}

class W2vModel : CliktCommand(name = "analyze-w2v", help = """
  This command helps you to analyze a word2vec model.
  It reads words from stdin and finds the nearest words in the model.
""".trimIndent()) {
  private val count by option("-c", "--count", help = "Number of nearest words returned").int().default(10)
  private val modelFiles by argument("model", help = "One or multiple models to analyze").file(mustExist = true, canBeDir = false, mustBeReadable = true).multiple(true)
  override fun run() {
    val models = modelFiles.map { WordVectorSerializer.readWord2VecModel(it) }
    while (true) {
      println("Type words. Start them with - to subtract. Separate by space.")
      val line = readLine() ?: return
      val words = line.split(" ")
      val pWords = words.filter { !it.startsWith("-") }
      val nWords = words.filter { it.startsWith("-") }.map { it.substring(1) }

      val maxLen = models
          .map { it.wordsNearest(pWords, nWords, count) }
          .map { strings -> strings.map { it.unicodeLength() }.max() ?: 0 }
      val seen = List(models.size) { mutableSetOf<String>() }

      // header
      print("    | ")
      println(
          modelFiles
              .map { it.name }
              .mapIndexed { index, string -> string.padStart(maxLen[index]) }
              .joinToString(" | ")
      )

      // data
      for (i in 1..count) {
        print((" %2d | ").format(i))
        println(
            models.asSequence().map { it.wordsNearest(pWords, nWords, i) }
                .mapIndexed { modelIndex, foundWords ->
                  foundWords.filter {
                    if (seen[modelIndex].contains(it)) false
                    else {
                      seen[modelIndex].add(it)
                      true
                    }
                  }.map { w ->
                    " ".repeat(maxLen[modelIndex] - w.unicodeLength()) + w
                  }
                }
                .map {
                  assert(it.size == 1)
                  it
                }
                .flatten()
                .joinToString(" | "))
      }
    }
  }
}

class CloneDetection : CommonOptions(name = "detect", help = """
  This command runs a clone evaluation.
  The clone pairs are printed to stdout.
""".trimIndent()) {
  private val type by option("--type", help = "The runner type to use").enum<RunnerType>().default(RunnerType.RWD)
  private val tolerance by option("-t", "--tolerance", help = "The clone detection tolerance").double().default(0.2)
  private val model by option("-m", "--model", help = "The word2vec model. If no model is given, but it is needed by the runner, the runner creates a new model").file(mustExist = true, canBeDir = false, mustBeReadable = true)
  private val verbose by option("-v", "--verbose", help = "If enabled, the log messages are printed again, after the clones are outputted. Useful if stdout is not redirected to a file").flag()
  private val quiet by option("-q", "--quiet", help = "If enabled, no logging messages are printed").flag()

  private val formatter = BigCloneEvalFormatter()
  private val output by lazy {
    RememberingPrinter(
        if (quiet) OutputStream.nullOutputStream() else System.err,
        verbose)

  }
  private val runner by lazy {
    createRunner(type)(
        tolerance,
        output,
        filterMainAndOverride,
        minSize
    )
  }


  override fun run() {
    try {
      output.appendValue("Source dir", sourceDirectory.map { it.absolutePath })
      output.appendValue("Tolerance", tolerance)
      output.println()
      output.flush()

      model?.let {
        output.appendTimedInformation("Loading model") {
          runner.loadModel(it)
        }
      }

      output.appendTimedInformation("Parsing") { runner.parse(sourceDirectory, splitIdentifier) }
      System.gc()
      output.appendTimedInformation("setFunctions") { runner.setFunctions() }
      output.appendTimedInformation("evaluate") { runner.evaluate(formatter) }

    } finally {
      System.out.flush()
      Thread.sleep(0)
      output.printAgain()
    }
  }
}

fun <T> PrintStream.appendTimedInformation(name: String, fn: () -> T): T {
  val start = System.nanoTime()
  try {
    return fn()
  } finally {
    appendValue(name, (System.nanoTime() - start) / 1e9)
  }
}

fun <T> PrintStream.appendValue(name: String, value: T) {
  this.println(name.padEnd(20) + " $value")
}

fun String.unicodeLength() = this.chars().map { if (it < 1000) 1 else 2 }.sum()
