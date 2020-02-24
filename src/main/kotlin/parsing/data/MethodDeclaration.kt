package parsing.data

import data.Signature
import data.splitAndAdd
import org.apache.commons.collections4.bag.HashBag
import java.io.File

data class MethodDeclaration(
    val name: String,
    val returnType: String,
    val parameters: Collection<Parameter>,
    val file: File,
    val lines: Pair<Int, Int>,
    val modifiers: Collection<String>
)

fun MethodDeclaration.toSignature(splitIdentifier: Boolean): Signature {
  val identifier = HashBag<String>()
  val paramTypes = HashBag<String>()
  val paramNames = HashBag<String>()
  identifier.splitAndAdd(name, splitIdentifier)
  for (param in parameters) {
    paramTypes.add(param.type)
    if (param.name != null)
      paramNames.splitAndAdd(param.name, splitIdentifier)
  }
  val ret = if (returnType.isBlank()) null else returnType
  return Signature(ret, identifier, paramTypes, paramNames, file, lines)
}