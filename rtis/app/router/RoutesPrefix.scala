
// @GENERATOR:play-routes-compiler
// @SOURCE:C:/projects/BDA/RTIS-logstash/frontend-server/conf/routes
// @DATE:Fri Oct 12 15:19:21 EDT 2018


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
