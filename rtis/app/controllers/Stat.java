package controllers;



import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by srya0001 on 4/22/2016.
 */
public class Stat {

    static ConcurrentHashMap<String, AtomicInteger> accepted_requests;
    static ConcurrentHashMap<String, AtomicInteger> rejected_requests;
    static LocalDateTime start_time = null;

    public static LocalDateTime getStartTime() { return start_time; }

    static {
        start_time = LocalDateTime.now();
        accepted_requests = new ConcurrentHashMap();
        rejected_requests = new ConcurrentHashMap();
    }

    public static int getRequestStat(String appKey, int incr){
        if (!accepted_requests.contains(appKey)) {
            AtomicInteger ai = new AtomicInteger(incr);
            synchronized (accepted_requests) {
                accepted_requests.put(appKey, ai); }
            return ai.get();
        }
        else synchronized (accepted_requests) {
            AtomicInteger ai = accepted_requests.get(appKey);
            return ai.addAndGet(incr);
        }
    }

    public static int getRejectedRequestStat(String appKey, int incr){
        if (!rejected_requests.contains(appKey)) {
            AtomicInteger ai = new AtomicInteger(incr);
            synchronized (rejected_requests) {
                rejected_requests.put(appKey, ai); }
            return ai.get();
        }
        else synchronized (rejected_requests) {
            AtomicInteger ai = rejected_requests.get(appKey);
            return ai.addAndGet(incr);
        }
    }

}
