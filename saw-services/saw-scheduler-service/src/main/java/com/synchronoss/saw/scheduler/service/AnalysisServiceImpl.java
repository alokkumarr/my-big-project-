package com.synchronoss.saw.scheduler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Component
public class AnalysisServiceImpl implements AnalysisService {
    @Value("${saw-analysis-service-url}")
    private String analysisUrl;

    @Value("${saw-dispatch-service-url}")
    private String dispatchUrl;
    private RestTemplate restTemplate;

    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    public AnalysisServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public AnalysisSchedule[] getAnalysisSchedules() {
        String url = analysisUrl + "?view=schedule";
        return restTemplate.getForObject(url, AnalysisSchedulesResponse.class)
            .analyses();
    }

    public void executeAnalysis(String analysisId) {
        AnalysisExecution execution = ImmutableAnalysisExecution.builder()
            .type("scheduled").build();
        String url = analysisUrl + "/{analysisId}/executions";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AnalysisExecution> entity = new HttpEntity<>(
            execution, headers);
        restTemplate.postForObject(url, entity, String.class, analysisId);
    }

    public void scheduleDispatch(AnalysisSchedule analysis)
    {
           if ((analysis.schedule().emails() == null || analysis.schedule().emails().length == 0)
                   && !(isValidDispatch(analysis))) {
               return;
           }
           String recipients = prepareRecipientsList(analysis.schedule().emails());
           ExecutionBean[] executionBeans = fetchExecutionID(analysis.id());
           String[] latestexection = findLatestExecution(executionBeans);
           Date date = new Date(Long.parseLong(latestexection[1]));
           DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
           format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
           String formatted = format.format(date);
           DispatchBean execution = ImmutableDispatchBean.builder()
                   .emailList(recipients).fileType("csv")
                   .description(analysis.description()).name(analysis.name()).userFullName(analysis.userFullName())
                   .metricName(analysis.metricName()).publishedTime(formatted).build();
           String[] param = new String[3];
           param[0] = analysis.id();
           param[1] = latestexection[0];
           param[2] = analysis.type();
           String url = dispatchUrl + "/{analysisId}/executions/{executionId}/dispatch/{type}";
           HttpHeaders headers = new HttpHeaders();
           headers.setContentType(MediaType.APPLICATION_JSON);
           HttpEntity<DispatchBean> entity = new HttpEntity<>(
                   execution, headers);

           if (latestexection[0] != null) {
               restTemplate.postForObject(url, entity, String.class, param);
           }
       }

    private ExecutionBean[] fetchExecutionID(String analysisId)
    {
        String url = analysisUrl + "/{analysisId}/executions";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
       return restTemplate.getForObject(url, ExecutionResponse.class, analysisId).executions();

    }

    private String[] findLatestExecution(ExecutionBean[] executionBeans)
    {
        String latestExecutionID = null;
        String latestFinish =null;

        /** TO DO : pivot Analysis does not contains execution status , It may bug in system
         *   consider status by-default as success if execution doesn't contains status
         */

        if (executionBeans.length>0) {
            // Initialize latestExecution.
            latestExecutionID = executionBeans[0].id();
            latestFinish = executionBeans[0].finished();
            for (ExecutionBean executionBean : executionBeans) {
               if (Long.parseLong(executionBean.finished()) > Long.parseLong(latestFinish)
                       && (executionBean.status()==null || executionBean.status().equalsIgnoreCase("Success")))
               {
                   latestExecutionID = executionBean.id();
                   latestFinish = executionBean.finished();
               }
            }
        }
         String[] val = new String [2];
        val[0] = latestExecutionID;
        val[1] = latestFinish;
        return val;
    }
    private String prepareRecipientsList(String [] recipients) {
        StringBuffer stringBuffer = new StringBuffer();
        boolean first = true;
        for (String recipient :  recipients) {
            if (first) {
                stringBuffer.append(recipient);
                first = false;
            }
            else {
                stringBuffer.append(",");
                stringBuffer.append(recipient);
            }
        }
        return stringBuffer.toString();
    }

    /**
     *
     * @param analysis
     * @return
     */
    private boolean isValidDispatch(AnalysisSchedule analysis) {
        List<String> missingAtribute = new ArrayList<String>();
        if (analysis.id() == null)
            missingAtribute.add("id");
        if (analysis.name() == null)
            missingAtribute.add("name");
        if (analysis.metricName() == null)
            missingAtribute.add("metricName");
        if (analysis.schedule().emails() == null)
            missingAtribute.add("email");
        if (missingAtribute.size() > 0) {
            log.warn("Some of required attributes are not available " + missingAtribute.toString() + " , Skipping email dispatch for the analysis ");
          return false;
        }
        return true;
    }
}
