package main.output

import data.Signature

interface Formatter {
  fun formatMatch(sig1: Signature, sig2: Signature): String
}