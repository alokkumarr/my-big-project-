package sncr.xdf.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asor0002 on 5/10/2017.
 */
public class TableDescriptor {
    public boolean isInDropStatement = false;
    public String tableName;
    public boolean isTempTable = false;
    public boolean isTargetTable = false;
    public int statementIndex = 0;
    public String format;

    public List<Integer> asReference = new ArrayList<>();
    public String mode;
    public int numberOfFiles = 1;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String location = null;

    public TableDescriptor(String tn, boolean isTempTable, int inx, boolean targetTable){
        this.tableName = tn;
        this.isTempTable = isTempTable;
        this.statementIndex = inx;
        this.isTargetTable = targetTable;
    }

    public TableDescriptor(String tn, int inx, boolean dropStatement){
        this.tableName = tn;
        this.statementIndex = inx;
        this.isInDropStatement = dropStatement;
        this.isTempTable = false;
        this.isTargetTable = true;
    }

    public String toString(){
       return
        " { Table name : " + this.tableName + ", REFERENCED IN:  "+ asReference
        + " [temp :" + ((isTempTable)?"Yes":"No") + "],"
        + " [target table: " + ((isTargetTable)?"Yes":"No") + "],"
        + " [index: " + statementIndex + "] }\n";
    }

}
