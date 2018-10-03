
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/nareshgangishetty/swagger-work/sip/rtis/conf/routes
// @DATE:Wed Oct 03 15:10:59 EDT 2018


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
