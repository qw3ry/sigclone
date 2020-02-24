package main.datastructures.curve

data class PartialRange<T>(
  val startPoint: Double,
  val isStartOpen: Boolean = false,
  val isEndOpen: Boolean = false,
  val range: List<Pair<T, T>> = listOf()
) {

  val rangeStart = 0.0

  operator fun plus(other: PartialRange<T>): PartialRange<T> {
    return when {
      other.startPoint < startPoint -> other + this
      !isEndOpen || !other.isStartOpen -> PartialRange(
        startPoint,
        isStartOpen,
        other.isEndOpen,
        range + other.range
      )
      else -> PartialRange(
        startPoint,
        isStartOpen,
        other.isEndOpen,
        range.dropLast(1) + Pair(range.last().first, other.range.first().second) + other.range.drop(1)
      )
    }
  }
}