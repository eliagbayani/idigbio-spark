import org.scalatest._
import DateUtil._

class DateUtil$Test extends FlatSpec with Matchers {

  "a date with year and month" should "be valid" in {
    validDate("1965-1") should be(true)
  }

  "a date with year and month no hyphen" should "be valid" in {
    validDate("19650101") should be(true)
  }

  "a date interval with year and month" should "be valid" in {
    validDate("1965-1/1970") should be(true)
  }

  "a start date with year and month" should "parse using UTC" in {
    startDate("1965-1") should be(-157766400000L)
  }

  "a start date no hyphen" should "parse using UTC" in {
    basicDateToUnixTime("20150616") should be(1434412800000L)
    startDate("2015-06-16") should be(1434412800000L)
  }

  "an end date with year and month" should "parse using UTC" in {
    endDate("1965-1") should be(-157766400000L)
  }

  "start of date interval with year and month" should "parse using UTC" in {
    startDate("1965-1/1970") should be(-157766400000L)
  }

  "end of date interval with year and month" should "parse using UTC" in {
    endDate("1965-1/1970") should be(0L)
  }

  "valid date" should "be valid" in {
    DateUtil.validDate("2016-01-1") should be(true)
  }

  "valid date range" should "be valid" in {
    DateUtil.validDate("2016-01-10/2016-01-20") should be(true)
  }

  "invalid date" should "be not valid" in {
    DateUtil.validDate("boo 2016-01-1") should be(false)
  }

  "one month digit date" should "be valid" in {
    DateUtil.validDate("2014-7-23") should be(true)
  }

  "inaturalist date" should "be valid" in {
    DateUtil.validDate("2014-10-31T14:27:00-07:00") should be(true)
    DateUtil.validDate("20150605") should be(true)
  }

  "ebird-gbif date" should "be valid" in {
    DateUtil.validDate("2014-06-29T02:00Z") should be(true)
  }

  "first published" should "select oldest occurrence" in {
    val older = Occurrence("11.4", "12.2", "Animalia|Aves", "2013-01-01", "some id", "20150605", "some data source")
    val younger = Occurrence("11.4", "12.2", "Animalia|Aves", "2013-01-01", "some id", "20150616", "some data source")
    DateUtil.selectFirstPublished(older, younger) should be(older)
  }



  "config2string" should "be a json object" in {


  }


}
