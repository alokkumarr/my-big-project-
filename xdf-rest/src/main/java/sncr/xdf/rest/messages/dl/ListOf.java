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
    public final String category;
    public final String subCategory;


    public ListOf (String listOf){
        super(UUID.randomUUID().toString());
        this.listOf = listOf;
        this.list = Collections.unmodifiableList(new ArrayList<>());
        this.project = null;
        this.dataSource = null;
        this.catalog = null;
        this.subCategory = null;
        this.category = null;
    }

    public ListOf (String listOf, String project, String datasource, String catalog, String category, String subCategory, List<String> list) {
        super(UUID.randomUUID().toString());
        this.listOf = listOf;
        if(list != null)
            this.list = Collections.unmodifiableList(new ArrayList<>(list));
        else
            this.list = null;
        this.project = project;
        this.dataSource = datasource;
        this.catalog = catalog;
        this.subCategory = subCategory;
        this.category = category;
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
        this.category = o.category;
        this.subCategory = o.subCategory;
    }

    public String toJson(){
        StringBuilder sb = new StringBuilder();
        String lst = String.join(",", list);
        sb.append("[").append(lst).append("]");
        return sb.toString();
    }

    public String toString(){
        String lst = " List: \n";
        if (list != null && !list.isEmpty() )
            lst += String.join(",", list);
        else
            lst += " empty ";
        return
        "List type: " + listOf + ", Project: " + project +
        ", DataSource: " + ((dataSource != null)? dataSource: "n/a") +
        ", Catalog: " + ((catalog != null)? catalog: "n/a") +
        ", Category: " + ((category != null)? category: "n/a") +
        ", Sub Category: " + ((subCategory != null)? subCategory+ "\n": "n/a\n") + lst;
    }

}
