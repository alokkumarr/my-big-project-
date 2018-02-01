package com.synchronoss.saw.store.metastore;

import java.util.Map;

import org.ojai.Document;
import org.ojai.store.QueryCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synchronoss.saw.store.base.MetadataStore;
import com.synchronoss.saw.store.base.WithSearchInMetastore;

/**
 * Created by @author spau0004 on 12/12/2017.
 */
public class PortalDataSetStore extends MetadataStore implements WithSearchInMetastore{

  private static final Logger logger = LoggerFactory.getLogger(PortalDataSetStore.class);
    private static String TABLE_NAME = "portalDataSetStore";

    public PortalDataSetStore(String altXDFRoot) throws Exception {
        super(TABLE_NAME, altXDFRoot);
    }

    public Map<String, Document> search(QueryCondition qc) throws Exception {
        logger.trace("Search query on search " + qc.toString());
        return searchAsMap(table, qc);
    }

}
