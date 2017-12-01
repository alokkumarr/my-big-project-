package sncr.xdf.metastore;

import sncr.xdf.base.MetadataStore;
import sncr.xdf.base.WithSearchInMetastore;

/**
 * Created by srya0001 on 12/1/2017.
 */
public class ProjectService extends MetadataStore implements WithSearchInMetastore{

    private static String TABLE_NAME = "projects";

    protected ProjectService(String altRoot) throws Exception {
        super(TABLE_NAME, altRoot);
    }
}
