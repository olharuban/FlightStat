package io.github.olgaruban

import java.io.File

import io.github.olgaruban.model.FlightInfo
import io.github.olgaruban.util.CloseableResource
import org.specs2.mutable.Specification

import scala.util.Random

/**
  * @author Olga Ruban
  * @version 5/18/16, 14:30
  */
class FlightInfoCsvReaderSpec extends Specification with CloseableResource {

  val fileName = "/planes_log.csv.gz"
  val testFile = new File(classOf[FlightInfoCsvReaderSpec].getResource(fileName).getFile)

  "A FlightInfoCsvReader" should {
    "produce an exception if file is null or doesn't exist" in {
      new FlightInfoCsvReader(null) should throwAn[IllegalArgumentException]

      val fileName = Random.alphanumeric.take(10).mkString
      new FlightInfoCsvReader(new File(fileName)) should throwAn[IllegalArgumentException]

    }

    "be able to read GZipped CSV file and produce Stream of FlightInfo objects" in {
      using(new FlightInfoCsvReader(testFile)) { parser =>
        parser.iterator must beAnInstanceOf[Iterator[FlightInfo]]

        val expectedFileData = {
            FlightInfo(2014, 1, 1, 1, 3, FlightInfo.dateFormat.parseDateTime("2014-01-01"), "JFK", "LAX") ::
            FlightInfo(2014, 1, 1, 2, 4, FlightInfo.dateFormat.parseDateTime("2014-01-02"), "JFK", "LAX") ::
            FlightInfo(2014, 1, 1, 3, 5, FlightInfo.dateFormat.parseDateTime("2014-01-03"), "JFK", "LAX") :: Nil
        }

        val result = new FlightInfoCsvReader(testFile).toList
        result foreach println
        result should have size 3
        result must be equalTo expectedFileData
      }
    }

  }
}
