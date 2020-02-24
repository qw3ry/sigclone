package main.datastructures.curve

interface SpaceFillingCurve<T : Number, S : Number> {
  val dimension: Int
  fun toCurvePoint(point: IPoint<T>): S

  fun range(center: IPoint<T>, radius: T): List<Pair<S,S>>
}