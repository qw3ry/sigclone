package main.runners

import main.output.Formatter
import java.io.File

interface Runner {
  fun parse(sourceDirectory: Collection<File>, splitIdentifier: Boolean)

  fun setFunctions()

  fun evaluate(formatter: Formatter)
  fun loadModel(file: File)
}

