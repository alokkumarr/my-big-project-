
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/pman0003/Codebase/bda/saw-feature/saw/saw-services/saw-transport-service/conf/routes
// @DATE:Thu Feb 15 13:04:46 IST 2018


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
