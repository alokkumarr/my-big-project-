
// @GENERATOR:play-routes-compiler
// @SOURCE:sip/saw-services/saw-transport-service/conf/routes
// @DATE:Wed Jul 04 14:27:39 IST 2018


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
