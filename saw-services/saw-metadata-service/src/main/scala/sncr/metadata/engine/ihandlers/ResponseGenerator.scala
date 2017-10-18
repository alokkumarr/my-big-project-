package sncr.metadata.engine.ihandlers

import java.io.OutputStream

import org.json4s.JValue
import org.json4s.native.JsonMethods._
import sncr.metadata.engine.responses.Response

/**
  * Created by srya0001 on 3/20/2017.
  */
class ResponseGenerator(val outStream: OutputStream) extends Response  {

  def generate(value: List[JValue]) =
  {
    var i :Int = 0
    val response =
      value.foldLeft[StringBuilder](new StringBuilder)( (s,v) =>
        { i += 1
          val rendered = s"\n\nResponse on work-item: $i => ${pretty(render(v))}"
//          val rendered = s"\n\nResponse on work-item: $i => ${v.toString}"
          m_log debug rendered
          s.append(rendered)})

    outStream.write(response.toString().getBytes())
    outStream.flush()
    outStream.close()
  }

}
