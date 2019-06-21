package com.synchronoss.saw.dl.spark;

import com.synchronoss.saw.exceptions.SipDslProcessingException;
import com.synchronoss.saw.model.Artifact;
import com.synchronoss.saw.model.Criteria;
import com.synchronoss.saw.model.DataSecurityKey;
import com.synchronoss.saw.model.DataSecurityKeyDef;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.Field.Aggregate;
import com.synchronoss.saw.model.Filter;
import com.synchronoss.saw.model.Join;
import com.synchronoss.saw.model.JoinCondition;
import com.synchronoss.saw.model.Model;
import com.synchronoss.saw.model.Model.Operator;
import com.synchronoss.saw.model.SIPDSL;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.model.Sort;
import com.synchronoss.saw.util.BuilderUtil;
import com.synchronoss.saw.util.DynamicConvertor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DLSparkQueryBuilder {

  public static final Logger logger = LoggerFactory.getLogger(DLSparkQueryBuilder.class);
  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd";
  private static final String DATE_ONLY_FORMAT = "yyyy-MM-dd";
  private static final String DATE_WITH_HOUR_MINUTES = "yyyy-MM-dd HH:mm:ss";
  private static final String ONLY_YEAR_FORMAT = "YYYY";
  private static final String EPOCH_SECOND = "epoch_second";
  private static final String EPOCH_MILLIS = "epoch_millis";

  List<String> groupByColumns = new ArrayList<>();

  public String buildDataQuery(SIPDSL sipdsl) {
    SipQuery sipQuery = sipdsl.getSipQuery();
    sipQuery.getArtifacts();
    String select = "SELECT ";
    List<String> selectList = buildSelect(sipQuery.getArtifacts());
    String finalSelect = String.join(", ", selectList);
    select = select.concat(finalSelect);
    select =
        select.concat(
            " FROM " + buildFrom(sipQuery) + buildFilter(sipQuery.getFilters()) + buildGroupBy());

    return select.concat(
        buildSort(sipdsl.getSipQuery().getSorts()).trim().isEmpty() == true
            ? ""
            : " ORDER BY " + buildSort(sipdsl.getSipQuery().getSorts()));
  }

  /**
   * @param artifactList
   * @return
   */
  public List<String> buildSelect(List<Artifact> artifactList) {
    AtomicInteger aggCount = new AtomicInteger();
    List<String> selectColumns = new ArrayList<>();
    artifactList.forEach(
        artifact -> {
          artifact
              .getFields()
              .forEach(
                  field -> {
                    String column = null;
                    if (field.getAggregate() != null && !field.getAggregate().value().isEmpty()) {
                      aggCount.getAndIncrement();
                      if (field.getAggregate() == Aggregate.DISTINCTCOUNT) {
                        column = buildDistinctCount(artifact.getArtifactsName(), field);
                      } else if (field.getAggregate() == Aggregate.PERCENTAGE) {
                        column = buildForPercentage(artifact.getArtifactsName(), field);
                        groupByColumns.add(
                            artifact.getArtifactsName() + "." + field.getColumnName());
                      } else {
                        column =
                            field.getAggregate().value()
                                + "("
                                + artifact.getArtifactsName()
                                + "."
                                + field.getColumnName().replace(".keyword", "")
                                + ")";
                      }
                    } else {
                      column =
                          artifact.getArtifactsName()
                              + "."
                              + field.getColumnName().replace(".keyword", "");
                      groupByColumns.add(column);
                    }
                    selectColumns.add(column);
                  });
        });
    if (aggCount.get() == 0) {
      groupByColumns.clear(); // If aggregartion is not present Group By should not be set.
    }
    return selectColumns;
  }

  /**
   * @param sipQuery
   * @return
   */
  private String buildFrom(SipQuery sipQuery) {
    String fromString = null;
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
                  + " "
                  + joinCondition.getOperator()
                  + " "
                  + joinCondition.getRight().getArtifactsName()
                  + "."
                  + joinCondition.getRight().getColumnName();
          if (tablesRelation != null
              && !(tablesRelation.equals(
                  new Relation2<>(
                      joinCondition.getLeft().getArtifactsName(),
                      joinCondition.getRight().getArtifactsName()))))
            throw new SipDslProcessingException("Join artifact name for the criteria is not same");
          tablesRelation =
              new Relation2<>(
                  joinCondition.getLeft().getArtifactsName(),
                  joinCondition.getRight().getArtifactsName());
          joinRelation.add(condition);
        }
        String conditions = StringUtils.join(joinRelation, "AND");
        fromString =
            String.format(
                "%s %s JOIN %s ON %s",
                tablesRelation._1,
                join.getJoinType().value().toUpperCase(),
                tablesRelation._2,
                conditions);
      }
    } else {
      if (sipQuery.getArtifacts().size() > 2) {
        throw new SipDslProcessingException("Join is missing");
      }
      fromString = String.format("%s", sipQuery.getArtifacts().get(0).getArtifactsName());
    }
    return fromString;
  }

  /**
   * Build sql query for Filters.
   *
   * @param filterList {@link List} of {@link Filter}
   * @return String where clause
   */
  private String buildFilter(List<Filter> filterList) {
    List<String> whereFilters = new ArrayList<>();
    for (Filter filter : filterList) {
      if (filter.getType() != null) {
        switch (filter.getType()) {
          case DATE:
            whereFilters.add(buildDateTimestampFilter(filter));
            break;
          case TIMESTAMP:
            whereFilters.add(buildDateTimestampFilter(filter));
            break;
          case DOUBLE:
            whereFilters.add(buildNumericFilter(filter));
            break;
          case FLOAT:
            whereFilters.add(buildNumericFilter(filter));
            break;
          case LONG:
            whereFilters.add(buildNumericFilter(filter));
            break;
          case INTEGER:
            whereFilters.add(buildNumericFilter(filter));
            break;
          case STRING:
            whereFilters.add(buildStringFilter(filter));
            break;
        }
      } else {
        throw new SipDslProcessingException("Filter Type is missing");
      }
    }
    String firstFilter = null;
    if (whereFilters.size() != 0) {
      firstFilter = whereFilters.get(0);
      whereFilters.remove(0);
      whereFilters.add(0, " WHERE " + firstFilter);
    }
    String whereConditions = StringUtils.join(whereFilters, " AND ");
    return whereConditions;
  }

  /**
   * Build Actual query to be ran over background (DSK Included).
   *
   * @param sipdsl SIPDSL Object
   * @param dataSecurityKey DataSecurityKey Object
   * @return String dsk included query
   */
  public String buildDskDataQuery(SIPDSL sipdsl, DataSecurityKey dataSecurityKey) {
    SipQuery sipQuery = sipdsl.getSipQuery();
    sipQuery.getArtifacts();
    String select = "SELECT ";
    List<String> selectList = buildSelect(sipQuery.getArtifacts());
    String selectWithJoin = String.join(", ", selectList);
    select = select.concat(selectWithJoin);
    select =
        select.concat(
            " FROM "
                + buildFrom(sipQuery)
                + buildFilter(sipQuery.getFilters())
                + queryDskBuilder(dataSecurityKey, sipQuery)
                + buildGroupBy());

    return select.concat(
        buildSort(sipdsl.getSipQuery().getSorts()).trim().isEmpty() == true
            ? ""
            : " ORDER BY " + buildSort(sipdsl.getSipQuery().getSorts()));
  }

  /**
   * @param dataSecurityKeyObj
   * @param sipQuery
   * @return
   */
  private String queryDskBuilder(DataSecurityKey dataSecurityKeyObj, SipQuery sipQuery) {
    String dskFilter = "";
    if (dataSecurityKeyObj != null) {
      if (buildFilter(sipQuery.getFilters()).trim().isEmpty()) {
        dskFilter = " WHERE ";
      } else {
        dskFilter = " AND ";
      }
      int dskFlag = 0;

      if (dataSecurityKeyObj.getDataSecuritykey() != null
          && dataSecurityKeyObj.getDataSecuritykey().size() > 0) {
        for (DataSecurityKeyDef dsk : dataSecurityKeyObj.getDataSecuritykey()) {
          dskFilter = dskFlag != 0 ? dskFilter.concat(" AND ") : dskFilter;
          dskFilter = dskFilter.concat(dsk.getName() + " in (");
          List<String> values = dsk.getValues();
          int initFlag = 0;
          for (String value : values) {
            dskFilter = initFlag != 0 ? dskFilter.concat(", ") : dskFilter;
            dskFilter = dskFilter.concat("'" + value + "'");
            initFlag++;
          }
          dskFilter = dskFilter.concat(")");
          dskFlag++;
        }
      }
    }
    return dskFilter;
  }

  /**
   * Adding sql support for different date formats we support.
   *
   * @param filter Filter Object
   * @return String filter query
   */
  private String buildDateTimestampFilter(Filter filter) {
    String whereClause = filter.getArtifactsName() + "." + filter.getColumnName();
    Operator op = filter.getModel().getOperator();
    String dateFormat = filter.getModel().getFormat();
    if (dateFormat != null) {
      switch (dateFormat) {
        case DATE_ONLY_FORMAT:
          whereClause = whereClause.concat(dateFilterUtil(filter));
          break;
        case DATE_WITH_HOUR_MINUTES:
          whereClause = whereClause.concat(dateFilterUtil(filter));
          break;
        case EPOCH_MILLIS:
          whereClause =
              "from_unixtime(" + filter.getArtifactsName() + "." + filter.getColumnName() + ")";
          whereClause = whereClause.concat(epochDateFilterUtil(filter, true));
          break;
        case EPOCH_SECOND:
          whereClause =
              "from_unixtime(" + filter.getArtifactsName() + "." + filter.getColumnName() + ")";
          whereClause = whereClause.concat(epochDateFilterUtil(filter, false));
          break;
        case ONLY_YEAR_FORMAT:
          whereClause = whereClause.concat(onlyYearFilter(filter));
          break;
      }
    }
    return whereClause;
  }

  /**
   * @param filter
   * @param isMilli
   * @return
   */
  private String epochDateFilterUtil(Filter filter, boolean isMilli) {
    String whereCond = null;
    if (filter.getModel().getPreset() != null
        || filter.getModel().getPreset().value().equals(Model.Preset.NA.toString())) {
      DynamicConvertor dynamicConvertor =
          BuilderUtil.dynamicDecipher(filter.getModel().getPreset().value());
      String gte = dynamicConvertor.getGte();
      String lte = dynamicConvertor.getLte();
      whereCond = setGteLteForDate(gte, lte, filter);
    } else if ((filter.getModel().getPreset().value().equals(Model.Preset.NA.toString())
            || (filter.getModel().getOperator().equals(Operator.BTW)))
        && filter.getModel().getGte() != null
        && filter.getModel().getLte() != null) {
      long gteInEpoch;
      long lteInEpoch;
      if (filter.getModel().getValue() == null && filter.getModel().getOtherValue() == null) {
        gteInEpoch =
            isMilli == true
                ? Long.parseLong(filter.getModel().getGte()) / 1000
                /**
                 * Spark sql method : from_unixtime(<epoch_second>), accepts epoch second. So
                 * Converting milli to second
                 */
                : Long.parseLong(filter.getModel().getGte());
        lteInEpoch =
            isMilli == true
                ? Long.parseLong(filter.getModel().getLte()) / 1000
                : Long.parseLong(filter.getModel().getLte());
      } else {
        gteInEpoch =
            isMilli == true
                ? filter.getModel().getValue().longValue() / 1000
                : filter.getModel().getValue().longValue();
        lteInEpoch =
            isMilli == true
                ? filter.getModel().getOtherValue().longValue() / 1000
                : filter.getModel().getOtherValue().longValue();
      }
      Date date = new Date(gteInEpoch);
      DateFormat dateFormat = new SimpleDateFormat(DATE_WITH_HOUR_MINUTES);
      String gte = dateFormat.format(date);
      date = new Date(lteInEpoch);
      String lte = dateFormat.format(date);
      whereCond = setGteLteForDate(gte, lte, filter);
    }
    return whereCond;
  }

  private String dateFilterUtil(Filter filter) {
    String whereCond = null;
    if (filter.getModel().getPreset() != null
        || filter.getModel().getPreset().value().equals(Model.Preset.NA.toString())) {
      DynamicConvertor dynamicConvertor =
          BuilderUtil.dynamicDecipher(filter.getModel().getPreset().value());
      String gte = dynamicConvertor.getGte();
      String lte = dynamicConvertor.getLte();
      whereCond = setGteLteForDate(gte, lte, filter);
    } else if ((filter.getModel().getPreset().value().equals(Model.Preset.NA.toString())
            || (filter.getModel().getOperator().equals(Operator.BTW)))
        && filter.getModel().getGte() != null
        && filter.getModel().getLte() != null) {
      whereCond = setGteLteForDate(filter.getModel().getGte(), filter.getModel().getLte(), filter);
    }
    return whereCond;
  }

  private String setGteLteForDate(String gte, String lte, Filter filter) {
    String whereCond =
        " >= TO_DATE('"
            + gte
            + "') AND "
            + filter.getArtifactsName()
            + "."
            + filter.getColumnName()
            + " <= TO_DATE('"
            + lte
            + "')";
    return whereCond;
  }

  private String buildNumericFilter(Filter filter) {
    String whereClause = filter.getArtifactsName() + "." + filter.getColumnName();

    Operator op = filter.getModel().getOperator();

    switch (op) {
      case GTE:
        whereClause = whereClause.concat(" >= " + filter.getModel().getValue());
        break;
      case LTE:
        whereClause = whereClause.concat(" <= " + filter.getModel().getValue());
        break;
      case GT:
        whereClause = whereClause.concat(" > " + filter.getModel().getValue());
        break;
      case LT:
        whereClause = whereClause.concat(" < " + filter.getModel().getValue());
        break;
      case EQ:
        whereClause = whereClause.concat(" = " + filter.getModel().getValue());
        break;
      case NEQ:
        whereClause = whereClause.concat(" <> " + filter.getModel().getValue());
        break;
      case BTW:
        whereClause =
            whereClause.concat(
                " >= "
                    + filter.getModel().getValue()
                    + " AND <= "
                    + filter.getModel().getOtherValue());
        break;
    }

    return whereClause;
  }

  private String buildStringFilter(Filter filter) {
    String whereClause = "upper(" + filter.getArtifactsName() + "." + filter.getColumnName() + ")";

    Operator op = filter.getModel().getOperator();

    switch (op) {
      case SW:
        whereClause =
            whereClause.concat(" like '" + filter.getModel().getModelValues().get(0) + "%' ");
        break;
      case EW:
        whereClause =
            whereClause.concat(" like '%" + filter.getModel().getModelValues().get(0) + "' ");
        break;
      case ISIN:
        whereClause =
            whereClause.concat(" like '%" + filter.getModel().getModelValues().get(0) + "%' ");
        break;
      case ISNOTIN:
        whereClause =
            whereClause.concat(" NOT IN ('" + filter.getModel().getModelValues().get(0) + "') ");
        break;
      case CONTAINS:
        whereClause =
            whereClause.concat(" IN ('" + filter.getModel().getModelValues().get(0) + "') ");
        break;
      case EQ:
        whereClause = whereClause.concat(" = '" + filter.getModel().getModelValues().get(0) + "' ");
        break;
      case NEQ:
        whereClause =
            whereClause.concat(" <> '" + filter.getModel().getModelValues().get(0) + "' ");
        break;
    }
    return whereClause;
  }

  private String buildSort(List<Sort> sorts) {
    List<String> sortsList = new ArrayList<>();
    String order;
    for (Sort sort : sorts) {
      if (sort.getAggregate() != null) {
        order =
            sort.getAggregate()
                + "("
                + sort.getArtifacts()
                + "."
                + sort.getColumnName()
                + ") "
                + sort.getOrder();
      } else {
        order = sort.getArtifacts() + "." + sort.getColumnName() + " " + sort.getOrder();
      }
      sortsList.add(order);
    }
    return (String.join(", ", sortsList));
  }

  private String buildDistinctCount(String artifactName, Field field) {
    String column =
        "count(distinct "
            + artifactName
            + "."
            + field.getColumnName().replace(".keyword", "")
            + ") as `distinctCount("
            + field.getColumnName()
            + ")`";
    return column;
  }

  private String onlyYearFilter(Filter filter) {
    GregorianCalendar startDate;
    String whereClause = null;
    if (filter.getModel().getGte() != null) {
      int year =
          filter.getModel().getValue() == null
              ? Integer.parseInt(filter.getModel().getGte())
              : filter.getModel().getValue().intValue();
      startDate = new GregorianCalendar(year, 0, 1, 0, 0, 0);
      whereClause = " >= TO_DATE('" + startDate + "')";
    } else if (filter.getModel().getGt() != null) {
      int year =
          filter.getModel().getValue() == null
              ? Integer.parseInt(filter.getModel().getGt())
              : filter.getModel().getValue().intValue();
      startDate = new GregorianCalendar(year, 0, 1, 0, 0, 0);
      whereClause = " > TO_DATE('" + startDate + "')";
    } else if (filter.getModel().getLte() != null) {
      int year =
          filter.getModel().getValue() == null
              ? Integer.parseInt(filter.getModel().getLte())
              : filter.getModel().getValue().intValue();
      startDate = new GregorianCalendar(year, 0, 1, 0, 0, 0);
      whereClause = " <= TO_DATE('" + startDate + "')";
    } else if (filter.getModel().getLt() != null) {
      int year =
          filter.getModel().getValue() == null
              ? Integer.parseInt(filter.getModel().getLt())
              : filter.getModel().getValue().intValue();
      startDate = new GregorianCalendar(year, 0, 1, 0, 0, 0);
      whereClause = " < TO_DATE('" + startDate + "')";
    }
    return whereClause;
  }

  private String buildForPercentage(String artifactName, Field field) {
    String buildPercentage = "";
    if (artifactName != null && !artifactName.trim().isEmpty() && field.getColumnName() != null) {
      buildPercentage =
          buildPercentage.concat(
              "("
                  + artifactName
                  + "."
                  + field.getColumnName()
                  + "*100)/(Select sum("
                  + artifactName
                  + "."
                  + field.getColumnName()
                  + ") FROM "
                  + artifactName
                  + ") as `percentage("
                  + field.getColumnName()
                  + ")`");
    }

    return buildPercentage;
  }

  /**
   * Build Group by clause.
   *
   * @return String group by clause
   */
  public String buildGroupBy() {
    String groupBy = "";
    if (groupByColumns != null && groupByColumns.size() > 0) {
      groupBy = " GROUP BY " + String.join(", ", groupByColumns);
    }

    return groupBy;
  }
}
