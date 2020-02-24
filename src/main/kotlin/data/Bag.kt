package data

import help.split
import org.apache.commons.collections4.Bag
import org.apache.commons.collections4.bag.HashBag
import kotlin.reflect.KFunction1

fun Bag<String>.splitAndAdd(string: String, splitIdentifier: Boolean) =
    if (splitIdentifier)
      addAll(string.split())
    else
      add(string.trim().toLowerCase())


fun <E> Bag<E>.mapBag(mapping: KFunction1<E, Int>): Bag<Int> {
  return HashBag(map(mapping))
}