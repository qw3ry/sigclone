package main.output

import data.Signature

class BigCloneEvalFormatter : Formatter {
  override fun formatMatch(sig1: Signature, sig2: Signature): String {
    return if (sig1 < sig2) formatMatch(sig2, sig1) else "${sig1.format()},${sig2.format()}"
  }
  
  private fun Signature.format(): String {
    return "${file.parentFile.name},${file.name},${lines.first},${lines.second}"
  }
}

private operator fun Signature.compareTo(sig2: Signature): Int {
  return Comparator.comparing<Signature, String> { it.file.name }
    .thenComparing(Comparator.comparingInt<Signature> { it.lines.first }.reversed())
    .compare(this, sig2)
}
