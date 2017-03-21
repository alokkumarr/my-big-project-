
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/markus/saw-services/saw-transport-service/conf/routes
// @DATE:Tue Mar 21 06:52:20 EDT 2017


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
