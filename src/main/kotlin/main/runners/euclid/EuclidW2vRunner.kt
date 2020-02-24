package main.runners.euclid

import data.Signature
import main.runners.AbstractW2vRunner
import java.io.PrintStream
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.math.pow

class EuclidW2vRunner(private val tolerance: Double, outputStream: PrintStream, filterMainAndOverrides: Boolean, minSize: Int)
  : AbstractW2vRunner(outputStream, filterMainAndOverrides, minSize) {
  private var map = mutableMapOf<Signature, DoubleArray>()
  private val lock = ReentrantReadWriteLock(false)

  override fun setFunctions(signatures: Collection<Signature>) {
    lock.write {
      map.clear()
      for (signature in signatures) {
        map[signature] = vector(signature)
      }
    }
  }

  override fun getNeighbors(signature: Signature): Collection<Signature> {
    try {
      lock.read {
        val vector = map[signature] ?: vector(signature)
        return map.asSequence()
            .filter { isDistanceBelow(vector, it.value, tolerance) }
            .map { it.key }
            .toList()
      }
    } finally {
      lock.write {
        map.remove(signature)
      }
    }
  }
}
