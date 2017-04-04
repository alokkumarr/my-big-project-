package sncr.analysis.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

/**
 * Created by srya0001 on 3/18/2017.
 */
public class ExecuteAnalysis implements Callable<String> {

    protected static final Logger m_log = LoggerFactory.getLogger(ExecuteAnalysis.class.getName());
    private String predefRowID;
    private String executorLocation = "/opt/saw/sql-executor/bin/start.sh";
    private String analysisId;

    ExecuteAnalysis(String analysisID ){
        predefRowID = analysisID + "::" + System.nanoTime();
        analysisId = analysisID;
    }

    ExecuteAnalysis(String predefResultRowID,  String analysisID ){
        predefRowID = predefResultRowID;
        analysisId = analysisID;
    }

    public void resetSQLExecutorLocation(String newLoc) {  executorLocation = newLoc; }

    public String getPredefinedRowID(){ return predefRowID; }

    @Override
    public String call() throws Exception {
        BufferedReader br = null;
        String cmd = executorLocation + " " + analysisId + " "  + predefRowID;
        m_log.debug("CMD line: " + cmd);
        StringBuilder sb = new StringBuilder();
        try {
            ProcessBuilder pb = new ProcessBuilder(executorLocation, analysisId, predefRowID);
            Process proc = pb.start();
            proc.waitFor();
            int rc = proc.exitValue();
            if (rc != 0)
            {
                m_log.error("SQL Executor failed with code: " + rc );
            }
            else{
                m_log.debug("Successfully completed execution: " + rc);
            }

            br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();br = null;

            br = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            while ((line = br.readLine()) != null) {
                m_log.debug( line);
            }

        } catch (IOException e) {
            m_log.error("IO Exception occurred: ", e);
        } catch (InterruptedException e) {
            m_log.error("SQL Execution process was aborted: ", e);
        } finally {
            if (br != null) br.close();
        }
        return sb.toString();
    }




}
