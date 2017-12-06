package synchronoss.data.countly.model;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexey.sorokin on 5/5/2016.
 */
public  class SerializationHelper {

    private static byte DOUBLE_QUOTE = (byte)34;

    public static void put(ByteBuffer output, byte[] value){
        if(value != null && value.length > 0) {
            output.putInt(value.length);
            output.put(value);
        } else output.putInt(0);
    }

    public static String getString(ByteBuffer input){

        int stringSize = input.getInt();
        if(stringSize > 0){
            // Non null value
            byte[] byteValue = new byte[stringSize];
            input.get(byteValue);
            int start = 0;
            int length = byteValue.length;
            if(byteValue[0] == DOUBLE_QUOTE) {
                start++;
                length--;
            }
            if(byteValue[byteValue.length - 1] == DOUBLE_QUOTE) length--;

            return new String(byteValue, start, length);
        } else
            return null;
    }

    public static ByteBuffer serializeMap(Map<String, String> map){
        ByteBuffer retval = null;
        if(map != null) {
            int mapSize = map.size();
            int mapDataSize = 0;
            if(mapSize  > 0){
                for(Map.Entry<String, String> entry : map.entrySet()){
                    mapDataSize += (entry.getKey() == null ? 0 : entry.getKey().getBytes().length) + 4;
                    mapDataSize += (entry.getValue() == null ? 0 : entry.getValue().getBytes().length) + 4;
                }
            }

            int size = 4 /* number of map entries */ + mapDataSize;
            retval = ByteBuffer.allocate(size);
            retval.putInt(mapSize);
            for(Map.Entry<String, String> entry : map.entrySet()){
                put(retval, entry.getKey() == null ? null : entry.getKey().getBytes());
                put(retval, entry.getValue() == null ? null : entry.getValue().getBytes());
            }
        }
        return retval;
    }

    public static Map<String, String> getMap(ByteBuffer input){
        Map<String, String> map = new HashMap<>();
        int numberOfMapEntries = input.getInt();
        for(int i = 0; i < numberOfMapEntries; i++){
            String key = getString(input);
            String value = getString(input);
            map.put(key, value);
        }
        return map;
    }

    public static void put(ByteBuffer output, boolean value){
        output.put(value ? (byte)0x01 : (byte)0x00);
    }

    public static boolean getBoolean(ByteBuffer input){
        byte b = input.get();
        return b == 0x01;
    }

    public static int getSerializedSize(String s) {
        return s == null ? 4 : (s.length() + 4);
    }

    public static int getSerializedSize(byte[] s) {
        return s == null ? 4 : (s.length + 4);
    }

    public static int getSerializedSize(int s) {
        return 4;
    }

    public static int getSerializedSize(boolean s) {
        return 1;
    }

    public static byte[] getBytes(String s){
        return s == null ? null : s.getBytes();
    }
}
