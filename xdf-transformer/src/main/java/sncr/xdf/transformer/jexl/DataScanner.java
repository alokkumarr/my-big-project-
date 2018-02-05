package sncr.xdf.transformer.jexl;

import org.apache.log4j.Logger;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import scala.Tuple2;

import java.util.*;

import static sncr.xdf.transformer.TransformerComponent.TRANSFORMATION_RESULT;

public class DataScanner {

//	private final StructType schema;
	public Map<String,  Broadcast<Dataset<Row>>> referenceData = null;
    private static final Logger logger = Logger.getLogger(DataScanner.class);


	//private String KEYWORD = "null";
	private final String COMMA = ",";

	//TODO:: Come up with descriptor for ref data
	// JEXL lookup :  ref:lookup('dataset', 'filter=?', field_A or List<Fields>>???
	public DataScanner(Map<String, Broadcast<Dataset<Row>>> brdcastReferenceData) throws Exception {
		this.referenceData = brdcastReferenceData;
		for(String refK : brdcastReferenceData.keySet()){
			System.out.printf("Ref DS: %s, Size: %d\n", refK, referenceData.get(refK).getValue().count());
		}
	}

	public String[][] lookup(String groupKey, String filterKey) {

		System.out.printf("Group key: %s, Filter: %s, \n", groupKey, filterKey);


		if(referenceData.containsKey(groupKey)) {
			return null;
		}
		if (filterKey == null || filterKey.isEmpty())
			return null;


		Dataset<Row> refDataset = referenceData.get(groupKey).getValue();
		Dataset<Row> work = refDataset;

		Tuple2<Map<String, String> , Map<Integer, String>> allDecodedKeys = decodeFilterKey(filterKey);

		for(Integer inx: allDecodedKeys._2().keySet()){
			String fName = refDataset.schema().fieldNames()[inx];
			System.out.printf("Process key: %s, Inx: %d\n", fName, inx);
			Column c1 = refDataset.col(fName);
			work = work.where(c1.equalTo(allDecodedKeys._2().get(inx)));
		}

		System.out.println("Do actual lookup" );
		for (int i = 0; i < refDataset.schema().fieldNames().length; i++) {
			String fName = refDataset.schema().fieldNames()[i];
            System.out.println("Ref. dataset field: "  + fName);

			Column c1;
			if (allDecodedKeys._1().containsKey(fName)){
				System.out.printf("Process key: %s, filter value: %s\n", fName, allDecodedKeys._1().get(fName));
				c1 = refDataset.col(fName);
				work = work.where(c1.equalTo(allDecodedKeys._1().get(fName)));
			}
		}

		 List<Row>  rows = work.collectAsList();
		String[][] rowsAsStringArray = new String[rows.size()][0];
		int len = work.schema().fields().length;
		for (int i = 0; i < rows.size(); i++) {
			for ( int j = 0; j < len; j++) {
				if (rows.get(i).get(j) == null)
					rowsAsStringArray[i][j] = "";
				else
					rowsAsStringArray[i][j] = String.valueOf(rows.get(i).get(j));
			}
		}

		return rowsAsStringArray;
	}
	


	private Tuple2<Map<String, String>, Map<Integer, String>> decodeFilterKey(String filterKey) {
		Map<String, String> decodedMapWithNames = new HashMap<>();
		Map<Integer, String> decodedMapWithIndex = new HashMap<>();

		if (filterKey != null)
		{
			String splitKeys[] = filterKey.split(COMMA);
			String key = null;
			for (int i = 0; i < splitKeys.length; i++) {
				key = splitKeys[i];
				if(key.contains("=")) {
					int kInx = (key.trim().substring(0, 2).equals("$K"))?(Integer.parseInt(key.substring(2).trim())):0;
					if (kInx == 0) {
						String kfName = key.substring(0, key.indexOf("="));
						decodedMapWithNames.put(kfName,key.substring(key.indexOf("=") + 1).trim());
					}else if (kInx > 0)
						decodedMapWithIndex.put(kInx,key.substring(key.indexOf("=") + 1).trim());
				}
			}
		}

		System.out.println("Filter decoding result: ");
		for (String k:decodedMapWithNames.keySet()){
			System.out.printf("K: %s, V: %s\n", k, decodedMapWithNames.get(k));
		}
		return new Tuple2<>(decodedMapWithNames, decodedMapWithIndex);
	}

}
