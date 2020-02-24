package main.runners

import Java9Lexer
import data.Signature
import help.readFileToText
import help.split
import main.appendTimedInformation
import org.antlr.v4.runtime.*
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.word2vec.Word2Vec
import org.deeplearning4j.text.sentenceiterator.BaseSentenceIterator
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory
import parsing.ErrorListener
import java.io.File
import java.io.InputStream
import java.io.PrintStream
import java.util.*

abstract class AbstractW2vRunner(outputStream: PrintStream, filterMainAndOverrides: Boolean, minSize: Int,
                                 private val hiddenLayerSize: Int = 50,
                                 private val windowSize: Int = 5)
  : AbstractRunner(outputStream, filterMainAndOverrides, minSize) {
  private lateinit var w2v: Word2Vec

  override fun tokenAnalysis(lexers: Sequence<Pair<Lexer, File>>) {
    if (::w2v.isInitialized) return
    outputStream.appendTimedInformation("Word2Vec:") {
      val sentenceIterator = CodeFileIterator(lexers.map { it.second }.toHashSet())
      val tokenizerFactory = LexerTokenizerFactory()
      w2v = Word2Vec.Builder()
          .minWordFrequency(1)
          .layerSize(hiddenLayerSize)
          .seed(245)
          .windowSize(windowSize)
          .iterate(sentenceIterator)
          .tokenizerFactory(tokenizerFactory)
          .build()
      w2v.fit()
    }
  }

  fun saveModel(file: File) {
    WordVectorSerializer.writeWord2VecModel(w2v, file)
  }

  override fun loadModel(file: File) {
    w2v = WordVectorSerializer.readWord2VecModel(file)
  }

  protected fun vector(signature: Signature): DoubleArray {
    val result = DoubleArray(hiddenLayerSize) { 0.0 }
    for (word in signature.words()) {
      val vec = w2v.getWordVector(word)
      if (vec != null) {
        for (i in 0 until hiddenLayerSize) {
          result[i] += vec[i]
        }
      } else {
        outputStream.println("WARN Did not find vector for '$word'")
      }
    }
    return result
  }

  private fun Signature.words() = (
      paramTypes.asSequence().flatMap { it.split().asSequence() }
          + paramIdentifiers.asSequence().flatMap { it.split().asSequence() }
          + identifier.asSequence().flatMap { it.split().asSequence() }
          + (returnType?.split()?.asSequence() ?: emptySequence())
      )

  private class CodeFileIterator(val files: Collection<File>) : BaseSentenceIterator() {
    private var iter = files.iterator()

    override fun reset() {
      iter = files.iterator()
    }

    override fun nextSentence(): String {
      return readFileToText(iter.next())
    }

    override fun hasNext() = iter.hasNext()
  }

  private class LexerTokenizerFactory : TokenizerFactory {
    private var preProcessor = TokenPreProcess { it }
    override fun setTokenPreProcessor(preProcessor: TokenPreProcess) {
      this.preProcessor = preProcessor
    }

    override fun getTokenPreProcessor(): TokenPreProcess {
      return preProcessor
    }

    override fun create(toTokenize: String) = create(CharStreams.fromString(toTokenize))

    override fun create(toTokenize: InputStream) = create(CharStreams.fromStream(toTokenize))

    private fun create(toTokenize: CharStream): LexerTokenizer {
      val lexer = Java9Lexer(toTokenize)
      lexer.removeErrorListeners()
      val s = toTokenize.toString()
      toTokenize.seek(0)
      lexer.addErrorListener(ErrorListener(File(s)))
      return LexerTokenizer(lexer, preProcessor)
    }
  }

  private abstract class BaseTokenizer(protected var preProcessor: TokenPreProcess) : Tokenizer {
    override fun setTokenPreProcessor(tokenPreProcessor: TokenPreProcess) {
      this.preProcessor = tokenPreProcessor
    }
  }

  private class LexerTokenizer(val lexer: Lexer, preProcessor: TokenPreProcess) : BaseTokenizer(preProcessor) {
    private val tokens = CommonTokenStream(lexer)
    private var queue: Queue<String> = LinkedList<String>()

    init {
      advance()
    }

    override fun hasMoreTokens() = !queue.isEmpty()

    override fun countTokens() = getTokens().size

    override fun nextToken(): String {
      try {
        return queue.poll()
      } finally {
        if (queue.isEmpty()) {
          advance()
        }
      }
    }

    override fun getTokens() = lexer.allTokens
        .map { it.text }
        .flatMap { it.split() }
        .map { preProcessor.preProcess(it) }
        .toList()

    private fun advance() {
      while (queue.isEmpty() && tokens.LA(1) != Token.EOF) {
        if (tokens.LA(1) == Java9Lexer.Identifier) {
          queue = LinkedList<String>(tokens.LT(1).text.split())
        }
        tokens.consume()
      }
    }

  }
}
