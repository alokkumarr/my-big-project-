import mapr.streaming.{EventHandler, StreamHelper}
import org.scalatest.FlatSpec

/**
  * Created by srya0001 on 10/22/2016.
  */
class StreamHelperTest extends FlatSpec{

  EventHandler.buildEventHandlerList

  "loadActiveStreams" should "create default stream mapping if file is not present and create file" in {
    StreamHelper.loadActiveStreams
    StreamHelper.printStreamMap
    StreamHelper.dumpActiveStreams

  }

  "loadActiveStreams" should "load stream mapping from file" in {
    StreamHelper.loadActiveStreams
    StreamHelper.printStreamMap
  }


}
