package io.github.olgaruban

import java.io.File

import scala.util.Try
import io.github.olgaruban.util.CloseableResource

/**
  * @author Olga Ruban
  * @version 5/18/16, 16:19
  */
object FlightsStatsApp extends App with CloseableResource {

  Try(new File(super.getClass.getResource("/planes_log.csv.gz").getFile))
    .map { csvFile =>
      using(new FlightInfoCsvReader(csvFile)){ reader =>
        val statsExtractor = new FlightInfoStatsExtractor(reader.iterator.toStream)
        val reporter = new FlightInfoStatsReporter(statsExtractor)
        reporter.generateReports
      }
    } recover { case ex =>
    println("An error occurred during flights statistic reports generation: ")
    ex.printStackTrace()
  }

}
