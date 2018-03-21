package sncr.xdf.transformer.jexl;

import org.apache.log4j.Logger;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Row;
import scala.Tuple2;

import java.util.*;

public class DataScanner {

	private Map<String,  Broadcast<List<Row>>> referenceData;
	private Map<String, Broadcast<List<Tuple2<String, String>>>> refDataDescriptor;


	//private String KEYWORD = "null";
	private final String COMMA = ",";

	//TODO:: Come up with descriptor for ref data
	// JEXL lookup :  ref:lookup('dataset', 'filter=?', field_A or List<Fields>>???
	public DataScanner(Map<String, Broadcast<List<Row>>> rd,
					   Map<String, Broadcast<List<Tuple2<String, String>>>> refDataDescriptor) {
		this.referenceData = rd;
		this.refDataDescriptor = refDataDescriptor;
	}

	public String[][] lookup(String groupKey, String filterKey) {

		//System.out.printf("Group key: %s, Filter: %s, \n", groupKey, filterKey);
		if (filterKey == null || filterKey.isEmpty()) return null;
		if(!referenceData.containsKey(groupKey)) 	return null;
		if(!refDataDescriptor.containsKey(groupKey)) return null;

		List<Row>  rows = referenceData.get(groupKey).getValue();
		List<Tuple2<String, String>> desc = refDataDescriptor.get(groupKey).getValue();
		Tuple2<Map<String, String>, Boolean> allDecodedKeys = decodeFilterKey(filterKey);
		if (allDecodedKeys == null) return null;

		Map<String, String> fMap = allDecodedKeys._1();
		Boolean filterWithNames = allDecodedKeys._2();

//		for(Tuple2<String, String> tu: desc) System.out.printf(" %s -> %s, ", tu._1(), tu._2());

		HashSet<Row> matchingRows = new HashSet<>();
		boolean matchAllFilters = true;
		for( Row r: rows) {
			if (filterWithNames) {
				for (int i = 0; i < desc.size(); i++) {
				String fName = desc.get(i)._1();
				String fType = desc.get(i)._2();
					if (fMap.containsKey(fName)) {
//						System.out.printf("Process (with name) field[%d]: %s, filter value: %s, field type: %s, field value: %s \n", i, fName, fMap.get(fName), fType, String.valueOf(r.get(i)));
						matchAllFilters = matchAllFilters && matchFilter(r, i, fType, fMap.get(fName));
					}
					//else - continue looping over fields
				}
			}else{
				for( String sIndex : fMap.keySet()){
					int ind = Integer.parseInt(sIndex);
					String fName = desc.get(ind)._1();
					String fType = desc.get(ind)._2();
//					System.out.printf("Process ( with index ) field[%d]: %s, filter value: %s, field type: %s \n", ind, fName, fMap.get(fName), fType);
					matchAllFilters = matchAllFilters && matchFilter(r, ind, fType, fMap.get(fName));

				}
			}
			if (matchAllFilters) matchingRows.add(r);
		}

		Row r0 = rows.get(0);
		String[][] rowsAsStringArray = new String[matchingRows.size()][r0.length()];
		System.out.printf("Found totally %d matching rows of %d rows in ref data, nuber of fields %d \n", matchingRows.size(), rows.size(), r0.length());
		int i=0;
		for (Row r : matchingRows) {
			for ( int j = 0; j < r.length(); j++) {
				if (r.get(j) == null) {
                    //System.out.printf("Field %d of row %d is null\n", j, i);
                    rowsAsStringArray[i][j] = "";
                }
				else {
					//System.out.printf(" Row[%d] - Field[%d] = %s \n", i, j, String.valueOf(r.get(j)));
					rowsAsStringArray[i][j] = String.valueOf(r.get(j));
				}
			}
			i++;
		}

		return rowsAsStringArray;
	}

	private boolean matchFilter(Row r, int i, String fType, String filterValue) {
		try {
			if (r.get(i) == null && filterValue.equalsIgnoreCase("null")) return true;
			switch (fType) {
				case "BooleanType":
					return (r.getBoolean(i) == Boolean.valueOf(filterValue));
				case "IntegerType":
				case "ShortType":
					return (r.getInt(i) == Integer.valueOf(filterValue));
				case "LongType":
					return (r.getLong(i) == Long.valueOf(filterValue));
				case "DoubleType":
				case "FloatType":
					return (r.getDouble(i) == Double.valueOf(filterValue));
				case "StringType":
					return r.getString(i).equalsIgnoreCase(filterValue);
				case "NullType":
					break;
				default:
					System.err.println("Unsupported type: " + fType);
					return false;
			}
		}catch(Exception e){
			System.err.println("Could not compare filter and row values, see following exception");
			System.err.print(e);
			return false;
		}
		return false;
	}


	private Tuple2<Map<String, String>, Boolean> decodeFilterKey(String filterKey) {
		Map<String, String> decodedMapWithNames = new HashMap<>();

		//By default - assume we have filter with names
		Boolean filterByName = true;
		if (filterKey != null)
		{
			String splitKeys[] = filterKey.split(COMMA);
			if (splitKeys.length > 0) {
				//Check by first filter pair if we have filter with names or filter with field index, key expressin  should be $K
				filterByName = !(splitKeys[0].trim().substring(0, 2).equals("$K"));
				String key = null;
				for (int i = 0; i < splitKeys.length; i++) {
					key = splitKeys[i];
					if (key.contains("=")) {

						if (filterByName && key.trim().substring(0, 2).equals("$K"))
						{
							System.err.printf("Filter can be either with index $K or with names $fieldName1, but not both: %s \n", key);
							return null;
						}
						if (filterByName){
							String kfName = key.substring(0, key.indexOf("="));
							kfName = (kfName.indexOf('$') == 0) ? kfName.substring(1) : kfName;
							decodedMapWithNames.put(kfName, key.substring(key.indexOf("=") + 1).trim());
						}
						else{
							int kInx = (key.trim().substring(0, 2).equals("$K")) ? (Integer.parseInt(key.substring(2).trim())):-1;
							if (kInx >= 0)
								decodedMapWithNames.put(String.valueOf(kInx), key.substring(key.indexOf("=") + 1).trim());
						}
					}
				}
			}
			else{
				System.err.println("Could not parse filter string");
				return null;
			}
		}
/*
		System.out.println("Filter decoding result: ");
		for (String k:decodedMapWithNames.keySet()){
			System.out.printf("K: %s, V: %s\n", k, decodedMapWithNames.get(k));
		}
*/
		return new Tuple2<>(decodedMapWithNames, filterByName);
	}

}
