package main.datastructures.curve

class Point<T : Number>(private val pos: List<T>) : IPoint<T> {
  constructor(vararg pos: T) : this(listOf(*pos))
  constructor(dimension: Int, valueFunction: (Int) -> T) : this(
    List(dimension) { valueFunction(it) }
  )
  
  override val dimension: Int
    get() = pos.size
  
  override fun get(dim: Int) = pos[dim]
  
  override fun toString() = pos.joinToString(",", "(", ")", transform = Any::toString)
}