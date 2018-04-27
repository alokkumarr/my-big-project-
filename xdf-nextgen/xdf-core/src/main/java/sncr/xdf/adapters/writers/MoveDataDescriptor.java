package sncr.xdf.adapters.writers;

import sncr.xdf.file.DLDataSetOperations;

import java.util.List;

public class MoveDataDescriptor {

    public String source;
    public String dest;
    public String mode;
    public String format;
    public String objectName;
    public List<String> partitionList;

    {
        mode = DLDataSetOperations.MODE_APPEND;
        format = DLDataSetOperations.FORMAT_PARQUET;
    }

    public MoveDataDescriptor(String src,
                              String dest,
                              String objectName,
                              String mode,
                              String format,
                              List<String> partitionList) {
        this.source = src;
        this.dest = dest;
        this.mode = mode.toLowerCase();
        this.format = format.toLowerCase();
        this.objectName = objectName;
        this.partitionList = partitionList;
    }
}