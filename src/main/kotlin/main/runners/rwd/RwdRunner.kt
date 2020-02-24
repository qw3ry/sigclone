package main.runners.rwd

import data.HashedSignature
import data.Signature
import main.runners.AbstractRunner
import java.io.File
import java.io.PrintStream
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.streams.toList

class RwdRunner(private val tolerance: Double,
                outputStream: PrintStream,
                filterMainAndOverrides: Boolean,
                minSize: Int) :
    AbstractRunner(outputStream, filterMainAndOverrides, minSize) {

  private val signatures = LinkedHashMap<Signature, HashedSignature>()
  private val lock = ReentrantReadWriteLock(false)

  override fun getNeighbors(signature: Signature): Collection<Signature> {
    val hashed = signatures[signature] ?: HashedSignature(signature)
    val size = hashed.size()
    try {
      lock.read {
        return signatures.values.stream()
            .filter { it != hashed }
            .filter { it.isDistanceBelow(hashed, tolerance) }
            .map { it.signature }
            .toList()
      }
    } finally {
      lock.write { this.signatures.remove(signature) }
    }
  }

  override fun setFunctions(signatures: Collection<Signature>) {
    lock.write {
      this.signatures.putAll(signatures.associateBy({ it }, { HashedSignature(it) }))
    }
  }

  override fun loadModel(file: File) = Unit
}