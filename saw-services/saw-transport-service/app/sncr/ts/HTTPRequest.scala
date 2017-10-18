package sncr.ts

/**
  * Created by srya0001 on 5/8/2016.
  */

import org.apache.http.impl.nio.client.{CloseableHttpAsyncClient, HttpAsyncClients}
import sncr.request.TSResponse


class HTTPRequest extends TSResponse
{
  implicit val httpClient : CloseableHttpAsyncClient = HttpAsyncClients.createDefault()
  implicit val format = org.json4s.DefaultFormats
}

