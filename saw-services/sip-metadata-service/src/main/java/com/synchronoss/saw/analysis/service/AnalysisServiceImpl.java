package com.synchronoss.saw.analysis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.synchronoss.saw.analysis.metadata.AnalysisMetadata;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.analysis.modal.Ticket;
import com.synchronoss.saw.exceptions.SipCreateEntityException;
import com.synchronoss.saw.exceptions.SipDeleteEntityException;
import com.synchronoss.saw.exceptions.SipReadEntityException;
import com.synchronoss.saw.exceptions.SipUpdateEntityException;
import com.synchronoss.saw.util.SipMetadataUtils;
import java.time.Instant;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AnalysisServiceImpl implements AnalysisService {

  private static final Logger logger = LoggerFactory.getLogger(AnalysisServiceImpl.class);
  Gson gson = new Gson();

  @Value("${metastore.base}")
  @NotNull
  private String basePath;

  @Value("${metastore.analysis}")
  private String tableName;

  private ObjectMapper objectMapper = new ObjectMapper();
  private AnalysisMetadata analysisMetadataStore;

  /*{
    try {
      analysisMetadataStore = new AnalysisMetadata(tableName, basePath);
    } catch (Exception e) {
      throw new SipIoException("Exception occurred while initializing the analysis metadata table");
    }
  }*/

  @Override
  public Analysis createAnalysis(Analysis analysis, Ticket ticket) throws SipCreateEntityException {
    // analysis.setCreatedBy(ticket.getMasterLoginId());
    analysis.setCreatedTime(Instant.now().toEpochMilli());
    // analysis.setCustomerCode(ticket.getCustomerCode());
    try {
      JsonElement parsedAnalysis =
          SipMetadataUtils.toJsonElement(objectMapper.writeValueAsString(analysis));
      analysisMetadataStore = new AnalysisMetadata(tableName, basePath);
      analysisMetadataStore.create(analysis.getId(), parsedAnalysis);
    } catch (Exception e) {
      logger.error("Exception occurred while creating analysis", e);
      throw new SipCreateEntityException("Exception occurred while creating analysis");
    }
    return analysis;
  }

  @Override
  public Analysis updateAnalysis(Analysis analysis, Ticket ticket) throws SipUpdateEntityException {
    // analysis.setModifiedBy(ticket.getMasterLoginId());
    analysis.setModifiedTime(Instant.now().toEpochMilli());
    // analysis.setCustomerCode(ticket.getCustomerCode());
    try {
      JsonElement parsedAnalysis =
          SipMetadataUtils.toJsonElement(objectMapper.writeValueAsString(analysis));
      analysisMetadataStore = new AnalysisMetadata(tableName, basePath);
      analysisMetadataStore.update(analysis.getId(), parsedAnalysis);
    } catch (Exception e) {
      logger.error("Exception occurred while updating analysis", e);
      throw new SipUpdateEntityException("Exception occurred while updating analysis");
    }
    return analysis;
  }

  @Override
  public void deleteAnalysis(String analysisID, Ticket ticket) throws SipDeleteEntityException {
    try {
      analysisMetadataStore = new AnalysisMetadata(tableName, basePath);
      analysisMetadataStore.delete(analysisID);
    } catch (Exception e) {
      logger.error("Exception occurred while deleting analysis", e);
      throw new SipDeleteEntityException("Exception occurred while deleting analysis", e);
    }
  }

  @Override
  public Analysis getAnalysis(String analysisID, Ticket ticket) throws SipReadEntityException {
    JsonElement doc;
    Analysis analysis;
    try {
      analysisMetadataStore = new AnalysisMetadata(tableName, basePath);
      doc = analysisMetadataStore.read(analysisID);
      if (doc == null) {
        return null;
      }
      analysis = gson.fromJson(doc, Analysis.class);
    } catch (Exception e) {
      logger.error("Exception occurred while fetching analysis", e);
      throw new SipReadEntityException("Exception occurred while fetching analysis", e);
    }
    return analysis;
  }
}
