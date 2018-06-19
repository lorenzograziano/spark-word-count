import com.holdenkarau.spark.testing.SharedSparkContext
import org.apache.spark.rdd.RDD
import org.scalatest.FunSuite
import org.spark.wordcount.WordCountFunctions

class WordCountTest extends FunSuite with SharedSparkContext {
  test("Take the lines and split in individual words") {
    val lines = sc.parallelize(Seq("Hello world","The World is beautiful", "The lif is so nice"))
    val words: RDD[String] = WordCountFunctions.splitOnRegExp(lines, " ")
    assert(words.collect().length === 11)
  }

  test("Keep only words longer than a value") {
    val words = sc.parallelize(Seq("Hello","Nice","World","Is"))
    val longerThanFour: RDD[String] = WordCountFunctions.longerThanN(words, 4)
    assert(longerThanFour.collect().length === 2)
    assert(longerThanFour.collect() === Seq("Hello","World"))
  }

  test("Count word occurrences") {
    val words = sc.parallelize(Seq("Hello","Nice","World","Hello"))
    val counts = WordCountFunctions.countWords(words)
    assert(counts.collectAsMap() === Map("Hello" -> 2, "Nice" -> 1, "World" -> 1))
  }

  test("Take the top n word with best count") {
    val wordcount = sc.parallelize(Seq("Hello" -> 3,"Nice" -> 1 ,"World" -> 4,"Moon" -> 2))
    val topThree = WordCountFunctions.takeTopN(wordcount, 3)
    assert(topThree === Array("World" -> 4, "Hello" -> 3, "Moon" -> 2))
  }
}
