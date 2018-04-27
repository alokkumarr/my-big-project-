import mapr.streaming.{EventHandler, StreamHelper}
import org.scalatest.FlatSpec
import org.scalatest._

import scala.collection.mutable

class TopicSelectorTest extends FlatSpec {
  val stream: Array[mutable.HashMap[String, String]] = new Array[mutable.HashMap[String, String]](10)

  val map1: mutable.HashMap[String, String] = new mutable.HashMap()
  map1.put("a", "b")
  map1.put("c", "d")

  val map2: mutable.HashMap[String, String] = new mutable.HashMap()
  map2.put("p", "q")
  map2.put("r", "s")

  stream :+ map1
  stream :+ map2

  "roundRobinSelector" should "select topics in cyclic fashion" in {
    (assert (EventHandler.roundRobinSelector(stream) == 0))
    (assert (EventHandler.roundRobinSelector(stream) == 1))
    (assert (EventHandler.roundRobinSelector(stream) == 0))
  }

  "roundRobinSelector" should "select topics less than the length of stream" in {
    (assert (EventHandler.roundRobinSelector(stream) < stream.length))
    (assert (EventHandler.roundRobinSelector(stream) < stream.length))
  }

  "randomSelector" should "select topics less than the length of the stream" in {
    (assert (EventHandler.randomSelector(stream) < stream.length))
    (assert (EventHandler.randomSelector(stream) < stream.length))
  }


}