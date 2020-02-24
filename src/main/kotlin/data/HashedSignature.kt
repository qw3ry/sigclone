package data

import org.apache.commons.collections4.Bag

class HashedSignature(
    val signature: Signature
) {
  val returnType: Int? = signature.returnType?.hashCode()
  val identifier: Bag<Int> = signature.identifier.mapBag(String::hashCode)
  val paramTypes: Bag<Int> = signature.paramTypes.mapBag(String::hashCode)
  val paramIdentifiers: Bag<Int> = signature.paramIdentifiers.mapBag(String::hashCode)

  override fun toString() = signature.toString()
}
