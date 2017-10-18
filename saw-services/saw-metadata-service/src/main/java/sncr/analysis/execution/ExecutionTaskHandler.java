package sncr.analysis.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sncr.metadata.analysis.AnalysisExecutionHandler;

import java.util.concurrent.*;


/**
 * Created by srya0001 on 3/18/2017.
 */
public class ExecutionTaskHandler {

    private ConcurrentSkipListMap<String, Future<String>> sqlExecutors  = null;
    private ConcurrentSkipListMap<String, AnalysisExecutionHandler> executionHandler = null;

    private ExecutorService executorService;
    private int capacity;
    private static final Logger m_log = LoggerFactory.getLogger(ExecutionTaskHandler.class.getName());

    public ExecutionTaskHandler(int threads){
        executorService = Executors.newFixedThreadPool(threads);
        sqlExecutors = new ConcurrentSkipListMap<>();
        executionHandler = new ConcurrentSkipListMap<>();
        capacity = threads;
    }

    public void startSQLExecutor(AnalysisExecutionHandler ah) throws Exception {

        if (sqlExecutors.size() >= capacity)
            throw new Exception("SQL Executor service out of capacity, please wait");

        ExecutionTask ea = new ExecutionTask(ah.getRowID(), ah.sqlExecInputFilename(), ah.resultExecOutputFile());
        executionHandler.put(ah.getRowID(), ah);
        String resultId = ea.getPredefinedRowID();
        ah.setPreDefinedResultKey(resultId);
        ah.generateJobDescription(resultId);
        FutureTask<String> sqlTask = (FutureTask<String>) executorService.submit(ea);
        sqlExecutors.put(ah.getRowID(), sqlTask);
    }

    public String checkStatusAndUpdate(String analysisId, long timeout)
    {
        AnalysisExecutionHandler aeh = executionHandler.get(analysisId);
        if (aeh == null)
            throw new IllegalArgumentException("Could not find analysis with ID =  " + analysisId );

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
                aeh.setResult(res);
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

    public AnalysisExecutionHandler waitForCompletion(String analysisId, long waittime)
    {
        m_log.debug("Wait for completion of SQL Execution task: " + analysisId);

        AnalysisExecutionHandler aeh = executionHandler.remove(analysisId);
        if (aeh == null)
            throw new IllegalArgumentException("Could not find analysis with ID =  " + analysisId );

        try {
            Future<String> sqlTask = sqlExecutors.get(analysisId);
            String res = "";
            while (!sqlTask.isDone() &&
                   !sqlTask.isCancelled())
            {
                m_log.trace("Wait for result: " + waittime  + " sec");
                Thread.sleep(500);
            }
            res = sqlTask.get(waittime, TimeUnit.SECONDS);
            sqlExecutors.remove(analysisId);
            aeh.setResult(res);
            m_log.debug("Result ===> " + res);
            return aeh;
        } catch (InterruptedException e) {
            m_log.error("The task [ Analysis ID: " + analysisId + " ] was interrupted", e);
            aeh.setResult(ProcessExecutionResult.InterruptedException.name());
            return aeh;
        } catch (ExecutionException e) {
            m_log.error("The task [ Analysis ID: " + analysisId + " ] has thrown execution exception", e);
            aeh.setResult(ProcessExecutionResult.ExecutionException.name());
            return aeh;
        } catch (TimeoutException e) {
            m_log.error("The task [ Analysis ID: " + analysisId + " ] has thrown timeout exception", e);
            return aeh;
        }
    }


    public AnalysisExecutionHandler cancel(String analysisId, long timeout)
    {
        m_log.debug("Cancel SQL Execution task: " + analysisId);

        AnalysisExecutionHandler aeh = executionHandler.remove(analysisId);
        if (aeh == null) {
            m_log.error( "The task handler with analysis ID not found - skip it" );
            return null;
        }

        try {
            Future<String> sqlTask = sqlExecutors.remove(analysisId);
            if(sqlTask.isDone()){
                String res;
                if (timeout <= 0) {
                    m_log.debug("Cancelling: Wait for result " + timeout + " sec and remove task from list");
                    res = sqlTask.get(timeout, TimeUnit.SECONDS);
                    aeh.setResult(res);
                }
                else {
                    m_log.debug("Cancelling: Wait for result (blocking) and remove task from list");
                    res = sqlTask.get();
                    aeh.setResult(res);
                }
            }
            else {
                m_log.debug("Cancel SQL Execution task: " + analysisId +  " if not cancelled yet.");
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
        return aeh;
    }

    public AnalysisExecutionHandler getResult(String analysisID){ return executionHandler.get(analysisID); }

    public String getPredefResultRowID(String analysisID){
        AnalysisExecutionHandler aeh = executionHandler.get(analysisID);
        return aeh.getPreDefinedResultKey();
    }

    @Override
    protected void finalize() throws Throwable {
        sqlExecutors.keySet().forEach( k -> cancel(k, 30));
        executorService.shutdownNow();
    }
}


