
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/Shared/WORK/SAW-BE/saw-transport-service/conf/routes
// @DATE:Fri Aug 18 00:45:57 EDT 2017


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
