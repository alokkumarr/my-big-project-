# Introduction

This is Sycnhronoss Real Time Ingestion Service(RTIS) source code
repository. It provides source code, documentation and tools to build
and deply various RTIS services

#   Development



##  Run in Local:

 1. Create applciation.conf file in conf folder by copying contents of
    applicaiton.conf.dev file
 2. Make sure application.conf file has **play.crypto.secret** value set. 
    Value should be generated key and should be enclosed in quotes
 3. Do maven build: **mvn clean install**
 4. Create a new run configuration with conf folder in class path and run
    **play.core.server.ProdServerStart**
    
 5. Once server starts access [localhost:9100](http://localhost:9100) and
    the output should be "Your new application is ready”




    
## Errors:
 
 1. This application is already running (Or delete \<path\>/RUNNING_PID file):
    
    Simply remove RUNNING_PID file. 
    
      ``` $  rm RUNNING_PID ```


    
 2. Configuration Error:  
    Make sure conf file is in classpath and secret key is set. 
    Also make sure maven build is successful.



    
    
 
## Local changes:
 
 1. Download sbt to compile and generate routes:
     
      ```  $  brew install sbt ```


 2. Implement changes. 
    Ex: add new route in conf/routes file and update controller class
 3. Delete all generated files inside app folder
 
		* Controllers/javascript folder and contents
		* ReverseRoutes.scala
		* Routes.java
		* Router folder and contents

 4. Open terminal and make sure in rtis folder and compile : sbt compile  
	 Note: If you encounter multiple imports of ‘RoutesCompiler’ 
	 then comment first import line in build.sbt
 
 5. New files with chagnes are gneerated into below folders 
      rtis/target/scala-2.1.1/routes/controllers, 
      rtis/target/scala-2.1.1/routes/router  
      
 6. Copy generated files into app folder accordingly.
 
    Router folder in app/router
    controllers in app/controllers
 
 
 7. Launch the application following “Run in Local” section and you can see 
    changes reflected.


 
 ## Troubleshooting:
    
 Running in local makes sure that code is working till sending message. However 
 if we want to make sure message is properly sent and recieved by rtps and
 writing parquet/json format, then below inormtion is useful to troubleshoot.
  
  
 We have one  Dev server  and multiple  Mapr nodes.  Environment details can be
 found at
 [confluence](https://confluence.synchronoss.net:8443/pages/viewpage.action?pageId=177065278) 
 
 
 1.Make sure  RTIS configuration on dev server has same stream name, 
  topic name as with which you are trying to publish.You can verify at
  ```/opt/bda/rtis/conf/application.conf```


 2.Make sure subscribers on MapR nodes are configured with same stream name
  and topic  names with which messages are published on RTIS.

   Topic subscriber configuration can be found at
   ```/dfs/opt/bda/apps/rta-iot_demo/conf/rta-iot_demo.conf```

 3.Invoke /events Ex: using
   [swagger](https://realtime-rd-sip-vaste.sncrcorp.net/docs)
   with valid query params and payload

 4.Check RTIS log file to make sure messages are sent without any errors at
   ``` /var/bda/rtis/log/app_<date>.log```

 5.If all configurations are as expected, then you can see a parquet/json 
   file written as per configuration.
     Ex: /dfs/data/bda/rta-iot_demo/raw/\<file\>  
