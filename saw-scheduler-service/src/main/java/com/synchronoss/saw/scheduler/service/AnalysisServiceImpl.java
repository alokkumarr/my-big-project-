package com.synchronoss.saw.scheduler.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
        String url = analysisUrl + "-schedules";
        AnalysisSchedule[] schedules = restTemplate.getForObject(
            url, AnalysisSchedule[].class);
        return Arrays.asList(schedules);
    }

    public void executeAnalysis(String analysisId) {
        String url = analysisUrl + "/" + analysisId + "/executions";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
            org.springframework.http.MediaType.APPLICATION_JSON);
        String body = "{type: \"scheduled\"}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject(url, entity, String.class);
    }
}
