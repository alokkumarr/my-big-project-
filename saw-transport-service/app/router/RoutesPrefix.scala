
// @GENERATOR:play-routes-compiler
// @SOURCE:C:/projects/SAW/saw-services/saw-transport-service/conf/routes
// @DATE:Wed Mar 01 11:53:47 EST 2017


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
