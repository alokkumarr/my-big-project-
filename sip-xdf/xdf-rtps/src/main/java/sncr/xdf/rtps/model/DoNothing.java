package sncr.xdf.rtps.model;

import com.google.gson.JsonObject;
import sncr.xdf.rtps.transform.Transform;

/**
 * Created by asor0002 on 10/5/2016.
 */
public class DoNothing implements Transform {
    public void transform(JsonObject src, JsonObject result){

    }
}
