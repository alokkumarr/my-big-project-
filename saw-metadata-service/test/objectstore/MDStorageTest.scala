package objectstore

import org.scalatest.FlatSpec
import sncr.metadata.objectstore.NodeStore

/**
  * Created by srya0001 on 2/1/2017.
  */
class MDStorageTest  extends FlatSpec{

  "sr interface" should "return short status report" in {

    val os = new NodeStore("content")

    val compositeKey = "Test:Test"
    println(compositeKey)

  }


}
