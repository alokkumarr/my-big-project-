
// @GENERATOR:play-routes-compiler
// @SOURCE:C:/projects/real-time-analytics/frontend-server/conf/routes
// @DATE:Thu Oct 20 10:21:37 EDT 2016


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
