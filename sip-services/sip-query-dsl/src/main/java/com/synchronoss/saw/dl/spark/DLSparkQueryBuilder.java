package com.synchronoss.saw.dl.spark;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.synchronoss.saw.exceptions.SipDslProcessingException;
import com.synchronoss.saw.model.Artifact;
import com.synchronoss.saw.model.Criteria;
import com.synchronoss.saw.model.Join;
import com.synchronoss.saw.model.JoinCondition;
import com.synchronoss.saw.model.SIPDSL;
import com.synchronoss.saw.model.SipQuery;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DLSparkQueryBuilder {

  public static final Logger logger = LoggerFactory.getLogger(DLSparkQueryBuilder.class);
  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd";

  String dataSecurityString;

  public String buildDataQuery(SIPDSL sipdsl) {
    SipQuery sipQuery = sipdsl.getSipQuery();
    sipQuery.getArtifacts();
    return null;
  }

  /**
   * @param artifactList
   * @return
   */
  public List<String> buildSelect(List<Artifact> artifactList) {
    List<String> selectColumns = new ArrayList<>();
    List<String> groupByColumns = new ArrayList<>();
    artifactList.forEach(
        artifact -> {
          artifact
              .getFields()
              .forEach(
                  field -> {
                    String column = null;
                    if (field.getAggregate() != null && !field.getAggregate().value().isEmpty()) {
                      column =
                          field.getAggregate().value()
                              + "("
                              + artifact.getArtifactsName()
                              + "."
                              + field.getColumnName()
                              + ")";
                    } else {
                      column = artifact.getArtifactsName() + "." + field.getColumnName();
                      groupByColumns.add(column);
                    }
                    selectColumns.add(column);
                  });
        });
    return selectColumns;
  }

  /**
   * @param sipQuery
   * @return
   */
  private String buildFrom(SipQuery sipQuery) {
    if (sipQuery.getJoins() != null && sipQuery.getJoins().size() > 0) {
      for (Join join : sipQuery.getJoins()) {
        List<String> joinRelation = new ArrayList<>();
        Relation2<String, String> tablesRelation = null;
        for (Criteria criteria : join.getCriteria()) {
          JoinCondition joinCondition = criteria.getJoinCondition();
          String condition =
              joinCondition.getLeft().getArtifactsName()
                  + "."
                  + joinCondition.getLeft().getColumnName()
                  + joinCondition.getOperator()
                  + joinCondition.getRight().getArtifactsName()
                  + "."
                  + joinCondition.getRight().getColumnName();
          if (tablesRelation != null
              && !(tablesRelation.equals(
                  new Relation2<>(
                      joinCondition.getLeft().getArtifactsName(),
                      joinCondition.getRight().getArtifactsName()))))
            throw new SipDslProcessingException("Join artifact name for the criteria is not same");
          new Relation2<>(
              joinCondition.getLeft().getArtifactsName(),
              joinCondition.getRight().getArtifactsName());
          joinRelation.add(condition);
        }
        String conditions = StringUtils.join(joinRelation, "AND");
        String.format(
            "%s %s JOIN %s ON %s ",
            tablesRelation._1,
            join.getJoinType().value().toUpperCase(),
            tablesRelation._2,
            conditions);
      }
    } else {

    }
    return null;
  }

  private String buildFilter(){
      return null;
  }

  private String buildSort(){
      return null;
  }

  private String buildAggragation() {
      return null;
  }

}
