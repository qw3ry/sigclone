package data

import org.apache.commons.collections4.Bag
import java.io.File

data class Signature(
  val returnType: String?,
  val identifier: Bag<String>,
  val paramTypes: Bag<String>,
  val paramIdentifiers: Bag<String>,
  val file: File,
  val lines: Pair<Int, Int>
) {
  override fun toString(): String {
    return "${formatBag(identifier)} ( ${formatBag(paramIdentifiers)} : ${formatBag(paramTypes)} ): ${returnType?:"Unit"}"
  }
  
  private fun formatBag(bag: Bag<String>): String {
    return bag.sorted().filter{ it.isNotEmpty() }.map{it[0].toUpperCase() + it.substring(1)}.joinToString(separator = " ") { it }
  }
}
