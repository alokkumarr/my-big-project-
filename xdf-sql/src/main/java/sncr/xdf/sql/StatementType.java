package sncr.xdf.sql;

public enum StatementType {

    DROP_TABLE("Drop table"),
    UNKNOWN("Unknown type"),
    SELECT("Plain select"),
    CREATE("Create Table As Select");

    StatementType(String s) {
        subtitle = s;
    }

    String subtitle;

}
