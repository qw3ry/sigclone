package main.runners.cosine

import kotlin.math.pow

fun Pair<DoubleArray, Double>.isCosineDistanceBelow2(other: Pair<DoubleArray, Double>, threshold: Double): Boolean {
  assert(first.size == other.first.size)
  var numerator = 0.0
  val denominator = second * other.second

  for (i in first.indices) {
    numerator += first[i] * other.first[i]
  }
  return numerator / denominator >= 1 - threshold
}

fun Pair<DoubleArray, Double>.isCosineDistanceBelow(other: Pair<DoubleArray, Double>, tolerance: Double): Boolean {
  assert(first.size == other.first.size)

  var threshold = second.pow(2) + other.second.pow(2) - 2 * second * other.second * (1 - tolerance)
  for (i in first.indices) {
    threshold -= (first[i] - other.first[i]).pow(2)
    if (threshold < 0) return false
  }
  return true
}