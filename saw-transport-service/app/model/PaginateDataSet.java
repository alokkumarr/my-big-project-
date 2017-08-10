package model;

import play.libs.F;
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
        return cache.get(key);
    }

    /**
     * @return
     */
    public int sizeOfData(){
        return dataSize;
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
       if (cache.get(key)!=null)
       {
            paginatedData = getPage(cache.get(key), start, limit);
       }
       dataSize = paginatedData.size();
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
    private <T> List<T> getPage(List<T> sourceList, int start, int limit) {
        if(limit <= 0 || start <= 0) {
            throw new IllegalArgumentException("invalid limit: " + limit);
        }

        int fromIndex = (start - 1) * limit;
        if(sourceList == null || sourceList.size() < fromIndex){
            return Collections.emptyList();
        }
        // toIndex exclusive
        return sourceList.subList(fromIndex, Math.min(fromIndex + limit, sourceList.size()));
    }



}
