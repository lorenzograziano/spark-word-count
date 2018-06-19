package org.spark.wordcount

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import org.spark.wordcount.WordCount.{counts, lines, longerThanFour, words}

object WordCountOrdering extends Ordering[(String, Int)] {

  override def compare(word1: (String, Int), word2: (String, Int)): Int = word2._2 - word1._2

}

object WordCount extends App {

  // We'll run our project in local mode
  val conf = new SparkConf().
    setAppName("word-count").
    setMaster("local[*]")

  // Here we initialize the context with the configuration above
  val sc = new SparkContext(conf)

  // Load the file
  val lines = sc.textFile("src/main/resources/pagerank.txt")

  // Take the lines in the file and split the in individual words
  val words = WordCountFunctions.splitOnRegExp(lines, " ")

  // Keep only words longer than 4
  val longerThanFour = WordCountFunctions.longerThanN(words,4)

  val counts = WordCountFunctions.countWords(longerThanFour)

  val topTen = WordCountFunctions.takeTopN(counts, 10)

  // Print each item in the RDD
  for ((word, count) <- topTen) {
    println(s"$word -> ${Console.GREEN}$count${Console.RESET}")
  }

  // Halt the context before closing the application
  sc.stop()

}

object WordCountFunctions {

  def splitOnRegExp(lines: RDD[String], regexp: String) = {
    lines.flatMap(line => line.split(regexp))
  }

  def longerThanN(wordRDD: RDD[String], length: Int) = {
    wordRDD.filter(word => word.length > length)
  }

  def countWords(words: RDD[String]) = {
    // Couple each word with a "weight" of 1
    val weighted = words.map(word => word -> 1)

    // Sum the weights to couple words with their counts
    weighted.reduceByKey(_ + _)
  }

  def takeTopN(counts: RDD[(String,Int)], value: Int) = {
    // Get only the top n
    counts.takeOrdered(value)(WordCountOrdering)
  }
}