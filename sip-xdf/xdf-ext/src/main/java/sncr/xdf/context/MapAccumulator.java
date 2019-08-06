package sncr.xdf.context;

import java.util.HashMap;
import java.util.Map;

import org.apache.spark.util.AccumulatorV2;



public class MapAccumulator extends AccumulatorV2<Map<String, Long>, Map<String, Long>> {
	
	/**
	 * serialization id.
	 */
	private static final long serialVersionUID = -3602573521711464337L;


	
	
	
	private Map<String , Long> accumulatorMap;
	
	public MapAccumulator(){
		this.accumulatorMap = new HashMap<String, Long>();
	}
	
	MapAccumulator(Map<String , Long> map){
		this.accumulatorMap = map;
	}

	@Override
	public void add(Map<String, Long> map) {
		this.accumulatorMap.putAll(map);
	}

	@Override
	public AccumulatorV2<Map<String, Long>, Map<String, Long>> copy() {
		
		return new MapAccumulator(this.accumulatorMap);
	}

	@Override
	public boolean isZero() {
		
		return this.accumulatorMap.isEmpty();
	}

	@Override
	public void merge(AccumulatorV2<Map<String, Long>, Map<String, Long>> map) {
		this.accumulatorMap.putAll(map.value());
	}

	@Override
	public void reset() {
		this.accumulatorMap = new HashMap<String, Long>();
		
	}

	@Override
	public Map<String, Long> value() {
		return this.accumulatorMap;
	}
	
	
}