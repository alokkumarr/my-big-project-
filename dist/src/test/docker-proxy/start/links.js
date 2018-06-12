/*
 * Workaround: Previously links on the start page were in certain
 * cases hardcoded to localhost, as links using different ports or
 * protocols must include the hostname.  So use a JavaScript
 * workaround to make links that also work when deployed to a remote
 * host, such as with Docker Machine.
 */
window.onload = function () {
  var links = document.links;
  for (var i = 0; i < links.length; i++) {
    var link = links[i]
    var protocolAttr = link.getAttribute("protocol");
    var portAttr = link.getAttribute("port");
    var pathAttr = link.getAttribute("path");
    if (protocolAttr || portAttr || pathAttr) {
      var protocol = (protocolAttr || "http") + "://"
      var port = portAttr ? (":" + portAttr) : ""
      var path = pathAttr || ""
      link.href = protocol + window.location.hostname + port + path
    }
  }
}
