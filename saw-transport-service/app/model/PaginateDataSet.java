package model;

import scala.Tuple2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public enum PaginateDataSet {

    INSTANCE;
    private PaginateDataSet(){}
    private int dataSize;

    private LRUCache<String, List<Map<String,Tuple2<String, Object>>>> cache
         = new LRUCache<String, List<Map<String,Tuple2<String, Object>>>>(10);
    /**
     * @param key
     * @return
     */
    public List<Map<String, Tuple2<String, Object>>> getCache(String key) {
        return this.cache.get(key);
    }

    /**
     * @return
     */
    public int sizeOfData(){
        return this.dataSize;
    }

    /**
     * @param key
     * @param data
     */
    public void putCache(String key, List<Map<String, Tuple2<String, Object>>> data) {
        this.cache.put(key, data);
    }

    /**
     * @param limit
     * @param start
     * @param key
     * @return
     */
   public List<Map<String, Tuple2<String, Object>>> paginate(int limit, int start, String key)
   {
       List<Map<String, Tuple2<String, Object>>> paginatedData = new ArrayList<Map<String, Tuple2<String, Object>>>();
       if (this.cache.get(key)!=null)
       {
            dataSize = this.cache.get(key).size();
            paginatedData = getPage(this.cache.get(key), start, limit);
       }

       return paginatedData;
   }

    /*
      * returns a view (not a new list) of the sourceList for the
      * range based on page and pageSize
      * @param sourceList
      * @param page
      * @param pageSize
      * @return
  */
    private List<Map<String, Tuple2<String, Object>>> getPage(List<Map<String, Tuple2<String, Object>>> sourceList, int start, int limit) {
        if(limit < 0) {
            throw new IllegalArgumentException("invalid limit: " + limit + " because limit default value is 10");
        }
        if(start < 0) {
            throw new IllegalArgumentException("invalid start: " + start + " because start default value is 0");
        }
        int fromIndex = (start - 1) * limit;
        if(sourceList == null || sourceList.size() < fromIndex){
            throw new NullPointerException("sourceList size is zero or null or list is smaller size then provided start index : " + fromIndex);
        }

        return sourceList.subList(fromIndex, Math.min(fromIndex + limit, sourceList.size()));
    }




}
