package main.runners

import main.runners.cosine.CosineW2vRunner
import main.runners.euclid.EuclidW2vRunner
import main.runners.euclid.NaiveRunner
import main.runners.rwd.RwdRunner

enum class RunnerType {
  RWD,
  EUCLID_NAIVE,
  EUCLID_W2V,
  EUCLID_W2V_SFC,
  COSINE_W2V
}

fun createRunner(type: RunnerType) =
    when (type) {
      RunnerType.RWD -> ::RwdRunner
      RunnerType.EUCLID_NAIVE -> ::NaiveRunner
      RunnerType.EUCLID_W2V -> ::EuclidW2vRunner
      RunnerType.EUCLID_W2V_SFC -> TODO()
      RunnerType.COSINE_W2V -> ::CosineW2vRunner
    }