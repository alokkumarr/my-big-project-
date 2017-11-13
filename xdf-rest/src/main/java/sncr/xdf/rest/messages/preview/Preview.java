package sncr.xdf.rest.messages.preview;

import sncr.xdf.rest.messages.ActorMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Preview extends ActorMessage {

    public String list;
    public final String what;
    public final String dataSource;
    public final String catalog;
    public final String set;

    public Preview (String what, String project, String source, String catalog, String set, String info, String list) {
        super(UUID.randomUUID().toString());

        this.list = null;
        this.what = what;
        this.project = project;
        this.dataSource = source;
        this.catalog = catalog;
        this.set = set;
        this.info = info;
    }

    public Preview (Preview p) {
        super(UUID.randomUUID().toString());
        this.list = p.list;
        this.what = p.what;
        this.project = p.project;
        this.dataSource = p.dataSource;
        this.catalog = p.catalog;
        this.set = p.set;
        this.info = p.info;
    }

    public String toJson(){
        // Supposed to be JSON array of preview items
        return info;
    }

}
