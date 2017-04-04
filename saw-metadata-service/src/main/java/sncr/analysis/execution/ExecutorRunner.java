package sncr.analysis.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Created by srya0001 on 3/18/2017.
 */
public class ExecutorRunner {

    private ConcurrentSkipListMap<String, Future<String>> sqlExecutors  = null;
    private ConcurrentSkipListMap<String, String> lastExecutionResults = null;
    private ConcurrentSkipListMap<String, String> lastPredefinedResultsRowID = null;
    private ExecutorService executorService;
    private int capacity;
    private static final Logger m_log = LoggerFactory.getLogger(ExecutorRunner.class.getName());

    public ExecutorRunner(int threads){
        executorService = Executors.newFixedThreadPool(threads);
        sqlExecutors = new ConcurrentSkipListMap<>();
        lastExecutionResults = new ConcurrentSkipListMap<>();
        lastPredefinedResultsRowID = new ConcurrentSkipListMap<>();
        capacity = threads;
    }

    public void startSQLExecutor(String analysisId) throws Exception {

        if (sqlExecutors.size() >= capacity)
            throw new Exception("SQL Executor service out of capacity, please wait");
        ExecuteAnalysis ea = new ExecuteAnalysis(analysisId);
        lastPredefinedResultsRowID.put(analysisId, ea.getPredefinedRowID());
        FutureTask<String> sqlTask = (FutureTask<String>) executorService.submit(ea);
        sqlExecutors.put(analysisId, sqlTask);
    }

    public void startSQLExecutor(String analysisId, String predefResRowID) throws Exception {

        if (sqlExecutors.size() >= capacity)
            throw new Exception("SQL Executor service out of capacity, please wait");
        lastPredefinedResultsRowID.put(analysisId, predefResRowID);
                ExecuteAnalysis ea = new ExecuteAnalysis(predefResRowID, analysisId);
        FutureTask<String> sqlTask = (FutureTask<String>) executorService.submit(ea);
        sqlExecutors.put(analysisId, sqlTask);
    }


    public String checkStatusAndUpdate(String analysisId, long timeout)
    {
        try {
            Future<String> sqlTask = sqlExecutors.get(analysisId);
            if(sqlTask.isDone())
            {
                String res;
                if (timeout <= 0) {
                    m_log.debug("Wait for result " + timeout + " sec and remove task from list");
                    res = sqlExecutors.remove(analysisId).get(timeout, TimeUnit.SECONDS);
                }
                else {
                    m_log.debug("Wait for result (blocking) and remove task from list");
                    res = sqlExecutors.remove(analysisId).get();
                }
                lastExecutionResults.put(analysisId, res);
                return ProcessExecutionResult.Success.name();
            }
            else {
                if (sqlTask.isCancelled()) {
                    m_log.debug("The task [ Analysis ID: " + analysisId + " ] was cancelled");
                    return ProcessExecutionResult.Cancelled.name();
                }
                else {
                    m_log.debug("The task [ Analysis ID: " + analysisId + " ] still working");
                    return ProcessExecutionResult.InProgress.name();
                }
            }
        } catch (InterruptedException e) {
            m_log.error("The task [ Analysis ID: " + analysisId + " ] was interrupted", e);
            return ProcessExecutionResult.InterruptedException.name();
        } catch (ExecutionException e) {
            m_log.error("The task [ Analysis ID: " + analysisId + " ] has thrown execution exception", e);
            return ProcessExecutionResult.ExecutionException.name();
        } catch (TimeoutException e) {
            m_log.error("Timeout has occurred at attempt get result for the task [ Analysis ID: " + analysisId + " ]", e);
            return ProcessExecutionResult.Timeout.name();
        }
    }

    public String waitForCompletion(String analysisId, long waittime)
    {
        try {
            Future<String> sqlTask = sqlExecutors.get(analysisId);
            while (!sqlTask.isDone() &&
                   !sqlTask.isCancelled())
            {
                m_log.debug("Wait for result: " + waittime + " msec");
                sqlTask.wait(waittime);
            }
            String res = sqlExecutors.remove(analysisId).get();
            lastExecutionResults.put(analysisId, res);
            return ProcessExecutionResult.Success.name();

        } catch (InterruptedException e) {
            m_log.error("The task [ Analysis ID: " + analysisId + " ] was interrupted", e);
            return ProcessExecutionResult.InterruptedException.name();
        } catch (ExecutionException e) {
            m_log.error("The task [ Analysis ID: " + analysisId + " ] has thrown execution exception", e);
            return ProcessExecutionResult.ExecutionException.name();
        }
    }

    public void cancel(String analysisId, long timeout)
    {
        try {
            Future<String> sqlTask = sqlExecutors.remove(analysisId);
            if(sqlTask.isDone()){
                String res;
                if (timeout <= 0) {
                    m_log.debug("Cancelling: Wait for result " + timeout + " sec and remove task from list");
                    res = sqlTask.get(timeout, TimeUnit.SECONDS);
                }
                else {
                    m_log.debug("Cancelling: Wait for result (blocking) and remove task from list");
                    res = sqlTask.get();
                }
                lastExecutionResults.put(analysisId, res);
            }
            else {
                if (!sqlTask.isCancelled())
                    sqlTask.cancel(true);
            }
        } catch (InterruptedException e) {
            m_log.error("Cancelling: The task [ Analysis ID: " + analysisId + " ] was interrupted", e);
        } catch (ExecutionException e) {
            m_log.error("Cancelling: The task [ Analysis ID: " + analysisId + " ] has thrown execution exception", e);
        } catch (TimeoutException e) {
            m_log.error("Cancelling: Timeout has occurred at attempt get result for the task [ Analysis ID: " + analysisId + " ]", e);
        }
    }

    public String getLastResult(String analysisID){ return lastExecutionResults.get(analysisID); }
    public String getPredefResultRowID(String analysisID){ return lastPredefinedResultsRowID.get(analysisID); }

    @Override
    protected void finalize() throws Throwable {
        sqlExecutors.keySet().forEach( k -> cancel(k, 30));
        executorService.shutdownNow();
    }
}


