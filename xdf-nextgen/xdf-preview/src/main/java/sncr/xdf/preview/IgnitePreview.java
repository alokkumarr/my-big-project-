package sncr.xdf.preview;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;

public class IgnitePreview {
    public static void main(String[] args){
        Ignition.setClientMode(true);
        // Start Ignite in client mode.
        Ignite ignite = Ignition.start();

        ignite.close();
    }


}
