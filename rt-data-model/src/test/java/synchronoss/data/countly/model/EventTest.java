package synchronoss.data.countly.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alexey.sorokin on 5/11/2016.
 */
public class EventTest {

    @Test
    public void testSerialization() throws Exception {
        Event e = new Event();
        e.app_key = "TEST_APP_KEY";
        byte[] serialized = e.getBytes();
        Event copy = new Event(serialized);

        assertEquals("APP KEY should be the same", e.app_key, copy.app_key);
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