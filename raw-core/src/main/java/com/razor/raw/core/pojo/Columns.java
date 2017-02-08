package com.razor.raw.core.pojo;
/**
 * 
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Columns implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1318762258956626204L;
	protected List<Column> column;

    
    public void setColumn(List<Column> column) {
		this.column = column;
	}


	public List<Column> getColumn() {
        if (column == null) {
            column = new ArrayList<Column>();
        }
        return this.column;
    }

}
