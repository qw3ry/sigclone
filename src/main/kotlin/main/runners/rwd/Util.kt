package main.runners.rwd

import data.HashedSignature
import data.Signature
import org.apache.commons.collections4.Bag


fun Signature.size() =
    identifier.size + paramIdentifiers.size + paramTypes.size + if (returnType == null) 0 else 1

fun HashedSignature.size() = signature.size()

/* this speeds up the process compared to Signature.distance(other) */
fun Signature.isDistanceBelow(other: Signature, threshold: Double): Boolean {
  var thr = threshold
  if (thr < 0) return false
  thr -= identifier.distance(other.identifier)
  if (thr < 0) return false
  thr -= paramIdentifiers.distance(other.paramIdentifiers)
  if (thr < 0) return false
  thr -= paramTypes.distance(other.paramTypes)
  if (thr < 0) return false
  return thr >= 1 || returnType == other.returnType
}

fun HashedSignature.isDistanceBelow(other: HashedSignature, threshold: Double): Boolean {
  var thr = threshold * (size() + other.size())
  if (thr < 0) return false
  thr -= identifier.distance(other.identifier)
  if (thr < 0) return false
  thr -= paramIdentifiers.distance(other.paramIdentifiers)
  if (thr < 0) return false
  thr -= paramTypes.distance(other.paramTypes)
  if (thr < 0) return false
  return thr >= 1 || returnType == other.returnType
}

fun <T> Bag<T>.distance(other: Bag<T>): Int {
  var dist = size + other.size

  for (t in uniqueSet()) {
    dist -= 2 * kotlin.math.min(this.getCount(t), other.getCount(t))
  }

  return dist
}