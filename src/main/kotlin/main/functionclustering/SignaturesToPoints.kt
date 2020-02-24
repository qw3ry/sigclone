package main.functionclustering

import data.Signature
import main.datastructures.curve.IPoint
import main.datastructures.curve.Point
import org.apache.commons.collections4.Bag
import org.apache.commons.collections4.bag.HashBag
import kotlin.math.max

class SignaturesToPoints(signatures: Collection<Signature>) {
  
  private val idMaxCount = mutableMapOf<String, Int>()
  private val typeMaxCount = mutableMapOf<String, Int>()
  
  private val typeMap: Map<Int, String>
  private val idMap: Map<Int, String>
  
  val dimension: Int
  
  init {
    val usefulIds = HashBag<String>()
    val usefulTypes = HashBag<String>()
    
    signatures.forEach { signature ->
      val ids = HashBag(signature.paramIdentifiers)
      ids.addAll(signature.identifier)
      usefulIds.generateDimensions(ids)
      idMaxCount.maxCount(ids)

      val types = HashBag(signature.paramTypes)
      if (signature.returnType != null) types.add(signature.returnType)
      usefulTypes.generateDimensions(types)
      typeMaxCount.maxCount(types)
    }
    
    typeMap = usefulTypes.byIndex()
    idMap = usefulIds.byIndex(typeMap.size)
    
    dimension = typeMap.size + idMap.size
  }
  
  fun signatureToPoint(signature: Signature): IPoint<Double> {
    return Point(idMap.size + typeMap.size) { dim ->
      when {
        dim < typeMap.size ->
          countAndNormalize(typeMaxCount, requireNotNull(typeMap[dim]), signature.paramTypes, signature.returnType)
        else ->
          countAndNormalize(idMaxCount, requireNotNull(idMap[dim]), signature.identifier, signature.paramIdentifiers)
      }
    }
  }

  private fun countAndNormalize(max: Map<String, Int>, type: String, bag1: Bag<String>, bag2: Bag<String>): Double {
    return (bag1.getCount(type) + bag2.getCount(type)) / (max.getOrDefault(type, 0).toDouble() + 1)
  }

  private fun countAndNormalize(max: Map<String, Int>, type: String, bag1: Bag<String>, returnType: String?): Double {
    return (bag1.getCount(type) + if (returnType != null && returnType == type) 1 else 0) / (max.getOrDefault(type, 0).toDouble() + 1)
  }
}

fun <T : Any> bag(element: T?): HashBag<T> {
  return HashBag(if (element == null) listOf() else listOf(element))
}

private fun <T> MutableMap<T, Int>.maxCount(other: Bag<T>) {
  other.uniqueSet().forEach {
    this[it] = max(getOrDefault(it, 0), other.getCount(it))
  }
}

private fun <T> Bag<T>.generateDimensions(t: Collection<T>) {
  t.forEach(this::generateDimensions)
}

private fun <T> Bag<T>.generateDimensions(t: T) {
  add(t)
}

private fun <T> Bag<T>.byIndex(offset: Int = 0): Map<Int, T> {
  return uniqueSet()
    .withIndex()
    .associateBy({ it.index + offset }, { it.value })
}