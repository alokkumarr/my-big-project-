package com.synchronoss.saw.store.metastore;

import com.synchronoss.saw.store.base.MetadataStore;

/**
 * Created by srya0001 on 10/31/2017.
 */
public class AuditLogStore extends MetadataStore{

    private static String TABLE_NAME = "auditlog";

    public AuditLogStore(String fsr) throws Exception {
        super(TABLE_NAME, fsr);
    }


}
