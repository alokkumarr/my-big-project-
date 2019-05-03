package model

case class ClientException(message: String) extends RuntimeException(message)
