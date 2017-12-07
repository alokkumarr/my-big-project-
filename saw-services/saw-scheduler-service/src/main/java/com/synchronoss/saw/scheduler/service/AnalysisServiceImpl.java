package com.synchronoss.saw.scheduler.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
       String recipients = prepareRecipientsList(analysis.schedule().email());
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
        String[] param= new String[2];
        param[0] =analysis.id();
        param[1]=latestexection[0];
        String url = dispatchUrl + "/{analysisId}/executions/{executionId}/dispatch";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DispatchBean> entity = new HttpEntity<>(
                execution, headers);

       if (latestexection[0]!=null)
       {
           restTemplate.postForObject(url, entity, String.class,param);
       }
    }

    private ExecutionBean[] fetchExecutionID(String analysisId)
    {
        String url = analysisUrl + "/{analysisId}/executions";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
       return restTemplate.getForObject(url, ExecutionResponse.class, analysisId).execution();

    }

    private String[] findLatestExecution(ExecutionBean[] executionBeans)
    {
        String latestExecutionID = null;
        String latestFinish =null;

        if (executionBeans.length>0) {
            // Initialize latestExecution.
            latestExecutionID = executionBeans[0].id();
            latestFinish = executionBeans[0].finished();
            for (ExecutionBean executionBean : executionBeans) {
               if (Long.parseLong(executionBean.finished()) > Long.parseLong(latestFinish)
                       && executionBean.status().equalsIgnoreCase("Success"))
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
    private String prepareRecipientsList(List<String> recipients) {
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
}
