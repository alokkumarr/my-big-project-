
// @GENERATOR:play-routes-compiler
// @SOURCE:saw-services/saw-transport-service/conf/routes
// @DATE:Tue Oct 16 17:30:43 IST 2018


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
