package io.github.olgaruban.model

import org.joda.time.DateTime
import org.specs2.mutable.Specification

import scala.util.Random

/**
  * @author Olga Ruban
  * @version 5/18/16, 15:43
  */

class FlightInfoSpec extends Specification {
  def it = "A FlightInfo"

  def flightInfo(
                  year: Int = DateTime.now().getYear,
                  quarter: Int = Random.nextInt(4) + 1,
                  month: Int = Random.nextInt(12) + 1,
                  day: Int = Random.nextInt(31) + 1,
                  dayOfWeek: Int = Random.nextInt(7) + 1,
                  flightDate: DateTime = DateTime.now(),
                  origin: String = Random.alphanumeric.filter(!_.isDigit).take(3).mkString.toUpperCase,
                  destination: String = Random.alphanumeric.filter(!_.isDigit).take(3).mkString.toUpperCase
                ) = FlightInfo(year, quarter, month, day, dayOfWeek, flightDate, origin, destination)

  it should {
    "not allow to create an instance of object if year/quarter/month are out of bounds" in {
      flightInfo(quarter = 5) should throwA[IllegalArgumentException]("Quarter should be in \\[1,4\\]")
      flightInfo(month = 13) should throwA[IllegalArgumentException]
      flightInfo(day = 32) should throwA[IllegalArgumentException]
      flightInfo(dayOfWeek = 0) should throwA[IllegalArgumentException]
    }

    "not allow to create an instance of object when flight date is before first flight (in the history)" in {
      val anyDateBeforeFirstFlight = 1902
      flightInfo(flightDate = DateTime.now().withYear(anyDateBeforeFirstFlight)) should throwAn[IllegalArgumentException]
    }
  }

  it can {
    "be created with valid year/quarter/month arguments" in {
      val flight = flightInfo(
        quarter = 1,
        month = 12,
        day = 25,
        dayOfWeek = 7
      )

      flight.quarter must be equalTo 1
      flight.month must be equalTo 12
      flight.day must be equalTo 25
      flight.dayOfWeek must be equalTo 7
    }
  }
}
