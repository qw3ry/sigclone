package main.datastructures.curve

import main.datastructures.curve.zcurve.DoubleZCurve
import help.pow
import io.kotlintest.specs.StringSpec

class Analyze : StringSpec({
  val gridSizes = listOf(4)//, 16, 32, 64)
  val curves = listOf<(Int) -> SpaceFillingCurve<Double, Double>> { DoubleZCurve(3, 1.0 / it.pow(3)) }
  val centerPoints = listOf(
    Point(0.5, 0.6, 0.7),
    Point(0.2, 0.7, 0.3)
  )
  val radius = listOf(0.5, 0.2)
  
  fun forEach(fn: (Int, SpaceFillingCurve<Double, Double>) -> Unit) {
    gridSizes.forEach { gridSize ->
      curves.forEach { ctor ->
        fn(gridSize, ctor(gridSize))
      }
    }
  }
  
  fun forEach(fn: (Int, SpaceFillingCurve<Double, Double>, Point<Double>, Double) -> Unit) {
    centerPoints.forEach { center ->
      radius.forEach { r ->
        forEach { gridSize, curve ->
          fn(gridSize, curve, center, r)
        }
      }
    }
  }
  
  "drawCurve" {
    forEach { gridSize: Int, curve: SpaceFillingCurve<Double, Double> ->
      println("${curve.javaClass.simpleName}: image on ${gridSize}x$gridSize grid")
      drawCurve(curve, gridSize)
    }
  }
  "drawRange" {
    forEach { gridSize, curve, center, radius ->
      println("${curve.javaClass.simpleName}: Range ($center, $radius) on ${gridSize}x$gridSize grid")
      drawRange(curve, curve.range(center, radius), gridSize)
    }
  }
})