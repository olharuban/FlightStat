package io.github.olgaruban

import java.io.{File, FileWriter}

import io.github.olgaruban.FlightInfoStatsReporter.Report
import io.github.olgaruban.FlightInfoStatsReporter.Report.Report

/**
  * @author Olga Ruban
  * @version 5/18/16, 20:07
  */
object FlightInfoStatsReporter {

  object Report extends Enumeration {
    type Report = Value
    val ArrivalStats, ArrivalDepartureBalance, ArrivalStatsByWeek = Value
  }

  val defaultBaseDir = new File("reports").getAbsolutePath
}

class FlightInfoStatsReporter(
                               statsExtractor: FlightInfoStatsExtractor,
                               baseDir: Option[String] = None,
                               enabledReports: Iterable[Report] = Report.values) {

  import FlightInfoStatsReporter._

  def reportArrivalStats = {
    val arrived = statsExtractor.countArrivedPlanesByAirport
    writeToFile(
      baseDir.getOrElse(defaultBaseDir),
      "arrivals_report.txt",
      arrived.toList.sortBy(_._2).reverse.zipWithIndex.map { case ((airportCode, arr), line) =>
        s"${line + 1}. $airportCode $arr"
      }.toStream
    )
  }

  def reportArrivalDepartureBalance = {
    val diff = statsExtractor.countDiffInArrivesAndDeparturesByAirport
    writeToFile(
      baseDir.getOrElse(defaultBaseDir),
      "arrivals_departures_balance_report.txt",
      diff.toList.sortBy(_._1).zipWithIndex.map { case ((airportCode, diff), line) =>
        s"${line + 1}. $airportCode $diff"
      }.toStream
    )
  }

  def reportArrivalStatsByWeek = {
    val stats = statsExtractor.countArrivedPlanesByAirportByWeek
    val sortedStats =
      stats.toList.sortBy(_._1).map { case (year, yearStats) =>
      (year, yearStats.toList.sortBy(_._1).map { case (week, weekStats) =>
        (week, weekStats.toList.sortBy(_._2) reverse)
      })
    }

    writeToFile(
      baseDir.getOrElse(defaultBaseDir),
      "arrivals_by_week_report.txt",
      sortedStats.toStream.flatMap { case (year, yearStats) =>
          Stream(s"Year $year:") ++
          yearStats.flatMap { case (week, weekStats) =>
            Stream(s"\tW$week:") ++
              weekStats.zipWithIndex.map { case ((airportCode, arr), line) =>
                f"\t\t${line + 1}. $airportCode%-5s $arr"
              }.toStream
          }
      }
    )
  }

  def generateReports = enabledReports.foreach {
    case Report.ArrivalStats => reportArrivalStats
    case Report.ArrivalDepartureBalance => reportArrivalDepartureBalance
    case Report.ArrivalStatsByWeek => reportArrivalStatsByWeek
  }

  private def writeToFile(baseDir: String, filename: String, contents: Stream[String]) = {
    new File(baseDir).mkdirs()
    val writer = new FileWriter(new File(baseDir, filename))
    try {
      contents.foreach { line =>
        writer.write(line)
        writer.write("\n")
      }
    } finally {
      writer.close()
    }
  }
}
