package help

import org.apfloat.Apfloat
import kotlin.math.pow

fun Int.pow(exponent: Int) = toDouble().pow(exponent).toInt()

operator fun Apfloat.plus(other: Apfloat): Apfloat = add(other)

operator fun Apfloat.times(other: Apfloat): Apfloat = multiply(other)