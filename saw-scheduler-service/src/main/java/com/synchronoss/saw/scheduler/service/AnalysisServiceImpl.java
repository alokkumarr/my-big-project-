package com.synchronoss.saw.scheduler.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AnalysisServiceImpl implements AnalysisService {
    @Value("${saw-analysis-service-url}")
    private String analysisUrl;
    private RestTemplate restTemplate;

    public AnalysisServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public List<AnalysisSchedule> getAnalysisSchedules() {
        String url = analysisUrl + "?view=schedule";
        AnalysisSchedule[] schedules = restTemplate.getForObject(
            url, AnalysisSchedule[].class);
        return Arrays.asList(schedules);
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
}
