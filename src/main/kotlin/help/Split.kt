package help

import org.apache.commons.lang3.StringUtils

private val regex = Regex(pattern = "^\\p{IsAlphabetic}+$")

fun String.split(): List<String> =
  StringUtils.splitByCharacterTypeCamelCase(this).filter { it.matches(regex) }.map { it.trim().toLowerCase() }