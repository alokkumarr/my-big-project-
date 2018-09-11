package sncr.bda.datasets.conf;

/**
 * Created by srya0001 on 9/18/2017.
 */
public enum DataSetProperties {

        Name("name"),
        PhysicalLocation("physicalLocation"),
        Type("dstype"),
        Format("format"),
        Catalog("catalog"),
        Component("component"),
        StartTS("started"),
        FinishTS("finished"),
        ComponentProducer("transformation"),
        Transformations("transformations"),
        isNewDataSet("isNewDataSet"),
        Exists("exists"),
        Empty("empty"),
        NumberOfFiles("numberOfFiles"),
        MetaCreatedBy("metaCreatedBy"),
        Category("category"),
        SubCategory("subCategory"),
        MetaDescription("metaDescription"),
        Mode("mode"),
        createdBy("createdBy"),
        modifiedBy("modifiedBy"),
        Description("description"),
        UserData("userdata"),
        Id("_id"),
        Status("status"),
        Project("project"),
        BatchID("batchID"),
        User("user"),
        System("system"),
        Schema("schema"),
        RecordCount("recordCount"),
        Sample("sample"),
        Script("script"),
        ScriptLocation("scriptLocation"),
        size("size"),
        PartitionKeys("partitionsKeys"),
        CreatedTime("createdTime"),
        ModifiedTime("modifiedTime");

  private final String intName;

  DataSetProperties(String in) {
    intName = in;
  }

  public String toString() {
    return intName;
  }

}


