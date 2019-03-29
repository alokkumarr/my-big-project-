package com.synchronoss.saw.analysis.service.migrationservice;

import com.google.gson.JsonObject;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.model.Store;

public interface AnalysisSipDslConverter {
  public Analysis convert(JsonObject oldAnalysisDefinition);

  /**
   * Set all the common parameters across all types of analysis.
   *
   * @param analysis - SIP DSL analysis definition object
   * @param oldAnalysisDefinition - Old analysis definition
   *
   * @return
   */
  default Analysis setCommonParams(Analysis analysis, JsonObject oldAnalysisDefinition) {
    if (analysis == null) {
      return null;
    }

    analysis.setId(oldAnalysisDefinition.get("id").getAsString());

    if (oldAnalysisDefinition.has("parentAnalysisId")) {
      String parentAnalysisId = oldAnalysisDefinition.get("parentAnalysisId").getAsString();

      analysis.setParentAnalysisId(parentAnalysisId);
    }

    if (oldAnalysisDefinition.has("description")) {
      analysis.setDescription(oldAnalysisDefinition.get("description").getAsString());
    }

    analysis.setSemanticId(oldAnalysisDefinition.get("semanticId").getAsString());
    analysis.setName(oldAnalysisDefinition.get("name").getAsString());

    analysis.setType(oldAnalysisDefinition.get("type").getAsString());
    analysis.setCategory(oldAnalysisDefinition.get("categoryId").getAsString());

    analysis.setCustomerCode(oldAnalysisDefinition.get("customerCode").getAsString());
    analysis.setProjectCode(oldAnalysisDefinition.get("projectCode").getAsString());
    analysis.setModule(oldAnalysisDefinition.get("module").getAsString());

    analysis.setCreatedTime(oldAnalysisDefinition.get("createdTimestamp").getAsLong());
    analysis.setCreatedBy(oldAnalysisDefinition.get("username").getAsString());

    analysis.setModifiedTime(oldAnalysisDefinition.get("updatedTimestamp").getAsLong());
    analysis.setModifiedBy(oldAnalysisDefinition.get("updatedUserName").getAsString());

    return analysis;
  }

  default Store extractStoreInfo(JsonObject esRepository) {
    Store store = new Store();

    if (esRepository.has("storageType")) {
      store.setStorageType(esRepository.get("storageType").getAsString());
    }

    if (esRepository.has("indexName") && esRepository.has("type")) {
      String index = esRepository.get("indexName").getAsString();
      String type = esRepository.get("type").getAsString();
      store.setDataStore(index + "/" + type);
    }

    return store;
  }
}
