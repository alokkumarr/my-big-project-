package com.synchronoss.saw.semantic.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import com.synchronoss.saw.semantic.SAWSemanticUtils;
import com.synchronoss.saw.semantic.exceptions.CreateEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.DeleteEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.semantic.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.UpdateEntitySAWException;
import com.synchronoss.saw.semantic.model.Action.Verb;
import com.synchronoss.saw.semantic.model.NodeCategory;
import com.synchronoss.saw.semantic.model.request.SemanticNode;
import com.synchronoss.saw.semantic.model.request.SemanticNodes;
import sncr.bda.cli.MetaDataStoreRequestAPI;
import sncr.bda.store.generic.schema.Action;
import sncr.bda.store.generic.schema.Category;
import sncr.bda.store.generic.schema.MetaDataStoreStructure;


public class SemanticServiceImpl implements SemanticService {

  private static final Logger logger = LoggerFactory.getLogger(SemanticServiceImpl.class);

  private DateFormat format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

  @Value("${metastore.base}")
  @NotNull
  private String basePath;

  @Override
  public SemanticNode addSemantic(SemanticNode node)
      throws JSONValidationSAWException, CreateEntitySAWException {
    logger.trace("Adding semantic with an Id : {}", node.get_id());
    node.setCreatedAt(format.format(new Date()));
    node.setCreatedBy(node.getUsername());
    try {
      NodeCategory nodeCategory = new NodeCategory();
      nodeCategory.setNodeCategory(nodeCategoryConvention);
      nodeCategory.setId(node.get_id());
      com.synchronoss.saw.semantic.model.Action internalAction =
          new com.synchronoss.saw.semantic.model.Action();
      internalAction.setVerb(Verb.CREATE);
      internalAction.setContent(node);
      nodeCategory.setAction(internalAction);
      List<MetaDataStoreStructure> structure = SAWSemanticUtils.node2JsonString(nodeCategory,
          basePath, node.get_id(), Action.CREATE, Category.SEMANTIC);
      logger.trace("Before invoking request to MaprDB JSON store :{}", structure);
      MetaDataStoreRequestAPI requestAPI = new MetaDataStoreRequestAPI(structure);
      requestAPI.process();
      node.setStatusMessage("Entity is created successfully");
    } catch (Exception ex) {
      logger.error("Problem on the storage while creating an entity", ex);
      throw new CreateEntitySAWException("Problem on the storage while creating an entity.", ex);
    }
    logger.debug("Response : " + node.toString());
    return node;
  }

  

  @Override
  public SemanticNode getDashboardbyCriteria(SemanticNode node)
      throws JSONValidationSAWException, ReadEntitySAWException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SemanticNode updateSemantic(SemanticNode node)
      throws JSONValidationSAWException, UpdateEntitySAWException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SemanticNode deleteSemantic(SemanticNode node)
      throws JSONValidationSAWException, DeleteEntitySAWException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SemanticNodes search(SemanticNode node)
      throws JSONValidationSAWException, ReadEntitySAWException {
    // TODO Auto-generated method stub
    return null;
  }


}

