package sncr.analysis.execution;

import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sncr.saw.common.config.SAWServiceConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

/**
 * Created by srya0001 on 3/18/2017.
 */
public class ExecutionTask implements Callable<String> {

    protected static final Logger m_log = LoggerFactory.getLogger(ExecutionTask.class.getName());
    private final String inputFile;
    private final String outputFile;
    private String predefRowID;
    private Config conf = SAWServiceConfig.spark_conf();
    private String executorLocation;
    private String analysisId;

    ExecutionTask(String analysisID, String inpFile, String outFile ){
        predefRowID = analysisID + "::" + System.nanoTime();
        executorLocation = conf.getString("sql-executor.script");
        analysisId = analysisID;
        inputFile = inpFile;
        outputFile = outFile;
    }



    public String getPredefinedRowID(){ return predefRowID; }

    @Override
    public String call() throws Exception {
        BufferedReader br = null;
        String cmd = executorLocation + " " + inputFile + " "  + outputFile;
        m_log.debug("CMD line: " + cmd);
        StringBuilder sb = new StringBuilder();
        try {
            ProcessBuilder pb = new ProcessBuilder(executorLocation, inputFile, outputFile);
            Process proc = pb.start();
            proc.waitFor();
            int rc = proc.exitValue();
            if (rc != 0)
            {
                m_log.error("SQL Executor [ Analysis ID = " + analysisId + "] failed with code: " + rc );
            }
            else{
                m_log.debug("Successfully completed execution [ Analysis ID = " + analysisId + "]: " + rc);
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
            m_log.error("IO Exception occurred  [ Analysis ID = " + analysisId + "]: ", e);
        } catch (InterruptedException e) {
            m_log.error("SQL Execution process was  [ Analysis ID = " + analysisId + "] aborted: ", e);
        } finally {
            if (br != null) br.close();
        }
        return sb.toString();
    }




}
