package main.runners.analyzer

import data.Signature
import main.runners.AbstractW2vRunner
import java.io.File
import java.io.PrintStream

class AnalyzingW2vRunner(outputStream: PrintStream, filterMainAndOverrides: Boolean, minSize: Int, hiddenLayerSize: Int, windowSize: Int)
  : AbstractW2vRunner(outputStream, filterMainAndOverrides, minSize, hiddenLayerSize, windowSize) {
  override fun setFunctions(signatures: Collection<Signature>) = throw NotImplementedError("Not supported")
  override fun getNeighbors(signature: Signature): Collection<Signature> = throw NotImplementedError("Not supported")

  fun analyzeOnly(sourceDirectory: Collection<File>) {
    val lexers = getLexers(sourceDirectory)
    tokenAnalysis(lexers)
  }
}