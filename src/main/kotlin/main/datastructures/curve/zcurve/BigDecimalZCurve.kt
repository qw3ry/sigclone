package main.datastructures.curve.zcurve

import main.datastructures.curve.*
import org.apfloat.Apfloat
import help.plus
import help.times

open class BigDecimalZCurve(final override val dimension: Int, private val precision: Apfloat) : SpaceFillingCurve<Double, Apfloat> {
  override fun toCurvePoint(point: IPoint<Double>): Apfloat {
    assert(point.dimension == dimension)
    for (dim in 0 until dimension) assert(point[dim] >= 0 && point[dim] < 1.0)

    return toCurvePoint(point, 1.0, Apfloat(1.0))
  }

  val half = Apfloat(0.5)

  private fun <T : Number> toCurvePoint(point: IPoint<T>, range: Double, maxStep: Apfloat): Apfloat {
    assert(point.dimension == dimension)

    var location = Apfloat(0.0)
    var step = maxStep
    for (dim in (dimension - 1) downTo 0) {
      step *= half
      if (point[dim].toDouble() % range >= range / 2.0) location += step
    }

    if (step > precision) {
      // increase precision
      location += toCurvePoint(point, range / 2.0, step)
    }

    return location
  }

  override fun range(center: IPoint<Double>, radius: Double): List<Pair<Apfloat, Apfloat>> {
    TODO("not implemented")
  }
}