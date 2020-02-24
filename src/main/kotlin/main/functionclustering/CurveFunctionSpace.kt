package main.functionclustering

import data.Signature
import main.datastructures.btree.BTreeImpl
import main.datastructures.curve.zcurve.BigDecimalZCurve
import help.plus
import org.apfloat.Apfloat
import java.math.BigDecimal
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class CurveFunctionSpace {
  private val precision = Apfloat(BigDecimal(1).movePointLeft(500))

  private val btree = BTreeImpl<Apfloat>()
  private val lock = ReentrantReadWriteLock(false)

  private val lookup = HashMap<Signature, Apfloat>()

  private lateinit var sig2points: SignaturesToPoints

  fun setFunctions(signatures: Collection<Signature>) {
    lock.write {
      sig2points = SignaturesToPoints(signatures)

      for (signature in signatures) {
        val point = sig2points.signatureToPoint(signature)
        val pos = BigDecimalZCurve(sig2points.dimension, precision).toCurvePoint(point)
        lookup[signature] = pos
        btree.put(pos, signature)
      }
    }
  }

  fun getNeighbors(signature: Signature): Collection<Signature> {
    lock.read {
      require(::sig2points.isInitialized)
      val curvePoint = lookup[signature] ?: {
        val point = sig2points.signatureToPoint(signature)
        BigDecimalZCurve(sig2points.dimension, precision).toCurvePoint(point)
      }()
      return btree.scan(curvePoint, curvePoint + precision)
    }
  }
}
