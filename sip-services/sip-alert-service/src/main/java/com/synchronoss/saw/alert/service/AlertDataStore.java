package com.synchronoss.saw.alert.service;

import sncr.bda.base.DocumentConverter;
import sncr.bda.base.MetadataStore;
import sncr.bda.base.WithSearchInMetastore;

public class AlertDataStore extends MetadataStore
    implements WithSearchInMetastore, DocumentConverter {

  public AlertDataStore(String tableName, String basePath) throws Exception {
    super(tableName, basePath);
  }
}
