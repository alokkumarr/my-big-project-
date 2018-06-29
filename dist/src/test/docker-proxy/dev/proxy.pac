// -*- javascript -*-

function FindProxyForURL(url, host) {
  // Note: The "public_hostname" placeholder is substituted with the
  // public hostname in NGINX request processing
  var proxy = "PROXY %public_hostname%:3128";
  // If public SIP hostname, access it directly
  if (shExpMatch(host, "*.cloud.synchronoss.net")) {
    return "DIRECT";
  }
  // If internal SIP host, use forward proxy to access it
  if (shExpMatch(host, "sip-*")) {
    return proxy;
  }
  if (isInNet(host, "172.16.0.0", "255.240.0.0")) {
    return proxy;
  }
  return "DIRECT";
}
