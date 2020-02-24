package help

import java.io.File
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files

fun readFileToText(file: File): String {
  val throwable = mutableListOf<Throwable>()
  for (charset in listOf("ISO-8859-1", "UTF-8")) {
    try {
      val retval = Files.readAllLines(file.toPath(), Charset.forName(charset)).joinToString(separator = "\n")
      // strip the byte order mark, if present
      return if (retval.startsWith("\uFEFF")) retval.substring(1) else retval
    } catch (t: Throwable) {
      throwable.add(t)
      System.err.println("Error on File $file with charset $charset")
    }
  }
  val ex = Exception("Could not parse $file")
  throwable.forEach { ex.addSuppressed(it) };
  throw ex
}