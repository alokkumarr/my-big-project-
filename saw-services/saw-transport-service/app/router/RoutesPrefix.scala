
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/pman0003/Codebase/bda/saw-1335/saw/saw-services/saw-transport-service/conf/routes
// @DATE:Sat Dec 23 13:01:38 IST 2017


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
