package io.github.olgaruban

import java.io.File

import io.github.olgaruban.FlightInfoStatsReporter.Report
import org.mockito.Mockito._
import org.specs2.matcher.{ContentMatchers, FileMatchers}
import org.specs2.mock._
import org.specs2.mutable.{After, Specification}

/**
  * @author Olga Ruban
  * @version 5/19/16, 17:38
  */
class FlightInfoStatsReporterSpec extends Specification
  with FileMatchers
  with ContentMatchers
  with Mockito
  with After {

  val reportTestDir = new File("test_reports").getAbsolutePath

  def after = {
    for {
      files <- Option(new File(reportTestDir).listFiles)
      file <- files
    } file.delete()
  }

  def statsMock() = {
    val statsMock = mock[FlightInfoStatsExtractor]

    when(statsMock.countArrivedPlanesByAirport)
      .thenReturn(Map(
        "KBP" -> 20,
        "IEV" -> 3
      ))

    when(statsMock.countDiffInArrivesAndDeparturesByAirport)
      .thenReturn(Map(
        "KBP" -> -4,
        "IEV" -> 8
      ))

    when(statsMock.countArrivedPlanesByAirportByWeek)
      .thenReturn(Map(
        2014 -> Map(
          1 -> Map(
            "KBP" -> 20,
            "IEV" -> 3
          )
        )
      ))

    statsMock
  }

  "A FlightInfoStatsReporter" should {
    "be able to generate arrivals report" in {
      val mock = statsMock()
      val reporter = new FlightInfoStatsReporter(mock, Option(reportTestDir), Iterable(Report.ArrivalStats))

      reporter.generateReports
      verify(mock).countArrivedPlanesByAirport

      val reportFile = new File(reportTestDir, "arrivals_report.txt")

      reportFile should exist
      reportFile must haveSameLinesAs(
        Seq(
          "1. KBP 20",
          "2. IEV 3"
        )
      )
    }

    "be able to generate airport balance report" in {
      val mock = statsMock()
      val reporter = new FlightInfoStatsReporter(mock, Option(reportTestDir))

      reporter.reportArrivalDepartureBalance
      verify(mock).countDiffInArrivesAndDeparturesByAirport

      val reportFile = new File(reportTestDir, "arrivals_departures_balance_report.txt")

      reportFile should exist
      reportFile must haveSameLinesAs(
        Seq(
          "1. IEV 8",
          "2. KBP -4"
        )
      )
    }

    "be able to generate airport arrivals report broken down by weeks" in {
      val mock = statsMock()
      val reporter = new FlightInfoStatsReporter(mock, Option(reportTestDir))

      reporter.reportArrivalStatsByWeek
      verify(mock).countArrivedPlanesByAirportByWeek

      val reportFile = new File(reportTestDir, "arrivals_by_week_report.txt")

      reportFile should exist
      reportFile must haveSameLinesAs(
        """Year 2014:
          |	W1:
          |		1. KBP   20
          |		2. IEV   3""".stripMargin.split("\n").toSeq
      )
    }

    "be able to generate all reports at once" in {
      val mock = statsMock()
      val reporter = new FlightInfoStatsReporter(mock, Option(reportTestDir))

      reporter.generateReports

      new File(reportTestDir, "arrivals_report.txt") should exist
      new File(reportTestDir, "arrivals_departures_balance_report.txt") should exist
      new File(reportTestDir, "arrivals_by_week_report.txt") should exist

    }
  }

}
