package sncr.xdf.rest;

public class Server {
    public final static String XDF = "xdf";



    public static void main(String args[]){

/*
        String a1 =  System.getProperty("log.dir", "n/processMap");
        String a2 =  System.getProperty("xdf.core", "n/processMap");
        String a3 =  System.getProperty("comp.log.dir", "n/processMap");
        String a4 =  System.getProperty("log4j.configurationFile", "n/processMap");
        System.out.printf("# of args: %d, system variables log.dir: %s, xdf.core: %s, comp.log.dir: %s, log4j.configurationFile: %s\n", args.length, a1, a2, a3, a4);
*/

        // TBD
        // Old version of REST Server
        //if (args != null && args.length == 2 && args[0].equals("manager"))
        //   RequestManager.main(args);
        // New version of REST Server
        if (args != null && args.length == 2 && args[0].equals("server"))
            XDFHttpServer.main(args);
        // Executor JVM
        if (args != null && args.length == 3 && args[0].equals("task"))
            ComponentExecutor.main(args);
    }

}
