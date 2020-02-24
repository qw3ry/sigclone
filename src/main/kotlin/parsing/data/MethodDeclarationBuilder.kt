package parsing.data

import java.io.File

class MethodDeclarationBuilder (val file: File) {
  var name: String? = null; private set
  var returnType: String? = null; private set
  val parameters: MutableList<Parameter> = ArrayList()
  val modifiers: MutableList<String> = ArrayList()
  var lines: Pair<Int, Int>? = null
  
  fun build(): MethodDeclaration {
    val name = this.name
    val returnType = this.returnType
    val file = this.file
    val lines = this.lines
    
    require(name != null && returnType != null && lines != null)
    
    return MethodDeclaration(name, returnType, parameters, file, lines, modifiers)
  }
  
  fun setName(name: String?): MethodDeclarationBuilder {
    this.name = name
    return this
  }
  
  fun setReturnType(type: String?): MethodDeclarationBuilder {
    this.returnType = type
    return this
  }
  
  fun setParameters(lambda: (MutableList<Parameter>) -> Unit): MethodDeclarationBuilder {
    lambda.invoke(parameters)
    return this
  }
  
  fun setLines(start: Int, end: Int): MethodDeclarationBuilder {
    this.lines = Pair(start, end)
    return this
  }
  
  fun setLines(lines: Pair<Int, Int>?): MethodDeclarationBuilder {
    this.lines = lines
    return this
  }

  fun setModifiers(lambda: (MutableList<String>) -> Unit): MethodDeclarationBuilder {
    lambda.invoke(modifiers)
    return this
  }
}