package com.synchronoss.saw.dl.spark;

import com.synchronoss.bda.sip.dsk.SipDskAttribute;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.exceptions.SipDslProcessingException;
import com.synchronoss.saw.model.Aggregate;
import com.synchronoss.saw.model.Artifact;
import com.synchronoss.saw.model.Criteria;
import com.synchronoss.saw.model.DataSecurityKey;
import com.synchronoss.saw.model.DataSecurityKeyDef;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.Filter;
import com.synchronoss.saw.model.Join;
import com.synchronoss.saw.model.JoinCondition;
import com.synchronoss.saw.model.Model;
import com.synchronoss.saw.model.Model.Operator;
import com.synchronoss.saw.model.Model.Preset;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.model.SipQuery.BooleanCriteria;
import com.synchronoss.saw.model.Sort;
import com.synchronoss.saw.util.BuilderUtil;
import com.synchronoss.saw.util.DynamicConvertor;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
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
  public static final String CUSTOMER_CODE = "customerCode";
  private BooleanCriteria booleanCriteria;
  public static final String UPPER = "upper";
  public static final String IN = "IN";
  public static final String FROM = "FROM";
  public static final String SELECT = "SELECT";
  public static final String WHERE = "WHERE";
  public static final String AND = "AND";
  public static final String JOIN = "JOIN";
  public static final String SPACE = " ";
  public static final String ORDER_BY = "ORDER BY";

  List<String> groupByColumns = new ArrayList<>();

  public String buildDataQuery(SipQuery sipQuery) {
    groupByColumns.clear();
    booleanCriteria = sipQuery.getBooleanCriteria();
    String select = "SELECT ";
    List<String> selectList = buildSelect(sipQuery.getArtifacts());
    String finalSelect = String.join(", ", selectList);
    select = select.concat(finalSelect);
    select = select.concat(" FROM " + buildFrom(sipQuery));
    String filter = buildFilter(sipQuery.getFilters());
    if (filter != null && !StringUtils.isEmpty(filter)) {
      select = select.concat(" WHERE (").concat(filter).concat(")");
    }
    select = select.concat(buildGroupBy());

    return select.concat(
        buildSort(sipQuery.getSorts()).trim().isEmpty() == true
            ? ""
            : " ORDER BY " + buildSort(sipQuery.getSorts()));
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
                    Aggregate aggregate = field.getAggregate();
                    String artifactName = artifact.getArtifactsName();
                    String columnName = field.getColumnName();
                    String aliasName = field.getAlias();
                    if (aggregate != null && !aggregate.value().isEmpty()) {
                      aggCount.getAndIncrement();
                      if (aggregate == Aggregate.DISTINCTCOUNT) {
                        column = buildDistinctCount(artifactName, field);
                      } else if (aggregate == Aggregate.PERCENTAGE) {
                        column = buildForPercentage(artifactName, field);
                        groupByColumns.add(artifactName + "." + columnName);
                      } else {
                          column =
                              aggregate.value()
                                  + "("
                                  + artifactName
                                  + "."
                                  + columnName.replace(".keyword", "")
                                  + ")";
                          if (!StringUtils.isBlank(aliasName)) {
                            column += " AS `" + aliasName + "`";
                          }
                      }
                    } else {
                      column = artifactName + "." + columnName.replace(".keyword", "");
                      groupByColumns.add(column);

                      String alias = field.getAlias();
                      if (!StringUtils.isBlank(alias)) {
                        column = column + " AS `" + alias + "`";
                      }
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
          String joinOperator = "="; // default EQ.
          if (joinCondition.getOperator() != null
              && !joinCondition.getOperator().trim().isEmpty()) {
            String op = joinCondition.getOperator().trim();
            if (op.equalsIgnoreCase("NEQ")
                || op.equalsIgnoreCase("Not Equals")
                || op.equalsIgnoreCase("<>")
                || op.equalsIgnoreCase("!=")) {
              joinOperator = "<>";
            }
          }

          String condition =
              joinCondition.getLeft().getArtifactsName()
                  + "."
                  + joinCondition.getLeft().getColumnName()
                  + " "
                  + joinOperator
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
      Model model = filter.getModel();
      boolean inValidFilter = model == null ? true : model.isEmpty();
      if (filter.getType() == null) {
        throw new SipDslProcessingException("Filter Type is missing");
      } else if (!inValidFilter) {
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
      }
    }
    return StringUtils.join(whereFilters, " " + booleanCriteria.value() + " ");
  }

  /**
   * Build Actual query to be ran over background (DSK Included).
   *
   * @param sipQuery SipQuery Object
   * @param dataSecurityKey DataSecurityKey Object
   * @return String dsk included query
   */
  public String buildDskDataQuery(SipQuery sipQuery, DataSecurityKey dataSecurityKey) {
    booleanCriteria = sipQuery.getBooleanCriteria();
    String select = "SELECT ";
    List<String> selectList = buildSelect(sipQuery.getArtifacts());
    String selectWithJoin = String.join(", ", selectList);
    select = select.concat(selectWithJoin);
    select = select.concat(" FROM " + buildFrom(sipQuery));
    String filter = buildFilter(sipQuery.getFilters());
    if (filter != null && !StringUtils.isEmpty(filter)) {
      select = select.concat(" WHERE (").concat(filter).concat(")");
    }
    select = select.concat(queryDskBuilder(dataSecurityKey, sipQuery) + buildGroupBy());

    return select.concat(
        buildSort(sipQuery.getSorts()).trim().isEmpty() == true
            ? ""
            : " ORDER BY " + buildSort(sipQuery.getSorts()));
  }

  /**
   * @param dataSecurityKeyObj
   * @param sipQuery
   * @return
   */
  private String queryDskBuilder(DataSecurityKey dataSecurityKeyObj, SipQuery sipQuery) {
    String dskFilter = "";
    if (dataSecurityKeyObj.getDataSecuritykey() != null
        && dataSecurityKeyObj.getDataSecuritykey().size() != 0) {
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

    String dateFormat = null;
    if ((filter.getModel() != null)
        && (filter.getModel().getFormat() != null
            || filter.getModel().getGte() != null
            || filter.getModel().getLte() != null
            || filter.getModel().getPreset() != null
            || filter.getModel().getPresetCal() != null)) {
      dateFormat = filter.getModel().getFormat();
      dateFormat = dateFormat != null ? dateFormat : DATE_WITH_HOUR_MINUTES;
    }
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
    Preset preset = filter.getModel().getPreset();
    Operator operator = filter.getModel().getOperator();
    String gte = filter.getModel().getGte();
    String lte = filter.getModel().getLte();
    Double value = filter.getModel().getValue();
    Double otherValue = filter.getModel().getOtherValue();
    if (preset != null && !preset.value().equals(Model.Preset.NA.toString())) {
      DynamicConvertor dynamicConvertor = BuilderUtil.dynamicDecipher(preset.value());
      gte = dynamicConvertor.getGte();
      lte = dynamicConvertor.getLte();
      whereCond = setGteLteForDate(gte, lte, filter);
    } else if (filter.getModel().getPresetCal() != null) {
      DynamicConvertor dynamicConvertor =
          BuilderUtil.getDynamicConvertForPresetCal(filter.getModel().getPresetCal());
      gte = dynamicConvertor.getGte();
      lte = dynamicConvertor.getLte();
      whereCond = setGteLteForDate(gte, lte, filter);
    } else if ((preset.value().equals(Model.Preset.NA.toString())
            || (operator.equals(Operator.BTW)))
        && (gte != null || value != null)
        && (lte != null || otherValue != null)) {
      long gteInEpoch;
      long lteInEpoch;
      if (value == null && otherValue == null) {
        gteInEpoch =
            isMilli == true
                ? Long.parseLong(gte) / 1000
                /**
                 * Spark sql method : from_unixtime(<epoch_second>), accepts epoch second. So
                 * Converting milli to second
                 */
                : Long.parseLong(gte);
        lteInEpoch = isMilli == true ? Long.parseLong(lte) / 1000 : Long.parseLong(lte);
      } else {
        gteInEpoch = isMilli == true ? value.longValue() : value.longValue();
        lteInEpoch = isMilli == true ? otherValue.longValue() : otherValue.longValue();
      }
      Date date = new Date(gteInEpoch);
      DateFormat dateFormat = new SimpleDateFormat(DATE_WITH_HOUR_MINUTES);
      gte = dateFormat.format(date);
      date = new Date(lteInEpoch);
      lte = dateFormat.format(date);
      whereCond = setGteLteForDate(gte, lte, filter);
    }
    return whereCond;
  }

  /**
   * This util method is used to apply data ranges for filter(For both Preset and custom dates).
   *
   * @param filter Filter object
   * @return Where clause for date
   */
  private String dateFilterUtil(Filter filter) {
    String whereCond = null;
    Preset preset = filter.getModel().getPreset();
    Operator operator = filter.getModel().getOperator();
    String gte = filter.getModel().getGte();
    String lte = filter.getModel().getLte();
    if (preset != null && !preset.value().equals(Model.Preset.NA.toString())) {
      DynamicConvertor dynamicConvertor = BuilderUtil.dynamicDecipher(preset.value());
      gte = dynamicConvertor.getGte();
      lte = dynamicConvertor.getLte();
      whereCond = setGteLteForDate(gte, lte, filter);
    } else if (filter.getModel().getPresetCal() != null) {
      DynamicConvertor dynamicConvertor =
          BuilderUtil.getDynamicConvertForPresetCal(filter.getModel().getPresetCal());
      gte = dynamicConvertor.getGte();
      lte = dynamicConvertor.getLte();
      whereCond = setGteLteForDate(gte, lte, filter);
    } else if ((preset.value().equals(Model.Preset.NA.toString())
            || (operator.equals(Operator.BTW)))
        && gte != null
        && lte != null) {
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
            + " <= TO_DATE(date_add('"
            + lte
            + "',1))";
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
            whereClause.concat(" >= " + filter.getModel().getOtherValue())
                + " AND "
                + filter.getArtifactsName()
                + "."
                + filter.getColumnName()
                + " <= "
                + filter.getModel().getValue();
        break;
    }

    return whereClause;
  }

  private String buildStringFilter(Filter filter) {
    String whereClause = filter.getArtifactsName() + "." + filter.getColumnName();

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
            whereClause.concat(" IN (" + joinString(filter.getModel().getModelValues()) + ") ");
        break;
      case ISNOTIN:
        whereClause =
            whereClause.concat(" NOT IN (" + joinString(filter.getModel().getModelValues()) + ") ");
        break;
      case CONTAINS:
        whereClause =
            whereClause.concat(" like '%" + filter.getModel().getModelValues().get(0) + "%' ");
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
        switch (sort.getAggregate()) {
          case DISTINCTCOUNT:
            order = "`distinctCount(" + sort.getColumnName() + ")`";
            break;
          case PERCENTAGE:
            order = "`percentage(" + sort.getColumnName() + ")`";
            break;
          default:
            order =
                sort.getAggregate()
                    + "("
                    + sort.getArtifactsName()
                    + "."
                    + sort.getColumnName()
                    + ") "
                    + sort.getOrder();
        }
      } else {
        order = sort.getArtifactsName() + "." + sort.getColumnName() + " " + sort.getOrder();
      }
      sortsList.add(order);
    }
    return (String.join(", ", sortsList));
  }

  private String buildDistinctCount(String artifactName, Field field) {
    String columnName = field.getColumnName().replace(".keyword", "");
    String column = null;

    String aliasName = field.getAlias();
    column =
        "count(distinct "
            + artifactName
            + "."
            + field.getColumnName().replace(".keyword", "")
            + ") as ";

    StringBuilder aliasBuilder = new StringBuilder();
    aliasBuilder.append("`");

    if (StringUtils.isBlank(aliasName)) {
      aliasBuilder.append("distinctCount(").append(field.getColumnName()).append(")");
    } else {
      aliasBuilder.append(aliasName);
    }

    aliasBuilder.append("`");

    column += aliasBuilder.toString();
    return column;
  }

  private String onlyYearFilter(Filter filter) {
    GregorianCalendar startDate;
    String whereClause = null;
    String gte = filter.getModel().getGte();
    String lte = filter.getModel().getLte();
    String gt = filter.getModel().getGt();
    String lt = filter.getModel().getLt();
    Double value = filter.getModel().getValue();
    Double otherValue = filter.getModel().getOtherValue();

    if (gte != null) {
      int year = value == null ? Integer.parseInt(gte) : value.intValue();
      startDate = new GregorianCalendar(year, 0, 1, 0, 0, 0);
      whereClause = " >= TO_DATE('" + startDate + "')";
    } else if (gt != null) {
      int year = value == null ? Integer.parseInt(gt) : value.intValue();
      startDate = new GregorianCalendar(year, 0, 1, 0, 0, 0);
      whereClause = " > TO_DATE('" + startDate + "')";
    } else if (lte != null) {
      int year = value == null ? Integer.parseInt(lte) : value.intValue();
      startDate = new GregorianCalendar(year, 0, 1, 0, 0, 0);
      whereClause = " <= TO_DATE('" + startDate + "')";
    } else if (lt != null) {
      int year = value == null ? Integer.parseInt(lt) : value.intValue();
      startDate = new GregorianCalendar(year, 0, 1, 0, 0, 0);
      whereClause = " < TO_DATE('" + startDate + "')";
    }
    return whereClause;
  }

  private String buildForPercentage(String artifactName, Field field) {
    String buildPercentage = "";
    String columnName = field.getColumnName();
    String aliasName = field.getAlias();
    if (artifactName != null && !artifactName.trim().isEmpty() && columnName != null) {
      buildPercentage =
          buildPercentage.concat(
              "("
                  + artifactName
                  + "."
                  + columnName
                  + "*100)/(Select sum("
                  + artifactName
                  + "."
                  + columnName
                  + ") FROM "
                  + artifactName
                  + ") as ");

      StringBuilder aliasBuilder = new StringBuilder();
      if (StringUtils.isBlank(aliasName)) {
        aliasBuilder
            .append("`")
            .append("percentage")
            .append("(")
            .append(columnName)
            .append(")")
            .append("`");
      } else {
        aliasBuilder.append("`").append(aliasName).append("`");
      }

      buildPercentage += aliasBuilder.toString();
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

  String joinString(List<Object> strList) {
    Function<Object, String> addQuotes = s -> "\'" + s + "\'";
    return strList.stream().map(addQuotes).collect(Collectors.joining(", "));
  }

  public static String dskForManualQuery(
      SipQuery sipQuery, String query, SipDskAttribute attribute) {
    StringBuffer dskFilter13 = new StringBuffer();
    dskFilter13.append(" (").append(SELECT).append(" * ").append(" ").append(FROM).append(" ");
    if (attribute != null
        && (attribute.getBooleanCriteria() != null || attribute.getBooleanQuery() != null)) {
      logger.info("DSK attribute  :{}", attribute);
      List<Artifact> artifacts = sipQuery.getArtifacts();
      for (Artifact artifact : artifacts) {
        String artifactName = artifact.getArtifactsName();
        StringBuffer dskFilter = new StringBuffer();
        dskFilter.append(String.format(" (SELECT * FROM %s WHERE ", artifactName));
        if (query.toUpperCase().contains(artifactName)) {
          String dskFormedQuery = dskQueryForArtifact(attribute, artifactName);
          logger.info("dskformed query = " + dskFormedQuery);
          if (dskFormedQuery != null && !StringUtils.isEmpty(dskFormedQuery)) {
            dskFilter = dskFilter.append(dskFormedQuery).append(" ) as " + artifactName + " ");
            query = query + " ";
            String artName = "FROM " + artifactName;
            logger.trace("dskFilter str = " + dskFilter);
            /*added below line for SIP-9839 ,for  $ character in string the replacement(replaceAll method) replacing $ as well
            ,so to handle that  replacing $ with escapecharacter and $ */
            String dskFilterString = dskFilter.toString().replaceAll("\\$", "\\\\\\$");
            query =
                query
                    .trim()
                    .replaceAll("\\s{2,}", " ")
                    .replaceAll("(?i)" + artName.toUpperCase(), "FROM " + dskFilterString);
            String artName1 = "JOIN " + artifactName;
            query = query.replaceAll("(?i)" + artName1.toUpperCase(), "JOIN " + dskFilterString);
            logger.info("Logged query : " + query);
          }
        }
      }
    }

    logger.info("DSK applied Query : " + query);
    return query;
  }

  public static String dskQueryForArtifact(SipDskAttribute attribute, String arifactName) {
    boolean flag = true;
    String boolenaCriteria = null;
    StringBuilder dskquery = new StringBuilder();
    if (attribute == null) {
      return dskquery.toString();
    }
    if (attribute.getBooleanCriteria() == null && attribute.getBooleanQuery() == null) {
      logger.error("Invalid dsk object");
      return dskquery.toString();
    }
    if (attribute.getBooleanCriteria() != null) {
      boolenaCriteria = " " + attribute.getBooleanCriteria() + " ";
    }
    for (SipDskAttribute dskAttribute : attribute.getBooleanQuery()) {
      if (dskAttribute.getBooleanQuery() != null) {
        String childQuery = dskQueryForArtifact(dskAttribute, arifactName);
        if (childQuery != null && !StringUtils.isEmpty(childQuery)) {
          if (dskquery != null && dskquery.length() > 0) {
            dskquery.append(boolenaCriteria);
          }
          dskquery.append(childQuery);
          flag = false;
        }
      } else {
        String columnName = dskAttribute.getColumnName();
        com.synchronoss.bda.sip.dsk.Model model = dskAttribute.getModel();
        if (!flag) {
          dskquery.append(boolenaCriteria);
        }
        if (!StringUtils.containsIgnoreCase(columnName, arifactName)) {
          columnName = arifactName.concat(".").concat(columnName);
        }
        dskquery = prepareQueryWithCondition(columnName, model, dskquery);
        flag = false;
      }
    }
    if (dskquery.length() != 0) {
      dskquery.insert(0, "(").append(")");
    }
    return dskquery.toString();
  }

  private static StringBuilder prepareQueryWithCondition(
      String columnName, com.synchronoss.bda.sip.dsk.Model model, StringBuilder dskquery) {
    dskquery.append(UPPER).append("(" + columnName.trim() + ")");
    com.synchronoss.bda.sip.dsk.Operator operator = model.getOperator();
    List<String> values = model.getValues();
    switch (operator) {
      case EQ:
        {
          dskquery.append(" = ").append(UPPER).append("('" + values.get(0) + "')");
          break;
        }
      case ISIN:
        {
          dskquery.append(" ").append(IN).append(" (");
          int initFlag = 0;
          for (String value : values) {
            dskquery = initFlag != 0 ? dskquery.append(", ") : dskquery;
            dskquery = dskquery.append(UPPER).append("('" + value.trim() + "')");
            initFlag++;
          }
          dskquery.append(" )");
          break;
        }
    }
    return dskquery;
  }

  /**
   * Build Actual query to be ran over background (DSK Included).
   *
   * @param sipQuery SipQuery Object
   * @param sipDskAttribute DataSecurityKey Object
   * @return sipQuery semantic sipQuery
   */
  public String buildDskQuery(SipQuery sipQuery, SipDskAttribute sipDskAttribute) {
    booleanCriteria = sipQuery.getBooleanCriteria();
    StringBuilder select = new StringBuilder(SELECT).append(SPACE);
    List<String> selectList = buildSelect(sipQuery.getArtifacts());
    String selectWithJoin = String.join(", ", selectList);
    select
        .append(selectWithJoin)
        .append(SPACE)
        .append(FROM)
        .append(SPACE)
        .append(buildFrom(sipQuery));
    String filter = buildFilter(sipQuery.getFilters());
    if (filter != null && !StringUtils.isEmpty(filter)) {
      select
          .append(SPACE)
          .append(WHERE)
          .append(SPACE)
          .append("(")
          .append(filter)
          .append(")")
          .append(SPACE);
    }
    select.append(queryDskBuilder(sipDskAttribute, sipQuery)).append(buildGroupBy());

    String sort = buildSort(sipQuery.getSorts());
    if (sort != null && !StringUtils.isEmpty(sort)) {
      select.append(SPACE).append(ORDER_BY).append(SPACE).append(sort);
    }
    return select.toString();
  }

  /**
   * @param sipDskAttribute
   * @param sipQuery
   * @param sipQuery
   * @return
   */
  public static String queryDskBuilder(SipDskAttribute sipDskAttribute, SipQuery sipQuery) {
    StringBuilder dskFilter = new StringBuilder();
    if (sipDskAttribute != null
        && (sipDskAttribute.getBooleanCriteria() != null
            || sipDskAttribute.getBooleanQuery() != null)) {

      String condition = null;
      if (sipQuery.getFilters() != null && sipQuery.getFilters().size() > 0) {
        condition = AND;
      } else {
        condition = WHERE;
      }
      List<Artifact> artifacts = sipQuery.getArtifacts();
      for (Artifact artifact : artifacts) {
        String artifactName = artifact.getArtifactsName();
        String dskFormedQuery = dskQueryForArtifact(sipDskAttribute, artifactName);
        if (dskFormedQuery != null && !StringUtils.isEmpty(dskFormedQuery)) {
          dskFilter.append(" ").append(condition).append(" ");
          dskFilter.append(" ").append(dskFormedQuery);
          condition = AND;
        }
      }
    }
    return dskFilter.toString();
  }
}
