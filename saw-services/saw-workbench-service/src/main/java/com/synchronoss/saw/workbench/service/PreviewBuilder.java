package com.synchronoss.saw.workbench.service;

import com.mapr.db.MapRDB;
import com.mapr.db.Table;
import org.ojai.DocumentBuilder;

/**
 * Workbench preview builder.  Used for creating a MapR-DB document
 * for storing a Workbench preview.
 */ 
public class PreviewBuilder {
    public static final String PREVIEWS_TABLE = "/previews";

    private Table table;
    private DocumentBuilder document;

    /**
     * Create new preview builder with the given preview ID and
     * preview status.
     */
    public PreviewBuilder(String id, String status) {
        table = MapRDB.getTable(PREVIEWS_TABLE);
        document = MapRDB.newDocumentBuilder();
        document.addNewMap();
        document.put("_id", id);
        document.put("status", status);
    }

    /**
     * Get the document builder that allows setting properties in the
     * preview document.
     */
    public DocumentBuilder getDocumentBuilder() {
        return document;
    }

    /**
     * Insert the preview document into the MapR-DB table.
     */
    public void insert() {
        document.endMap();
        table.insertOrReplace(document.getDocument());
        table.flush();
    }
}
