package main.datastructures.curve

import help.pow
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10

fun drawCurve(curve: SpaceFillingCurve<Double, Double>, gridSize: Int) {
  val noOfCells = gridSize.pow(curve.dimension)
  val digits = ceil(log10(noOfCells.toDouble())).toInt()
  drawCurve(curve, gridSize, digits + 1) {
    "%${digits}d ".format(floor(it * noOfCells).toInt())
  }
}

private fun drawGrid(gridSize: Int, cellSize: Int, cellFn: (Point<Int>) -> String): List<String> {
  val out = mutableListOf<String>()
  val startLine = "─".repeat(gridSize * cellSize)
  out.add("┌$startLine┐")
  
  for (y in 0 until gridSize) {
    val sb = StringBuilder()
    for (x in 0 until gridSize) {
      sb.append(cellFn(Point(x, y)))
    }
    out.add("│$sb│")
  }
  
  out.add("└$startLine┘")
  return out
}

private fun drawCurve(curve: SpaceFillingCurve<Double, Double>, gridSize: Int, cellSize: Int, cellFn: (Double) -> String) {
  assert(curve.dimension >= 2)
  
  val grids = mutableListOf<List<String>>()
  
  val spareDimension = curve.dimension - 2
  for (i in 0 until gridSize.pow(spareDimension)) {
    grids.add(drawGrid(gridSize, cellSize) { point ->
      // extend point to curve dimension
      val extendedPoint = Point(curve.dimension) {
        when {
          it >= spareDimension -> point[it - spareDimension]
          else -> (i / gridSize.pow(it)) % gridSize
        }
      }
      val curvePoint = curve.toCurvePoint(Point(curve.dimension) { extendedPoint[it].toDouble() / gridSize })
      cellFn(curvePoint)
    })
  }
  
  for (y in 0..gridSize + 1) {
    for (grid in grids) {
      print(grid[y])
    }
    println()
  }
}

fun drawRange(curve: SpaceFillingCurve<Double, Double>, range: List<Pair<Double, Double>>, gridSize: Int) {
  drawCurve(curve, gridSize, 2) { point ->
    if (range.any { point >= it.first && point < it.second }) "██" else "  "
  }
}