/**
 * Copyright 2014 Reverb Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import play.api.mvc._
import play.api.Logger
import play.api.libs.iteratee.Enumerator
import play.modules.swagger.ApiListingCache
import javax.xml.bind.annotation._
import java.io.StringWriter

import akka.util.ByteString
import io.swagger.annotations.Api
import io.swagger.util.Json
import io.swagger.models.Swagger
import io.swagger.core.filter.SpecFilter
import io.swagger.config.FilterFactory

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import play.api.http.HttpEntity._

object ErrorResponse {
  val ERROR = 1
  val WARNING = 2
  val INFO = 3
  val OK = 4
  val TOO_BUSY = 5
}

class ErrorResponse(@XmlElement var code: Int, @XmlElement var message: String) {
  def this() = this(0, null)

  @XmlTransient
  def getCode: Int = code

  def setCode(code: Int) = this.code = code

  def getType: String = code match {
    case ErrorResponse.ERROR => "error"
    case ErrorResponse.WARNING => "warning"
    case ErrorResponse.INFO => "info"
    case ErrorResponse.OK => "ok"
    case ErrorResponse.TOO_BUSY => "too busy"
    case _ => "unknown"
  }

  def setType(`type`: String) = {}

  def getMessage: String = message

  def setMessage(message: String) = this.message = message
}

@Api( hidden = true )
class ApiHelpController extends SwaggerBaseApiController {

  def getResources = Action {
    request =>
      implicit val requestHeader: RequestHeader = request
      
      
      
      
      val host = request.headers.get("X-Forwarded-Host").orNull
      
        Logger(" Host :").debug(host)
        Logger(" Host :").info(host)
       
       
        
      val resourceListing = getResourceListing(host)

//      debugPrint(resourceListing)

      val responseStr = returnXml(request) match {
        case true => toXmlString(resourceListing)
        case false => toJsonString(resourceListing)
      }
      returnValue(request, responseStr)
  }

  def getResource(path: String) = Action {
    request =>
      implicit val requestHeader: RequestHeader = request
      val host = request.headers.get("Host").orNull
       Logger(" Host :").debug(host)
        Logger(" Host :").info(host)
      val apiListing = getApiListing(path, host)
      val responseStr = returnXml(request) match {
        case true => toXmlString(apiListing)
        case false => toJsonString(apiListing)
      }
      Option(responseStr) match {
        case Some(help) => returnValue(request, help)
        case None =>
          val msg = new ErrorResponse(500, "api listing for path " + path + " not found")
          Logger("swagger").error(msg.message)
          if (returnXml(request)) {
            InternalServerError.chunked(Enumerator(toXmlString(msg).getBytes("UTF-8"))).as("application/xml")
          } else {
            InternalServerError.chunked(Enumerator(toJsonString(msg).getBytes("UTF-8"))).as("application/json")
          }
      }
  }
  
  def viewSwaggerUI() = Action {
    val swaggerDocStream: java.io.InputStream =
      this.getClass().getResourceAsStream("/public/swagger-ui/index.html")
    val docs = scala.io.Source.fromInputStream(swaggerDocStream).mkString("")
      Ok(docs) as HTML
  }
}



class SwaggerBaseApiController extends Controller {

  protected def returnXml(request: Request[_]) = request.path.contains(".xml")

  protected val AccessControlAllowOrigin = ("Access-Control-Allow-Origin", "*")

  /**
   * Get a list of all top level resources
   */
  protected def getResourceListing(host: String)(implicit requestHeader: RequestHeader) = {
    Logger("swagger").debug("ApiHelpInventory.getRootResources")
    val docRoot = ""
    val queryParams = (for((key, value) <- requestHeader.queryString) yield {
      (key, value.toList.asJava)
    })
    val cookies = (for(cookie <- requestHeader.cookies) yield {
      (cookie.name, cookie.value)
    }).toMap
    val headers = (for((key, value) <- requestHeader.headers.toMap) yield {
      (key, value.toList.asJava)
    })

    val f = new SpecFilter
    val l: Option[Swagger] = ApiListingCache.listing(docRoot, host)

    val specs: Swagger = l match {
      case Some(m) => m
      case _ => new Swagger()
    }
/*
    val hasFilter = Option(FilterFactory.getFilter)
    hasFilter match {
      case Some(filter) => f.filter(specs, FilterFactory.getFilter, queryParams.asJava, cookies, headers)
      case None => specs
    }
*/
    specs
  }

  /**
   * Get detailed API/models for a given resource
   */
  protected def getApiListing(resourceName: String, host: String)(implicit requestHeader: RequestHeader) = {
    Logger("swagger").debug("ApiHelpInventory.getResource(%s)".format(resourceName))
    val docRoot = ""
    val f = new SpecFilter
    val queryParams = requestHeader.queryString.map {case (key, value) => key -> value.toList.asJava}
    val cookies = requestHeader.cookies.map {cookie => cookie.name -> cookie.value}.toMap.asJava
    val headers = requestHeader.headers.toMap.map {case (key, value) => key -> value.toList.asJava}
    val pathPart = resourceName

    val l: Option[Swagger] = ApiListingCache.listing(docRoot, host)
    val specs: Swagger = l match {
      case Some(m) => m
      case _ => new Swagger()
    }
    val hasFilter = Option(FilterFactory.getFilter)

    val clone = hasFilter match {
      case Some(filter) => f.filter(specs, FilterFactory.getFilter, queryParams.asJava, cookies, headers)
      case None => specs
    }
    clone.setPaths(clone.getPaths.filterKeys(_.startsWith(pathPart) ))
    clone
  }

  def toXmlString(data: Any): String = {
    if (data.getClass.equals(classOf[String])) {
      data.asInstanceOf[String]
    } else {
      val stringWriter = new StringWriter()
      stringWriter.toString
    }
  }

  protected def XmlResponse(data: Any) = {
    val xmlValue = toXmlString(data)
    val xmlBytes = ByteString(xmlValue.getBytes("UTF-8"))
    val header = ResponseHeader(200, Map(CONTENT_LENGTH -> xmlBytes.length.toString))
    Result (header, new Strict(xmlBytes,Option("application/xml")))
  }

  protected def returnValue(request: Request[_], obj: Any): Result = {
//    Logger("swagger").debug("Result (Any)" + obj )
    val response = returnXml(request) match {
      case true => XmlResponse(obj)
      case false => JsonResponse(obj)
    }
    response.withHeaders(AccessControlAllowOrigin)
  }

  def toJsonString(data: Any): String = {
    if (data.getClass.equals(classOf[String])) {
      data.asInstanceOf[String]
    } else {
      Json.pretty(data.asInstanceOf[AnyRef])
    }
  }

  protected def JsonResponse(data: Any) = {
    val jsonValue = toJsonString(data)
    val jsonBytes = ByteString(jsonValue.getBytes("UTF-8"))
    val header = ResponseHeader(200, Map(CONTENT_LENGTH -> jsonBytes.length.toString))
    Result (header, new Strict(jsonBytes,Option("application/json")))
  }


  def debugPrint( resourceListing : Swagger ) : Unit = {
    Logger("swagger").debug("Print cached data")
    import scala.collection.JavaConversions._
    resourceListing.getPaths.keySet().foreach(k => {
      val p = resourceListing.getPaths.get(k)
      Logger("swagger").debug(s"Path ${k}")
      p.getOperations.foreach(op => {
        Logger("swagger").debug(s"Operation: ${op.getOperationId}, Description: ${op.getDescription}")
      })
    })
    Logger("swagger").debug(s"Info: ${resourceListing.getInfo.getDescription}")
  }
}
