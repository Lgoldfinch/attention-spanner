package godfinch.industries.utils

import org.scalacheck.Gen

import java.time.{LocalDateTime, ZoneOffset}

object GeneralGenerators {
  def newtypeGen[A, B](gen: Gen[A])(f: A => B): Gen[B] = gen.map(f)

  def localDateTimeGen: Gen[LocalDateTime] = {
    val currentDateTime = LocalDateTime.now()
    val minDateTime = currentDateTime.minusYears(1)
    val maxDateTime = currentDateTime.plusYears(1)

    val minEpochSeconds = minDateTime.toEpochSecond(ZoneOffset.UTC)
    val maxEpochSeconds = maxDateTime.toEpochSecond(ZoneOffset.UTC)

    val epochSecondsGen = Gen.chooseNum(minEpochSeconds, maxEpochSeconds)

    epochSecondsGen.map(epochSeconds =>
      LocalDateTime.ofEpochSecond(epochSeconds, 0, ZoneOffset.UTC)
    )
  }
}
