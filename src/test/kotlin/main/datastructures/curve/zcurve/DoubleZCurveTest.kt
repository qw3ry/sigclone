package main.datastructures.curve.zcurve

import main.datastructures.curve.Point
import io.kotlintest.matchers.doubles.shouldBeGreaterThan
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import kotlin.math.sqrt

const val PRECISION = 1.0e-16
const val ONE = 1.0 - PRECISION

class DoubleZCurveTest : StringSpec({
  "checkEdgeCases" {
    DoubleZCurve(2, PRECISION).toCurvePoint(Point(0.0, 0.0)) shouldBe 0.0
    DoubleZCurve(2, PRECISION).toCurvePoint(Point(0.0, 0.5)) shouldBe 0.5
    DoubleZCurve(2, PRECISION).toCurvePoint(Point(0.5, 0.0)) shouldBe 0.25
    DoubleZCurve(2, PRECISION).toCurvePoint(Point(0.5, 0.5)) shouldBe 0.75
    DoubleZCurve(2, PRECISION).toCurvePoint(Point(ONE, ONE)) shouldBeGreaterThan ONE
  }
  "checkDimensionExplosion" {
    for (dim in 2..20) {
      val curve = DoubleZCurve(dim, 1e-3)
      val center = Point(dim) { 0.5 }
      val range = curve.range(center, sqrt(dim.toDouble()) / 5)
      println("$dim: ${range.size}")
    }
  }
})