package com.synchronoss.saw.analysis.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.saw.analysis.metadata.AnalysisMetadata;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.exceptions.SipCreateEntityException;
import com.synchronoss.saw.exceptions.SipDeleteEntityException;
import com.synchronoss.saw.exceptions.SipReadEntityException;
import com.synchronoss.saw.exceptions.SipUpdateEntityException;
import com.synchronoss.saw.util.SipMetadataUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import org.ojai.Document;
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

  @Value("${metastore.migration}")
  private String migrationStatusTable;

  @Value("${analysis.get-analysis-url}")
  @NotNull
  private String analysisUri;

  @Value("${analysis.binary-migration-required}")
  @NotNull
  private boolean migrationRequires;

  private ObjectMapper objectMapper = new ObjectMapper();
  private AnalysisMetadata analysisMetadataStore;

  /*{
    try {
      analysisMetadataStore = new AnalysisMetadata(tableName, basePath);
    } catch (Exception e) {
      throw new SipIoException("Exception occurred while initializing the analysis metadata table");
    }
  }*/
  @PostConstruct
  private void init() throws Exception {
    if (migrationRequires) {
      logger.trace("Migration initiated.. " + migrationRequires);
      new MigrateAnalysis()
          .convertBinaryToJson(tableName, basePath, analysisUri, migrationStatusTable);
    }
    logger.trace("Migration ended..");
  }

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
  public void deleteAnalysis(String analysisId, Ticket ticket) throws SipDeleteEntityException {
    try {
      analysisMetadataStore = new AnalysisMetadata(tableName, basePath);
      analysisMetadataStore.delete(analysisId);
    } catch (Exception e) {
      logger.error("Exception occurred while deleting analysis", e);
      throw new SipDeleteEntityException("Exception occurred while deleting analysis", e);
    }
  }

  // TODO : Response to be changed back to Analysis type
  @Override
  public Analysis getAnalysis(String analysisId, Ticket ticket) throws SipReadEntityException {
    Document doc;
    Analysis analysis;
    try {
      analysisMetadataStore = new AnalysisMetadata(tableName, basePath);
      doc = analysisMetadataStore.readDocumet(analysisId);
      if (doc == null) {
        return null;
      }
      ObjectMapper mapper = new ObjectMapper();
      mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      analysis = mapper.readValue(doc.asJsonString(), Analysis.class);
    } catch (Exception e) {
      logger.error("Exception occurred while fetching analysis", e);
      throw new SipReadEntityException("Exception occurred while fetching analysis", e);
    }
    return analysis;
  }

  @Override
  public List<Analysis> getAnalysisByCategory(String categoryId, Ticket ticket)
      throws SipReadEntityException {
    List<Document> doc = null;
    Analysis analysis;
    List<Analysis> analysisList = new ArrayList<>();
    Map<String, String> category = new HashMap<>();
    category.put("category", categoryId);
    try {
      analysisMetadataStore = new AnalysisMetadata(tableName, basePath);
      doc = analysisMetadataStore.searchAll(category);
      if (doc == null) {
        return null;
      }
      ObjectMapper mapper = new ObjectMapper();
      mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      for (Document d : doc) {
        analysisList.add(mapper.readValue(d.asJsonString(), Analysis.class));
      }
    } catch (Exception e) {
      logger.error("Exception occurred while fetching analysis", e);
      throw new SipReadEntityException("Exception occurred while fetching analysis", e);
    }
    return analysisList;
  }

  @Override
  public List<Analysis> getAnalysisByCategoryForUserId(
      String categoryId, Long userId, Ticket ticket) throws SipReadEntityException {
    List<Document> doc = null;
    List<Analysis> objDocs = new ArrayList<>();
    try {
      analysisMetadataStore = new AnalysisMetadata(tableName, basePath);
      doc = analysisMetadataStore.searchByCategoryForUserId(categoryId, userId);
      if (doc == null) {
        return null;
      }
      for (Document d : doc) {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objDocs.add(objectMapper.readValue(d.asJsonString(), Analysis.class));
      }
    } catch (Exception e) {
      logger.error("Exception occurred while fetching analysis by category for userId", e);
      throw new SipReadEntityException(
          "Exception occurred while fetching analysis by category for userId", e);
    }
    return objDocs;
  }
}
