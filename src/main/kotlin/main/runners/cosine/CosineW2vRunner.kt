package main.runners.cosine

import data.Signature
import main.runners.AbstractW2vRunner
import java.io.PrintStream
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.math.pow
import kotlin.math.sqrt

class CosineW2vRunner(private val tolerance: Double, outputStream: PrintStream, filterMainAndOverrides: Boolean, minSize: Int)
  : AbstractW2vRunner(outputStream, filterMainAndOverrides, minSize) {

  private var map = mutableMapOf<Signature, Pair<DoubleArray, Double>>()
  private val lock = ReentrantReadWriteLock(false)

  override fun setFunctions(signatures: Collection<Signature>) {
    lock.write {
      map.clear()
      for (signature in signatures) {
        map[signature] = calcVectorAndLength(signature)
      }
    }
  }

  override fun getNeighbors(signature: Signature): Collection<Signature> {
    try {
      lock.read {
        val vector = map[signature] ?: calcVectorAndLength(signature)
        return map.asSequence()
            .filter { vector.isCosineDistanceBelow(it.value, tolerance) }
            .map { it.key }
            .toList()
      }
    } finally {
      lock.write {
        // avoid duplicates
        map.remove(signature)
      }
    }
  }

  private fun calcVectorAndLength(signature: Signature): Pair<DoubleArray, Double> {
    val v = vector(signature)
    val l = sqrt(v.sumByDouble { it.pow(2) })
    return Pair(v, l)
  }

}
