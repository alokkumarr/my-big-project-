package synchronoss.data.countly.model;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by srya0001 on 6/8/2016.
 */

public class CrashTest {

    @Test
    public void testCrashSerialization() {
        String uuid = UUID.randomUUID().toString();
        Crash c = new Crash(uuid);
        c._app_version = "_app_version";
        c._background = "_background";
        c._bat = "_bat";
        c._os = "_os";
        c._manufacture = "_manufacture";
        c._device = "_device";
        c._resolution = "_resolution";
        c._app_version = "_app_version";
        c._cpu = "_cpu";
        c._opengl = "_opengl";
        c._orientation = "_orientation";
        c._error = "_error";
        c._name = "_name";
        c._ram_current = "_ram_current";
        c._ram_total = "_ram_total";
        c._disk_current = "_disk_current";
        c._disk_total = "_disk_total";
        c._bat_current = "_bat_current";
        c._bat_total = "_bat_total";
        c._run = "_run";
        c._online = "_online";
        c._root = null;
        c._muted = null;
        c._nonfatal = "_nonfatal";
        c._logs = "_logs";
        c._os_version = "_os_version";
        int ri = (int) Math.round(Math.random() * 1000);
        c._error_details = new byte[ri];
        Random r = new Random();
        r.nextBytes(c._error_details);

        byte[] serialized = c.getBytes();
        Crash copy = new Crash(serialized);

        assertEquals("_app_version should be the same", c._app_version, copy._app_version);
        assertEquals("_background should be the same", c._background, copy._background);
        assertEquals("_bat should be the same", c._bat, copy._bat);
    }
    @Test
    public void testSignature() throws Exception {
        Event event = new Event();
        event.app_key = "TEST_APP_KEY";
        byte[] serialized = event.getBytes();
        // Put error into serialized buffer
        serialized[0] = (byte)0xFF;

        boolean correctBuffer = true;
        if(!Event.canDeserialize(serialized)){
            try{
                // Exception should be thrown
                new Event(serialized);
            } catch(IllegalArgumentException e){
                correctBuffer = false;
            }
        }
        assertTrue("Incorrect serialized buffer signature has not been recognized.", !correctBuffer);
    }

}
