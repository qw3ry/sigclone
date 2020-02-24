package parsing

import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.atn.ATNConfigSet
import org.antlr.v4.runtime.dfa.DFA
import java.io.File
import java.util.*

class ErrorListener(val file: File) : ANTLRErrorListener {
  override fun reportAttemptingFullContext(recognizer: Parser, dfa: DFA, startIndex: Int, stopIndex: Int, conflictingAlts: BitSet, configs: ATNConfigSet) {
    report(startIndex, stopIndex, "Conflicting: $conflictingAlts")
  }

  override fun syntaxError(recognizer: Recognizer<*, *>, offendingSymbol: Any?, line: Int, charPositionInLine: Int, msg: String?, e: RecognitionException?) {
    System.err.println("Syntax Error at $line:$charPositionInLine on '$offendingSymbol'.")
    System.err.println("  Message: $msg")
    System.err.println("  Exception: $e")
  }

  override fun reportAmbiguity(recognizer: Parser, dfa: DFA, startIndex: Int, stopIndex: Int, exact: Boolean, ambigAlts: BitSet, configs: ATNConfigSet) {
    report(startIndex, stopIndex, "exact: $exact, ambigAlts: $ambigAlts")
  }

  override fun reportContextSensitivity(recognizer: Parser, dfa: DFA, startIndex: Int, stopIndex: Int, prediction: Int, configs: ATNConfigSet) {
    report(startIndex, stopIndex, "Prediction: $prediction")
  }

  private fun report(startIndex: Int, stopIndex: Int, additionalInformation: String) {
    System.err.println("Error in $file@$startIndex:$stopIndex: $additionalInformation")
  }
}