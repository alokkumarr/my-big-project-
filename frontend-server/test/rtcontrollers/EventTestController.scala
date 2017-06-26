package rtcontrollers

import java.util.concurrent.Callable

import play.mvc.Http
import synchronoss.handlers.countly.CountlyEventHandler

import scala.collection.Seq
import scala.collection.immutable.Map

/**
  * Created by srya0001 on 5/4/2016.
  */
class EventTestController( cch: CountlyEventHandler) extends Callable[play.mvc.Result] {

    var countly_crash_handler = cch

    override def call():  play.mvc.Result = {
      val ctx: Http.Context = Http.Context.current.get
      val query: Map[String, Seq[String]] = ctx._requestHeader().queryString

      val body: Http.RequestBody = ctx.request.body
      val jn = body.asJson()
      countly_crash_handler createMessage (query)

      val r: play.mvc.Result = play.mvc.Results.status(200, "success", "utf8")
      r
    }

}
