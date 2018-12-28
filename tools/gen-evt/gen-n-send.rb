#!/usr/bin/env ruby

require_relative 'valgen'
require 'uri'

## Public:
#rtis_url = "https://rtis-sncrbda-poc.synchronoss.net"
## Internal:
#rtis_url = "http://10.48.72.75:9100"

## Load record and params geration methods
require_relative 'custom-recgen'
###
# This file must define 3 custom vars for RTIS events processing:
# RTIS_URI - String - RTIS End point URL, ex <HOST>:/publishevent
# EVT_GEN - event generator function: () -> Hash
# PRM_GEN - function ( <event:Hash> ) -> Hash
###

## Default for RTIS_URI
# SIP PAC (172.31.216.79)
RTIS_URI ||= "https://realtime-pac-sip-vaste.sncrcorp.net/publishevent"
# curl -sS https://realtime-pac-sip-vaste.sncrcorp.net/sr

#####
p [:RTIS_URI, RTIS_URI]
uri = URI(RTIS_URI)

# Check Global vars defined
unless EVT_GEN and PRM_GEN
    warn "EVT_GEN not defined" unless EVT_GEN
    warn "PRM_GEN not defined" unless PRM_GEN
    exit(1)
end
#
def gen_evt()    EVT_GEN.call      end
def gen_prm(evt) PRM_GEN.call(evt) end
1||(
  require 'pp'
  evt = gen_evt()
  pp(:EVENT, evt)
  rqp = gen_prm(evt)
  pp(:PARAMS, rqp)
  rqs = URI.encode_www_form(rqp)
  puts("QUERY:", rqs)
  exit
)

if ARGV[0] == '-h'
    puts "Usage:"
    puts "[SKIP_HTTP=Y] #{$0} <REC_CNT:10>"
    puts "  env SKIP_HTTP=Y - do not send http, print json only"
    exit
end

REC_CNT=[(ARGV[0]||1).to_i, 1].max
puts "Generate #{REC_CNT} records"

SKIP_HTTP = %r/^[yY1]/.match(ENV['SKIP_HTTP']||"") && true
p [:SKIP_HTTP, SKIP_HTTP] if SKIP_HTTP

# Ruby HTTP Post With JSON Request + SSL + Certificate + Basic Authentication
# https://www.antanosolar.com/ruby-http-post-with-json-request-ssl-certificate-basic-authentication/
##
# HTTP Posts in Ruby
# https://coderwall.com/p/c-mu-a/http-posts-in-ruby
##

http = nil
header = { "Content-Type" => "text/json" }

unless SKIP_HTTP
  require 'net/http'
  require 'openssl'
  http = Net::HTTP.new(uri.host, uri.port)
  if uri.scheme == "https"
      http.use_ssl = true
      #http.verify_mode = OpenSSL::SSL::VERIFY_PEER
      http.verify_mode = OpenSSL::SSL::VERIFY_NONE
      #http.ca_file = "equifax_ca.crt"
  end
end # unless SKIP_HTTP

ok_cnt = 0
REC_CNT.times{
    # Generate next event object (Hash)
    evt = gen_evt()
    # Event JSON to send
    ejs = evt.to_json

    # POST params to pass (Hash)
    rqp = gen_prm(evt)
    # Request uri string
    rqs = URI.encode_www_form(rqp)
    uri.query = rqs

    if SKIP_HTTP
        puts "EVT: #{ejs}"
        puts "RQS: #{uri.request_uri}"
        next
    end

    request = Net::HTTP::Post.new(uri, initheader=header)
    response = http.request(request, ejs)

    if response.code == '200'
      ok_cnt += 1
      next
    end

    p [:STATUS, response.code]
    p [:RESP, response]
    p [:BODY, response.body]
    break
}
#p http
http.finish if http && http.started?

p [:OK_CNT, ok_cnt] unless SKIP_HTTP
exit

# In PAC env:
# MapR Stream: /bda/data/streams/rta-loadtest1:t
# DL: /data/bda/rta-loadtest1/raw
