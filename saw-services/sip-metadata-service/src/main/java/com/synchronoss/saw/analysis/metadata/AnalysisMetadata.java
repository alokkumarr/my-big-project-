package com.synchronoss.saw.analysis.metadata;

import sncr.bda.base.DocumentConverter;
import sncr.bda.base.MetadataStore;
import sncr.bda.base.WithSearchInMetastore;

public class AnalysisMetadata extends MetadataStore
    implements WithSearchInMetastore, DocumentConverter {

  public AnalysisMetadata(String tableName, String baseTableLocation) throws Exception {
    super(tableName, baseTableLocation);
  }
}
