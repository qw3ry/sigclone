package parsing.data

class ParameterBuilder {
  var name: String? = null
  var type: String? = null
  
  fun isValid(): Boolean {
    return name != null && type != null
  }
  
  fun build(): Parameter {
    val name = this.name
    val type = this.type
    
    if (name == null || type == null) {
      throw IllegalArgumentException()
    }
    
    return Parameter(name, type)
  }
  
  fun setName(name: String?): ParameterBuilder {
    this.name = name
    return this
  }
  
  fun setType(type: String?): ParameterBuilder {
    this.type = type
    return this
  }
}