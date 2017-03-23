
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/markus/saw-services/saw-transport-service/conf/routes
// @DATE:Thu Mar 23 01:49:42 EDT 2017


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
