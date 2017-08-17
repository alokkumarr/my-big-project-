
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/Shared/WORK/SAW-BE/saw-transport-service/conf/routes
// @DATE:Thu Aug 17 11:45:50 EDT 2017


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
