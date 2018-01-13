package sncr.xdf.transformer.jexl;

import org.apache.log4j.Logger;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.*;

public class DataScanner {

//	private final StructType schema;
	public Broadcast<Dataset> referenceData = null;
	public List<Row> records =null;
    private static final Logger logger = Logger.getLogger(DataScanner.class);


	//private String KEYWORD = "null";
	private final String COMMA = ",";
	
	public DataScanner(Broadcast<Dataset> brdcastReferenceData) throws Exception {
		this.referenceData = brdcastReferenceData;
/*
		if (referenceData!=null && !referenceData.isEmpty()) {
			Row row = (Row) (referenceData.entrySet().toArray())[0];
			schema = row.schema();
		}
		else{
			throw new Exception("Cannot create data scanner from empty reference dataset");
		}
*/
	}

	public String[][] lookup(String groupKey, String filterKey) {
/*
		List<Row> filterbyGroupKey = referenceData.get(groupKey);
		if(filterbyGroupKey == null) {
			return null;
		}

		logger.debug("filterbyGroupKey :" + filterbyGroupKey.toString() + " Filter: " + filterKey);
		Map<Integer, String> decodedKeys = decodeFilterKey(filterKey);

		int fields[] = new int[decodedKeys.size()];
		List<String[]>list = new ArrayList<String[]>();

		Iterator<Integer> it = decodedKeys.keySet().iterator();

		String valueData = "";
		for (int i = 0; i < schema.fieldNames().length; i++) {
			String fName = schema.fieldNames()[i];
			if (decodedKeys.containsKey(fName)){
				valueData +=
			}
		}

		int count = 0;
		while (it.hasNext()) {
			int key = it.next();
			fields[count++] = key;
			valueData += decodedKeys.get(key);
		}

		Key receivedKey = new Key(valueData);
		for (Row row : filterbyGroupKey)
		{
			if (receivedKey.equals(extractKey)) {
				//System.out.println("receivedKey :"+ receivedKey);
				//System.out.println("toStringArray(row):"+ row.toString());
				list.add(toStringArray(row));
			}
		}
		if(list.size() ==0){
			list.add(null);
		}
		return list.toArray(new String[list.size()][]);
*/
return null;
	}
	
	private String[] toStringArray(Row record)
	{
		String recordsIndex[] = null;
/*
		if (record != null)
		{

		int fields = record.getFields().length;
		recordsIndex = new String [fields];
		for (int i =0; i< fields ; i++)
		{
			recordsIndex[i] = new String(record.getField(i),Charset.forName("UTF-8"));
		}
		}
*/
		return recordsIndex;
	}

	private Map<Integer, String> decodeFilterKey(String filterKey) {
		Map<Integer, String> decodedMap = new HashMap<Integer, String>();
		if (filterKey != null)
		{
			//filterKey = filterKey.replaceAll("\\s+", "");
			String splitKeys[] = filterKey.split(COMMA);
			String key = null;
			for (int i = 0; i < splitKeys.length; i++) {
				key = splitKeys[i];
				if(key.contains("=")) {
					int kInx = (key.trim().substring(0, 2).equals("$K"))?(Integer.parseInt(key.substring(2).trim())):0;
					if (kInx > 0)decodedMap.put(kInx,key.substring(key.indexOf("=") + 1).trim());
				}
			}
		}
		return decodedMap;
	}

}
