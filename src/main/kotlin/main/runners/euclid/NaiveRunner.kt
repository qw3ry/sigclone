package main.runners.euclid

import data.Signature
import main.runners.AbstractRunner
import java.io.File
import java.io.PrintStream
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class NaiveRunner(private val tolerance: Double, outputStream: PrintStream, filterMainAndOverrides: Boolean, minSize: Int)
  : AbstractRunner(outputStream, filterMainAndOverrides, minSize) {

  private val signatures = LinkedHashSet<Signature>()
  private val lock = ReentrantReadWriteLock(false)

  override fun getNeighbors(signature: Signature): Collection<Signature> {
    try {
      lock.read {
        return signatures.filter {
          it.isDistanceBelow(signature, tolerance)
        }
      }
    } finally {
      lock.write { this.signatures.remove(signature) }
    }
  }

  override fun loadModel(file: File) = Unit

  override fun setFunctions(signatures: Collection<Signature>) {
    lock.write { this.signatures.addAll(signatures) }
  }
}