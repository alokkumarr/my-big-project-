package sncr.xdf.rest.messages.dl;

import sncr.xdf.rest.messages.ActorMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ListOf extends ActorMessage {



    // All members must be immutable
    public final List<String> list;
    public final String listOf;
    public final String dataSource;
    public final String catalog;

    public ListOf (String listOf){
        super(UUID.randomUUID().toString());
        this.listOf = listOf;
        this.list = Collections.unmodifiableList(new ArrayList<>());
        this.project = null;
        this.dataSource = null;
        this.catalog = null;
    }

    public ListOf (String listOf, String project, String datasource, String catalog, List<String> list) {
        super(UUID.randomUUID().toString());
        this.listOf = listOf;
        if(list != null)
            this.list = Collections.unmodifiableList(new ArrayList<>(list));
        else
            this.list = null;
        this.project = project;
        this.dataSource = datasource;
        this.catalog = catalog;

    }

    public ListOf (ListOf o) {
        super(UUID.randomUUID().toString());
        this.listOf = o.listOf;
        if(o.list != null)
            this.list = Collections.unmodifiableList(new ArrayList<>(o.list));
        else
            this.list = null;
        this.project = o.project;
        this.dataSource = o.dataSource;
        this.catalog = o.catalog;
    }

    public String toJson(){
        StringBuilder sb = new StringBuilder();
        String lst = String.join(",", list);
        sb.append("[").append(lst).append("]");
        return sb.toString();
    }
}
