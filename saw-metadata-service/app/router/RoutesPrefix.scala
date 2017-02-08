
// @GENERATOR:play-routes-compiler
// @SOURCE:C:/projects/BDA/bda-middle-tier/bda-transport-service/conf/routes
// @DATE:Thu Feb 02 14:24:46 EST 2017


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
