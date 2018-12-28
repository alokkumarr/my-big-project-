#!/usr/bin/env ruby
require 'json'
require 'securerandom'
require 'stringio'
require 'time'

class String
    # Common prefix
    def ^(s)
        mi = 0
        [size,s.size].min.times{|i|
            break unless slice(i)==s.slice(i)
            mi=i+1
        }
        slice(0,mi)
    end
end
#

########################################
# Value Generators
##
# Const
def const_gen(mi,*_ign)
    return ->{ mi }
end
1||(
    gf = const_gen(123,"Bla")
    puts( VAL: gf[] )
    exit
)

def ival_gen(mi,ma,c=nil)
    mi=mi.to_i
    ma=ma.to_i-mi
    if c==String
        ->{ String(mi+rand(ma)) }
    else
        ->{ mi+rand(ma) }
    end
end
1||(
fs=ival_gen("-5","0",String)
fi=ival_gen("0","5")
10.times{ puts( { sval: fs[], ival: fi[]}.to_json ); }
exit
)

def fval_gen(mi,ma,c=nil)
    ssi=mi.to_s.split(".",2)
    ssa=ma.to_s.split(".",2)
    dsz=ssi[1].size-ssa[1].size
    unless dsz.zero?
        if dsz<0
            ssi[1]<<"0"*(-dsz)
        else
            ssa[1]<<"0"*dsz
        end
    end
    n = ssa[1].size
    f = 1.0; n.times{ f*=10 }
    imi = (ssi[0]+ssi[1]).to_i
    ima = (ssa[0]+ssa[1]).to_i-imi
    if c==String
        ->{ String(((imi+rand(ima))/f).round(n)) }
    else
        ->{ ((imi+rand(ima))/f).round(n) }
    end
end
#f=fval_gen("0.0003","1.1",String);10.times{ p f[] };exit
1||(
fs=fval_gen("-5.00","0.00",String)
fi=fval_gen("0.0","5.000")
10.times{ puts( { sval: fs[], ival: fi[]}.to_json ); }
exit
)

def sval_gen(mi,ma,*_ign)
    mi=mi.to_s
    ma=ma.to_s
    scom=mi^ma
    slen=mi.size-scom.size
    ->{ scom + SecureRandom.hex((slen+1)/2).slice(0,slen) }
end
1||(
fa=sval_gen("ABC/123","ABC/WXYZ")
fb=sval_gen("12345","")
10.times{ puts( sa: fa[], sb: fb[] ); }
exit
)

#
ABC_LC=("a".."z").to_a.join
ABC_UC=("A".."Z").to_a.join
DIG_10="0123456789"
SCHARS=("!".."~").to_a.join+ABC_LC+ABC_LC+ABC_UC+DIG_10
def rnd_str(sz)
  s=StringIO.new
  sz.times{
    s<<SCHARS[rand(SCHARS.size)]
  }
  s.string
  #SecureRandom.random_bytes(sz).each_byte.map{ |b|
  #  SCHARS[ b%SCHARS.size ]
  #}.join
end
1||(
  p [:test, :rnd_str]
  10.times{
    puts( rnd_str(20) )
  }
  exit
)
def str_gen(mi,len=0)
  ->{ rnd_str(mi+rand(len+1)) }
end
1||(
  p [:test, :str_gen]
  10.times{
    s = str_gen(5,5).call
    p [s, s.size]
  }
  exit
)
def hex_gen(mi,len=0)
  ->{ sz=mi+rand(len+1);SecureRandom.hex((sz+1)/2).slice(0,sz) }
end
1||(
  p [:test, :hex_gen]
  10.times{
    s = hex_gen(5,5).call
    p [s, s.size]
  }
  exit
)
#
def tval_gen(mi=nil,ma=nil,c=nil)
    # UTC seconds with fraction
    f = ->{ Time.now.utc.to_f.round(3) }
    if c==String
        ->{ String(f.call) }
    elsif c==Fixnum
        # milliseconds
        ->{ ((f.call)*1000).to_i }
    else
        # Float
        f
    end
end
1||(
fs=tval_gen("1530557277","0.5",String)
ft=tval_gen("0.0","0.1")
10.times{ puts( { sval: fs[], tval: ft[]}.to_json ); }
exit
)

# 36 chars UUID
def uuid_gen()
    ->{ SecureRandom.uuid() }
end
#p uuid_gen().call; exit

# UTC time in iso8601(3) format
def time_gen()
    ->{ Time.now.utc.iso8601(3) }
end

# return random one-of
def one_of_gen( *val_funcs )
  ->{ val_funcs[rand(val_funcs.size)].call }
end
# pcode: { key: val_func }
def rec_gen(pcode)
  ->{ pcode.map{|key,val_func| [key, val_func.call] }.to_h }
end
# pcode: [ val_func, .. ]
def arr_gen(pcode)
  ->{ pcode.map{|val_func| val_func.call } }
end

###
1||(
f = rec_gen(
  "APP_VERSION": const_gen("0.3-100915-gac7c9b2"),
  "EVENT_ID": uuid_gen(),
  "EVENT_TYPE": one_of_gen(const_gen("KPI"),const_gen("SALT"),const_gen("ACCESS")),
  "@timestamp": time_gen(),
  "path": str_gen(40,20),
  # "@timestamp": time_gen(),
  # "RECEIVED_TS": time_gen(),
  # "APP_MODULE": const_gen("DV"),
  # "EVENT_DATE": time_gen(),
  # "APP_KEY": const_gen("PC.BT"),
  # "UID": uuid_gen()
  "payload": arr_gen( [
    str_gen(4,4),
    const_gen(""),
    uuid_gen(),
    const_gen(""),
    str_gen(4,4),
    str_gen(4,4),
    str_gen(4,4),
    str_gen(4,4),
    str_gen(4,4),
    str_gen(4,4)
    ] )
  )
obj = f.call
require 'pp'
pp obj
)
