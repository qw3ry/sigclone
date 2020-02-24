package parsing.data

data class Parameter(
    val name: String? /* nullable because of implicit `this` */,
    val type: String)