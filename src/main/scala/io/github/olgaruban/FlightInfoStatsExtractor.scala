package io.github.olgaruban

import io.github.olgaruban.model.FlightInfo

/**
  * @author Olga Ruban
  * @version 5/18/16, 16:34
  */
class FlightInfoStatsExtractor(private val flightInfoRecords: Stream[FlightInfo]) {

  implicit class AirportStatisticRich(s1: AirportStatistic) {
    def +(s2: AirportStatistic) = (s1._1 + s2._1, s1._2 + s2._2)
  }

  type AirportStatistic = (Int, Int)

  private lazy val _stats: Map[Int, Map[Int, Map[String, AirportStatistic]]] = {

    def calculateAirportStats(flights: Seq[FlightInfo]): Map[String, AirportStatistic] = {

      def calculateAirportStatsInternal(flights: Seq[FlightInfo], acc: List[(String, AirportStatistic)]): List[(String, AirportStatistic)] = {
        if (flights.isEmpty)
          acc
        else {
          val flight = flights.head
          calculateAirportStatsInternal(flights.tail, (flight.destination, (1, 0)) ::(flight.origin, (0, 1)) :: acc)
        }
      }

      calculateAirportStatsInternal(flights, List())
        .groupBy(_._1)
        .mapValues(_.map(_._2).foldLeft(0, 0)(_ + _))
    }

    val groupedByYear = flightInfoRecords.toList.groupBy(_.year)

    groupedByYear.map { case (year, flights) =>
      val groupedByWeeks = flights.groupBy(_.flightDate.getWeekOfWeekyear)
      (year, groupedByWeeks)
    } mapValues {
      _.map { case (week, flights) =>
        (week, calculateAirportStats(flights))
      }
    }

  }

  private def countStatisticByAirport: Map[String, AirportStatistic] = {
    _stats.values.flatMap(_.values).flatMap(_.toList).groupBy(_._1)
      .mapValues(_.map(_._2)).mapValues(_.foldLeft(0, 0)(_ + _))
  }

  def countArrivedPlanesByAirportByWeek: Map[Int, Map[Int, Map[String, Int]]] =
    _stats.mapValues(_.mapValues(_.mapValues(_._1).filterNot(_._2 == 0)))

  def countArrivedPlanesByAirport: Map[String, Int] = {
    countStatisticByAirport.mapValues(_._1)
  }

  def countDiffInArrivesAndDeparturesByAirport: Map[String, Int] = {
    countStatisticByAirport.mapValues { case (arrivals, departures) =>
      arrivals - departures
    }.filterNot(_._2 == 0)
  }

}
