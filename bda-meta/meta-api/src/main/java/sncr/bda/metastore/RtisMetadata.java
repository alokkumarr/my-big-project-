package sncr.bda.metastore;

import org.apache.log4j.Logger;
import sncr.bda.base.DocumentConverter;
import sncr.bda.base.MetadataStore;
import sncr.bda.base.WithSearchInMetastore;

public class RtisMetadata extends
    MetadataStore implements WithSearchInMetastore, DocumentConverter {

  private static final Logger logger = Logger.getLogger(RtisMetadata.class);

  public RtisMetadata(String tableName, String baseTableLocation) throws Exception {
    super(tableName, baseTableLocation);
  }
}