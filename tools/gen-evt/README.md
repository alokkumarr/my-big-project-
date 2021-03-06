### Utility to generate JSON events and send them to RTIS

Shell script `generate-and-send.sh` in this directory creates random
JSON events and send them to RTIS.

Event template is specified in custom record generator file **`custom-recgen.rb`**,
which provides required variables initialisations,
HTTP POST parameters and event JSON structure.

### To run:

1. Edit varaibles initialization in **`custom-recgen.rb`** file:

- **`RTIS_URI`** - `String` - RTIS End point URL, ex `<HOST>:/publishevent`

- **`EVT_GEN`** - event generator function: `() -> Hash`
  This generator is using functions defined in valgen.rb file.

- **`PRM_GEN`** - function `( <event:Hash> ) -> Hash`
  This generator creates hash-table of `POST` parameters,
  it can access event fields.

NB: current generators use global variables
    **`APP_KEY, APP_VERSION, APP_MODULE`**
  They must be updated if required (esp **`APP_KEY`**).

2. Optional: if you like to skip sending to RTIS, set shell env var
   `$ export SKIP_HTTP=Y`
   Messages and request queries will be generated and printed on stdout,
no **HTTP** request will be sent.

3. Execute sh scipt:

```
$ cd <this dir>
$ ./generate-and-send.sh [<number of messages to send>]
```

Script stops if RTIS respond with status other than 200.
Number of messages sent is printed at the end.

#### Sample run with -h key:
```
$ ./generate-and-send.sh -h
[:RTIS_URI, "https://realtime-pac-sip-vaste.sncrcorp.net/publishevent"]
Usage:
[SKIP_HTTP=Y] /media/sf_C_DRIVE/Users/svol0001/prj/svc/git/BDA/sip/tools/gen-evt/gen-n-send.rb <REC_CNT:10>
  env SKIP_HTTP=Y - do not send http, print json only
```

#### Sample run to send 10 events:
```
$ ./generate-and-send.sh 10
[:RTIS_URI, "https://realtime-pac-sip-vaste.sncrcorp.net/publishevent"]
Generate 10 records
[:OK_CNT, 10]
```

#### Sample run to print 2 events
```
$ SKIP_HTTP=Y ./generate-and-send.sh 2
[:RTIS_URI, "https://realtime-pac-sip-vaste.sncrcorp.net/publishevent"]
Generate 2 records
[:SKIP_HTTP, true]
EVT: {"APP_VERSION":"1.0","EVENT_ID":"ee7da99f-67f6-4a15-b8a7-a4542f644c39","EVENT_TYPE":"SALT","@timestamp":"2018-10-19T22:16:23.360Z","path":"G8;y%olAp%^tg&pMkog039XwjyKjkdTWd)b{ikw<M0smlGti<a,k.N/{Jr","payload":["d56a","","4fba9506-c602-4de8-9ae0-078068374bd0","","4461","o-ys","k%n8Wzj","NRjZSPDV","(dMC","ff40b1"]}
RQS: /publishevent?APP_KEY=rtis-samp-client&APP_MODULE=DV&APP_VERSION=1.0&EVENT_ID=ee7da99f-67f6-4a15-b8a7-a4542f644c39&EVENT_DATE=2018-10-19T22%3A16%3A23.360Z&EVENT_TYPE=SALT
EVT: {"APP_VERSION":"1.0","EVENT_ID":"48d8be57-2897-42f5-8f30-6c0cc2b59152","EVENT_TYPE":"SALT","@timestamp":"2018-10-19T22:16:23.361Z","path":".|o*8I.C5%L'eu,tDsyhaM,tx8fpp1dZsIhnlx3c1x4","payload":["e621","","1f783e80-962b-416a-8db7-f26919430c21","","8505","2bpy","RGlhO*vY","zkRTF0R","DtQu","ad90ae"]}
RQS: /publishevent?APP_KEY=rtis-samp-client&APP_MODULE=DV&APP_VERSION=1.0&EVENT_ID=48d8be57-2897-42f5-8f30-6c0cc2b59152&EVENT_DATE=2018-10-19T22%3A16%3A23.361Z&EVENT_TYPE=SALT
```

#### Sample run to pump 10000 events
```
$ for i in $(seq 10); do echo $i; (./generate-and-send.sh 1000 &); done
```

Checking DL files on MAPR box:

```
[mapr@mapr01 raw]$ pwd
/dfs/data/bda/rta-loadtest1/raw
[mapr@mapr01 raw]$ wc -l rta-iot_demo20181101-2246*/*
   4449 rta-iot_demo20181101-224620/part-00000
   5533 rta-iot_demo20181101-224630/part-00000
     18 rta-iot_demo20181101-224640/part-00000
  10000 total
```
