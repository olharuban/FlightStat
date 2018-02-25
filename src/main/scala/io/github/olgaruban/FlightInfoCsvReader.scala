package io.github.olgaruban

import java.io._
import java.util.zip.GZIPInputStream

import io.github.olgaruban.model.FlightInfo
import org.apache.commons.csv.CSVFormat
import org.joda.time.format.DateTimeFormat

import scala.collection.JavaConverters
import scala.util.{Failure, Success, Try}

/**
  * @author Olga Ruban
  * @version 5/18/16, 14:43
  */
object FlightInfoCsvReader {
  private val YEAR = "YEAR"
  private val QUARTER = "QUARTER"
  private val MONTH = "MONTH"
  private val DAY_OF_MONTH = "DAY_OF_MONTH"
  private val DAY_OF_WEEK = "DAY_OF_WEEK"
  private val FL_DATE = "FL_DATE"
  private val ORIGIN = "ORIGIN"
  private val DEST = "DEST"
  private val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd")
}

class FlightInfoCsvReader(gZippedFile: File) extends Closeable with Iterable[FlightInfo] {
  import FlightInfoCsvReader._

  require(gZippedFile != null, "File can not be null")
  require(gZippedFile.exists(), "Input file should exist")

  private val reader = new BufferedReader(
    new InputStreamReader(new GZIPInputStream(new FileInputStream(gZippedFile)))
  )
  private val parser = CSVFormat.DEFAULT.withHeader().parse(reader)

  def iterator =
    JavaConverters.asScalaIteratorConverter(parser.iterator()).asScala
      .map(csvRecord =>
        FlightInfo(
          csvRecord.get(YEAR).toInt,
          csvRecord.get(QUARTER).toInt,
          csvRecord.get(MONTH).toInt,
          csvRecord.get(DAY_OF_MONTH).toInt,
          csvRecord.get(DAY_OF_WEEK).toInt,
          dateFormat.parseDateTime(csvRecord.get(FL_DATE)),
          csvRecord.get(ORIGIN),
          csvRecord.get(DEST)
        )
      )

  def close() = {
    Option(reader).foreach(_.close())
    Option(parser).foreach(_.close())
  }
}
