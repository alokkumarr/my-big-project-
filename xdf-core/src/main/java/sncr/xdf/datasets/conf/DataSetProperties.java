package sncr.xdf.datasets.conf;

import javax.jws.soap.InitParam;
import java.util.HashMap;

/**
 * Created by srya0001 on 9/18/2017.
 */
public enum DataSetProperties {

    Name("name"),
    PhysicalLocation ("physicalLocation"),
    Type ("type"),
    Format ("format"),
    Catalog ("catalog"),
    StartTS ("started"),
    FinishTS ("finished"),
    ComponentProducer ("transformation"),
    Transformations ("transformations"),
    isNewDataSet ("isNewDataSet"),
    Exists ("exists"),
    Empty ("empty"),
    NumberOfFiles ("numberOfFiles"),
    MetaCreatedBy ("metaCreatedBy"),
    Category("category"),
    SubCategory("subCategory"),
    MetaDescription ("metaDescription"),
    Mode("mode"),
    Creator("createdBy"),
    Description ("description"),
    UserData("userData"),
    Id ("_id"),
    Status ("status"),
    Project ( "project"),
    BatchID ("batchID"),
    User("user"),
    System("system");

    private final String intName;

    DataSetProperties(String in){
        intName = in;
    }

    public String toString(){
        return intName;
    }

}


