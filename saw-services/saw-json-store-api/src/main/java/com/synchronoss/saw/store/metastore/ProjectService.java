package com.synchronoss.saw.store.metastore;

import com.synchronoss.saw.store.base.MetadataStore;
import com.synchronoss.saw.store.base.WithSearchInMetastore;

/**
 * Created by srya0001 on 12/1/2017.
 */
public class ProjectService extends MetadataStore implements WithSearchInMetastore{

    private static String TABLE_NAME = "projects";

    protected ProjectService(String altRoot) throws Exception {
        super(TABLE_NAME, altRoot);
    }
}
