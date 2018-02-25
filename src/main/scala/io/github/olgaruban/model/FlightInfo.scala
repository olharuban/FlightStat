package io.github.olgaruban.model

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
  * @author Olga Ruban
  * @version 5/18/16, 14:23
  */
object FlightInfo {

  val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd")

}

case class FlightInfo(
                       year: Int,
                       quarter: Int,
                       month: Int,
                       day: Int,
                       dayOfWeek: Int,
                       flightDate: DateTime,
                       origin: String,
                       destination: String
                     ) {
  private val dateOfFirstFlight = DateTime.now()
    .withYear(1903)
    .withMonthOfYear(12)
    .withDayOfMonth(17)
    .withTimeAtStartOfDay()

  require(year >= dateOfFirstFlight.getYear, "Year should be greater, than the year of first flight in the history of humanity")
  require(quarter > 0 && quarter <= 4, "Quarter should be in [1,4]")
  require(month > 0 && month <= 12, "Month should be in [1,12]")
  require(day > 0 && day <= 31, "Day should be in [1,31]")
  require(dayOfWeek > 0 && dayOfWeek <= 7, "Day of week should be in [1,7]")
  require(flightDate != null && flightDate.isAfter(dateOfFirstFlight), "Flight date should be after the date of first flight in the history of humanity")
}


