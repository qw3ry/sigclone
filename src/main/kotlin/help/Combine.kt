package help

fun <T> combine(t1: T?, t2: T?, combiner: (T, T) -> T): T? {
  return when {
    t1 == null -> t2
    t2 == null -> t1
    else -> combiner.invoke(t1, t2)
  }
}