package sncr.xdf.rtps;

import com.google.gson.JsonObject;

/**
 * Created by asor0002 on 10/5/2016.
 */
public interface Transform {
    public void transform(JsonObject src, JsonObject result);

}
