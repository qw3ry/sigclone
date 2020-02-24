package main.datastructures.curve.zcurve

import main.datastructures.curve.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

open class DoubleZCurve(final override val dimension: Int, private val precision: Double) : SpaceFillingCurve<Double, Double> {
  private val diagonalInFullSpace = sqrt(dimension.toDouble())
  
  override fun range(center: IPoint<Double>, radius: Double): List<Pair<Double, Double>> {
    assert(center.dimension == dimension)
    return range(Point(dimension) { 0 }, 0, center, radius).range
  }
  
  private fun range(gridCube: IPoint<Int>, depth: Int, center: IPoint<Double>, radius: Double): PartialRange<Double> {
    val distanceSquared = distanceFromCenterOfGridCube(center, gridCube, depth)
    val curvePoint = toCurvePoint(Point(dimension) { gridCube[it] / powerOf2(depth).toDouble() })
    val localCurveLength = 1 / powerOf2(depth * dimension).toDouble()
    // if the square is larger than precision, we require it to be fully inside/outside to abort the recursion
    val tolerance = if (localCurveLength <= precision) 0.0 else diagonalInFullSpace / (2 * powerOf2(depth) - precision)
    
    return when {
      distanceSquared >= (radius + tolerance).pow(2) ->
        PartialRange(curvePoint)
      distanceSquared <= (radius - tolerance) * abs(radius - tolerance) ->
        openRange(curvePoint, localCurveLength)
      else ->
        refinedRange(gridCube, depth, center, radius)
    }
  }
  
  private fun refinedRange(gridCube: IPoint<Int>, depth: Int, center: IPoint<Double>, radius: Double): PartialRange<Double> {
    return (0 until powerOf2(dimension))
      .map { i ->
        val point = Point(dimension) { dim -> 2 * gridCube[dim] + ((i / powerOf2(dim)) % 2) }
        range(point, depth + 1, center, radius)
      }
      .sortedBy { it.rangeStart }
      .reduce { r1, r2 -> r1 + r2 }
  }
  
  private fun openRange(curvePoint: Double, localCurveLength: Double) =
    PartialRange(
      curvePoint,
      isStartOpen = true,
      isEndOpen = true,
      range = listOf(Pair(curvePoint, curvePoint + localCurveLength))
    )
  
  private fun distanceFromCenterOfGridCube(point: IPoint<Double>, gridCube: IPoint<Int>, depth: Int): Double {
    val cubeCenter = Point(dimension) { (gridCube[it] + 0.5) / powerOf2(depth) }
    return cubeCenter.distanceSquared(point)
  }
  
  override fun toCurvePoint(point: IPoint<Double>): Double {
    assert(point.dimension == dimension)
    for (dim in 0 until dimension) assert(point[dim] in 0.0..1.0 && point[dim] < 1.0)
    
    return toCurvePoint(point, 1.0, 1.0)
  }
  
  private fun <T : Number> toCurvePoint(point: IPoint<T>, range: Double, maxStep: Double): Double {
    assert(point.dimension == dimension)
    
    var location = 0.0
    var step = maxStep
    for (dim in (dimension - 1) downTo 0) {
      step /= 2
      if (point[dim].toDouble() % range >= range / 2.0) location += step
    }
    
    if (step > precision) {
      // increase precision
      location += toCurvePoint(point, range / 2.0, step)
    }
    
    return location
  }
  
  private fun powerOf2(n: Int): Int = 1 shl n
}