
// @GENERATOR:play-routes-compiler
// @SOURCE:C:/projects/BDA/RTIS-logstash/frontend-server/conf/routes
// @DATE:Wed Dec 06 11:51:19 EST 2017


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
