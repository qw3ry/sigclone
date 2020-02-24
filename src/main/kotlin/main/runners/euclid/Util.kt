package main.runners.euclid

import data.Signature
import org.apache.commons.collections4.Bag
import kotlin.math.pow


fun isDistanceBelow(vector1: DoubleArray, vector2: DoubleArray, tolerance: Double): Boolean {
  assert (vector1.size == vector2.size)
  var dist = 0.0
  for (i in vector1.indices) {
    dist += (vector1[i] - vector2[i]).pow(2)
    if (dist > tolerance) return false
  }
  return dist <= tolerance
}



fun Signature.isDistanceBelow(other: Signature, tolerance: Double): Boolean {
  val tolSquared = tolerance * tolerance
  var remainingTolerance = tolSquared

  if(remainingTolerance < 0) return false
  remainingTolerance -= identifier.distanceSquared(other.identifier)
  if(remainingTolerance < 0) return false
  remainingTolerance -= paramIdentifiers.distanceSquared(other.paramIdentifiers)
  if(remainingTolerance < 0) return false
  remainingTolerance -= paramTypes.distanceSquared(other.paramTypes)
  if(remainingTolerance < 0) return false
  return remainingTolerance >= 1 || returnType == other.returnType
}

private fun Bag<String>.distanceSquared(other: Bag<String>): Int {
  val allKeys = listOf(uniqueSet(), other.uniqueSet()).flatten().toSet()
  return allKeys.map { getCount(it) - other.getCount(it) }.map { it * it }.sum()
}
