package main.runners

import Java9Lexer
import Java9Parser
import data.Signature
import help.readFileToText
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import main.appendValue
import main.output.Formatter
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.atn.PredictionMode
import org.antlr.v4.runtime.misc.ParseCancellationException
import parsing.ErrorListener
import parsing.MethodExtractionVisitor
import parsing.data.MethodDeclaration
import parsing.data.toSignature
import java.io.File
import java.io.PrintStream
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.streams.asStream

abstract class AbstractRunner(
    protected val outputStream: PrintStream,
    private val filterMainAndOverrides: Boolean,
    private val minSize: Int) : Runner {

  private var signatures: List<Signature> = emptyList()

  protected open fun tokenAnalysis(lexers: Sequence<Pair<Lexer, File>>) = Unit

  override fun parse(sourceDirectory: Collection<File>, splitIdentifier: Boolean) {
    val lexers = getLexers(sourceDirectory)
    tokenAnalysis(lexers)
    signatures = lexers.asStream()
        .parallel()
        .flatMap { visitFile(CommonTokenStream(it.first), it.second, splitIdentifier) }
        .collect(Collectors.toList())
    outputStream.appendValue("Num signatures:", signatures.size)
  }

  override fun evaluate(formatter: Formatter) {
    runBlocking {
      signatures
          .asSequence()
          .map { signature ->
            async {
              val functions = getNeighbors(signature)
              functions.joinToString(separator = "\n") { formatter.formatMatch(signature, it) }
            }
          }
          .chunked(200)
          .map {
            async {
              it.map { it.await() }
                  .filter { it != "" }
                  .joinToString(separator = "\n") { it }
            }
          }
          .forEach {
            runBlocking {
              println(it.await())
            }
          }
    }
  }

  override fun setFunctions() {
    setFunctions(signatures)
  }

  abstract fun setFunctions(signatures: Collection<Signature>)

  /**
   * Must be thread-safe
   */
  abstract fun getNeighbors(signature: Signature): Collection<Signature>

  protected fun lexer(file: File): Java9Lexer {
    val lexer = Java9Lexer(CharStreams.fromString(readFileToText(file), file.absolutePath))
    lexer.removeErrorListeners()
    lexer.addErrorListener(ErrorListener(file))
    return lexer
  }

  private fun visitFile(tokens: TokenStream, file: File, splitIdentifier: Boolean): Stream<Signature> {
    return try {
      MethodExtractionVisitor(file)
          .visit(parse(tokens))
          .filter { filterMethod(it) }
          .map { it.toSignature(splitIdentifier) }
          .stream()
    } catch (t: Throwable) {
      System.err.println("Exception while parsing $file")
      Stream.empty()
    }
  }

  private fun parse(input: TokenStream): Java9Parser.CompilationUnitContext {
    return try {
      val parser = Java9Parser(input)
      parser.interpreter.predictionMode = PredictionMode.SLL
      parser.removeErrorListeners()
      parser.errorHandler = BailErrorStrategy()
      //parser.addErrorListener(DiagnosticErrorListener())
      //parser.interpreter.predictionMode = PredictionMode.LL_EXACT_AMBIG_DETECTION
      parser.compilationUnit()
    } catch (exception: ParseCancellationException) {
      // SLL failed, so trying again with ll
      input.seek(0)

      val parser = Java9Parser(input)
      parser.removeErrorListeners()
      parser.compilationUnit()
    }
  }

  /**
   * Must be thread-safe
   */
  protected open fun filterMethod(method: MethodDeclaration): Boolean {
    if (filterMainAndOverrides) {
      if (method.name == "main") return false
      if (method.modifiers.contains("@Override")) return false
    }
    if (method.lines.second - method.lines.first <= minSize) return false
    return true
  }

  protected fun getLexers(sourceDirectory: Collection<File>) =
      sourceDirectory.asSequence()
          .flatMap { it.walk() }
          .filter { it.isFile }
          .filter { it.extension == "java" }
          .map { Pair(lexer(it), it) }
}