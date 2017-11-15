package sncr.xdf.rest;

public class Server {
    public final static String XDF = "xdf";



    public static void main(String args[]){

        // New version of REST Server
        if (args != null && args.length == 2 && args[0].equals("server"))
            XDFHttpServer.main(args);
        // Executor JVM
        if (args != null && args.length >= 3 && args[0].equals("task"))
            ComponentExecutor.main(args);
    }

}
