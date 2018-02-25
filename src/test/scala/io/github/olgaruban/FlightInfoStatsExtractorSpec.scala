package io.github.olgaruban

import io.github.olgaruban.model.FlightInfo
import org.specs2.mutable.Specification

/**
  * @author Olga Ruban
  * @version 5/18/16, 18:38
  */
class FlightInfoStatsExtractorSpec extends Specification {

  private val testData = Seq(
    FlightInfo(2014, 1, 1, 1, 3, FlightInfo.dateFormat.parseDateTime("2014-01-01"), "JFK", "LAX"),
    FlightInfo(2014, 1, 1, 5, 7, FlightInfo.dateFormat.parseDateTime("2014-01-05"), "JFK", "KBP"),
    FlightInfo(2014, 1, 1, 6, 1, FlightInfo.dateFormat.parseDateTime("2014-01-06"), "KBP", "LAX"),
    FlightInfo(2014, 1, 1, 8, 3, FlightInfo.dateFormat.parseDateTime("2014-01-08"), "JFK", "LAX"),
    FlightInfo(2014, 1, 1, 12, 7, FlightInfo.dateFormat.parseDateTime("2014-01-12"), "JFK", "KBP"),
    FlightInfo(2014, 1, 1, 13, 1, FlightInfo.dateFormat.parseDateTime("2014-01-13"), "KBP", "LAX"),
    FlightInfo(2015, 1, 1, 13, 5, FlightInfo.dateFormat.parseDateTime("2015-01-01"), "JFK", "LAX")
  )

  "A FlightInfoStatsExtractor" should {
    "be able to count total number of arrivals to the airport" in {
      val statsExtractor = new FlightInfoStatsExtractor(testData.toStream)
      val arrivalsByAirport = statsExtractor.countArrivedPlanesByAirport

      arrivalsByAirport should have size 3
      arrivalsByAirport should havePairs("LAX" -> 5, "KBP" -> 2, "JFK" -> 0)
    }

    "be able to count diff between number of arrivals and number of departures to the airport" in {
      val statsExtractor = new FlightInfoStatsExtractor(testData.toStream)
      val balanceByAirport = statsExtractor.countDiffInArrivesAndDeparturesByAirport

      balanceByAirport should have size 2
      balanceByAirport should havePairs("LAX" -> 5, "JFK" -> -5)
    }

    "be able to count total number of arrivals to the airport break down by the weeks" in {
      val statsExtractor = new FlightInfoStatsExtractor(testData.toStream)

      val byWeeks = statsExtractor.countArrivedPlanesByAirportByWeek

      byWeeks should have size 2
      byWeeks should haveKeys(2014, 2015)

      byWeeks(2014) should haveKeys(1, 2, 3)
      byWeeks(2014)(1) should have size 2

      byWeeks(2014)(1) should havePairs("LAX" -> 1, "KBP" -> 1)
      byWeeks(2014)(2) should havePairs("LAX" -> 2, "KBP" -> 1)
      byWeeks(2014)(3) should havePairs("LAX" -> 1)

      byWeeks(2015) should haveKeys(1)
      byWeeks(2015)(1) should have size 1
      byWeeks(2015)(1) should havePairs("LAX" -> 1)
    }

  }

}
