package main.datastructures.curve

import kotlin.math.abs
import kotlin.math.pow

interface IPoint<T : Number> {
  val dimension: Int
  
  operator fun get(dim: Int): T
  
  fun isInUnitCube(): Boolean {
    for (d in 0 until dimension) {
      if (abs(this[d].toDouble()) > 1) {
        return false
      }
    }
    return true
  }
  
  fun <R : Number> distanceSquared(other: IPoint<R>): Double {
    var dist = 0.0
    for (d in 0 until dimension) {
      dist += (this[d].toDouble() - other[d].toDouble()).pow(2.0)
    }
    return dist
  }
}