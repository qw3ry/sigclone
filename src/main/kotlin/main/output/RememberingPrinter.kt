package main.output

import java.io.OutputStream
import java.io.PrintStream

class RememberingPrinter(os: OutputStream, private val verbose: Boolean) : PrintStream(os) {
  private val stringBuffer = StringBuffer()

  override fun println(x: String?) {
    if (verbose) stringBuffer.append(x + "\n")
    super.println(x)
  }

  override fun println() {
    println("")
  }

  fun printAgain() {
    super.println()
    super.println(stringBuffer.toString())
  }
}