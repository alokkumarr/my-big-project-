package com.synchronoss.saw.dl.spark;

import static com.synchronoss.saw.util.BuilderUtil.buildNestedFilter;

import com.synchronoss.bda.sip.dsk.SipDskAttribute;
import com.synchronoss.saw.exceptions.SipDslProcessingException;
import com.synchronoss.saw.model.Aggregate;
import com.synchronoss.saw.model.Artifact;
import com.synchronoss.saw.model.Criteria;
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
import org.springframework.util.CollectionUtils;

public class DLSparkQueryBuilder {

  public static final Logger logger = LoggerFactory.getLogger(DLSparkQueryBuilder.class);
  private static final String DATE_ONLY_FORMAT = "yyyy-MM-dd";
  private static final String DATE_WITH_HOUR_MINUTES = "yyyy-MM-dd HH:mm:ss";
  private static final String ONLY_YEAR_FORMAT = "YYYY";
  private static final String EPOCH_SECOND = "epoch_second";
  private static final String EPOCH_MILLIS = "epoch_millis";
  private BooleanCriteria booleanCriteria;
  public static final String UPPER = "upper";
  public static final String IN = "IN";
  public static final String FROM = "FROM";
  public static final String SELECT = "SELECT";
  public static final String WHERE = "WHERE";
  public static final String AND = "AND";
  public static final String SPACE = " ";
  public static final String ORDER_BY = "ORDER BY";
  public static final String LIKE = " like ";
  public static final String DOT_KEYWORD = ".keyword";

  List<String> groupByColumns = new ArrayList<>();

  public String buildDataQuery(SipQuery sipQuery) {
    StringBuilder filterQuery ;
    groupByColumns.clear();
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
    List<Filter> filters = sipQuery.getFilters();

    Filter fil = buildNestedFilter(filters,booleanCriteria);
    filterQuery = buildSipFilters(fil);

    String filter = filterQuery.toString();
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
    select = select.append(buildGroupBy());
    String sort = buildSort(sipQuery.getSorts());
    if (sort != null && !StringUtils.isEmpty(sort)) {
      select.append(SPACE).append(ORDER_BY).append(SPACE).append(sort);
    }
    return select.toString();
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
                                  + columnName.replace(DOT_KEYWORD, "")
                                  + ")";
                          if (!StringUtils.isBlank(aliasName)) {
                            column += " AS `" + aliasName + "`";
                          }
                      }
                    } else {
                      column = artifactName + "." + columnName.replace(DOT_KEYWORD, "");
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
   * @param  {@link List} of {@link Filter}
   * @return String where clause
   */
  private StringBuilder buildFilterUtil(Filter sipFilter, StringBuilder filterQuery) {
    Filter filter = sipFilter;
    Model model = filter.getModel();
    boolean inValidFilter = model == null ? true : model.isEmpty();
    if (filter.getType() == null) {
      throw new SipDslProcessingException("Filter Type is missing");
    } else if (!inValidFilter) {
      switch (filter.getType()) {
        case DATE:
          filterQuery = buildDateTimestampFilter(filter, filterQuery);
          break;
        case TIMESTAMP:
          filterQuery = buildDateTimestampFilter(filter, filterQuery);
          break;
        case DOUBLE:
          filterQuery = buildNumericFilter(filter, filterQuery);
          break;
        case FLOAT:
          filterQuery = buildNumericFilter(filter,filterQuery);
          break;
        case LONG:
          filterQuery = buildNumericFilter(filter,filterQuery);
          break;
        case INTEGER:
          filterQuery = buildNumericFilter(filter, filterQuery);
          break;
        case STRING:
          filterQuery = buildStringFilter(filter, filterQuery);
          break;
      }
    }
    return filterQuery;
  }

  /**
   * Adding sql support for different date formats we support.
   *
   * @param filter Filter Object
   * @return String filter query
   */
  private StringBuilder buildDateTimestampFilter(Filter filter, StringBuilder filterQuery) {
    filterQuery.append(filter.getArtifactsName()).append(".").append(filter.getColumnName());

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
          filterQuery = dateFilterUtil(filter, filterQuery);
          break;
        case DATE_WITH_HOUR_MINUTES:
          filterQuery = dateFilterUtil(filter,filterQuery);
          break;
        case EPOCH_MILLIS:
          filterQuery.append("from_unixtime(").append(filter.getArtifactsName()).append(".")
              .append(filter.getColumnName()).append(")");
          filterQuery = epochDateFilterUtil(filter, true,filterQuery);
          break;
        case EPOCH_SECOND:
          filterQuery.append("from_unixtime(").append(filter.getArtifactsName()).append(".")
              .append(filter.getColumnName()).append(")");
          filterQuery = epochDateFilterUtil(filter, false, filterQuery);
          break;
        case ONLY_YEAR_FORMAT:
          filterQuery = onlyYearFilter(filter, filterQuery);
          break;
      }
    }
    return filterQuery;
  }

  /**
   * @param filter
   * @param isMilli
   * @return
   */
  private StringBuilder epochDateFilterUtil(Filter filter, boolean isMilli, StringBuilder filterQuery) {
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
      filterQuery = setGteLteForDate(gte, lte, filter, filterQuery);
    } else if (filter.getModel().getPresetCal() != null) {
      DynamicConvertor dynamicConvertor =
          BuilderUtil.getDynamicConvertForPresetCal(filter.getModel().getPresetCal());
      gte = dynamicConvertor.getGte();
      lte = dynamicConvertor.getLte();
      filterQuery = setGteLteForDate(gte, lte, filter, filterQuery);
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
        lteInEpoch = isMilli ? Long.parseLong(lte) / 1000 : Long.parseLong(lte);
      } else {
        gteInEpoch = isMilli ? value.longValue() / 1000 : value.longValue();
        lteInEpoch = isMilli ? otherValue.longValue() / 1000 : otherValue.longValue();
      }
      Date date = new Date(gteInEpoch);
      DateFormat dateFormat = new SimpleDateFormat(DATE_WITH_HOUR_MINUTES);
      gte = dateFormat.format(date);
      date = new Date(lteInEpoch);
      lte = dateFormat.format(date);
      filterQuery = setGteLteForDate(gte, lte, filter, filterQuery);
    }
    return filterQuery;
  }

  /**
   * This util method is used to apply data ranges for filter(For both Preset and custom dates).
   *
   * @param filter Filter object
   * @return Where clause for date
   */
  private StringBuilder dateFilterUtil(Filter filter, StringBuilder filterQuery) {
    Preset preset = filter.getModel().getPreset();
    Operator operator = filter.getModel().getOperator();
    String gte = filter.getModel().getGte();
    String lte = filter.getModel().getLte();
    if (preset != null && !preset.value().equals(Model.Preset.NA.toString())) {
      DynamicConvertor dynamicConvertor = BuilderUtil.dynamicDecipher(preset.value());
      gte = dynamicConvertor.getGte();
      lte = dynamicConvertor.getLte();
      filterQuery = setGteLteForDate(gte, lte, filter, filterQuery);
    } else if (filter.getModel().getPresetCal() != null) {
      DynamicConvertor dynamicConvertor =
          BuilderUtil.getDynamicConvertForPresetCal(filter.getModel().getPresetCal());
      gte = dynamicConvertor.getGte();
      lte = dynamicConvertor.getLte();
      filterQuery = setGteLteForDate(gte, lte, filter, filterQuery);
    } else if ((preset.value().equals(Model.Preset.NA.toString())
            || (operator.equals(Operator.BTW)))
        && gte != null
        && lte != null) {
      filterQuery = setGteLteForDate(filter.getModel().getGte(), filter.getModel().getLte(), filter,
          filterQuery);
    }
    return filterQuery;
  }

  private StringBuilder setGteLteForDate(String gte, String lte, Filter filter,
      StringBuilder filterQuery) {
    return filterQuery.append(" >= TO_DATE('").append(gte).append("') AND ")
        .append(filter.getArtifactsName()).append(".").append(filter.getColumnName())
        .append(" <= TO_DATE(date_add('").append(lte).append("',1))");
  }

  private StringBuilder buildNumericFilter(Filter filter,StringBuilder filterQuery) {
    filterQuery.append(filter.getArtifactsName()).append(".").append(filter.getColumnName());

    Operator op = filter.getModel().getOperator();

    switch (op) {
      case GTE:
        filterQuery.append(" >= ").append(filter.getModel().getValue());
        break;
      case LTE:
        filterQuery.append(" <= ").append( filter.getModel().getValue());
        break;
      case GT:
        filterQuery.append(" > ").append(filter.getModel().getValue());
        break;
      case LT:
        filterQuery.append(" < ").append(filter.getModel().getValue());
        break;
      case EQ:
        filterQuery.append(" = ").append(filter.getModel().getValue());
        break;
      case NEQ:
        filterQuery.append(" <> ").append(filter.getModel().getValue());
        break;
      case BTW:
        filterQuery.append(" >= ").append(filter.getModel().getOtherValue()).append(" AND ")
            .append(filter.getArtifactsName()).append(".").append(filter.getColumnName())
            .append(" <= ").append(filter.getModel().getValue());
        break;
    }

    return filterQuery;
  }

  private StringBuilder buildStringFilter(Filter filter, StringBuilder filterQuery) {
    filterQuery.append(UPPER).append("(" + filter.getArtifactsName()).append(".")
        .append(filter.getColumnName() + ")");

    Operator op = filter.getModel().getOperator();

    switch (op) {
      case SW:
        filterQuery.append(LIKE).append(UPPER)
            .append("('" + filter.getModel().getModelValues().get(0)).append("%')");
        break;
      case EW:
        filterQuery.append(LIKE).append(UPPER)
            .append("('%" + filter.getModel().getModelValues().get(0) + "')");
        break;
      case ISIN:
        List<Object> values = filter.getModel().getModelValues();
        filterQuery = buildInNotInClause(filterQuery, values, IN);
        break;
      case ISNOTIN:
        filterQuery = buildInNotInClause(filterQuery, filter.getModel().getModelValues(),
            "NOT IN");
        break;
      case CONTAINS:
        filterQuery.append(LIKE).append(UPPER)
            .append("('%" + filter.getModel().getModelValues().get(0) + "%')");
        break;
      case EQ:
        filterQuery.append(" = ").append(UPPER)
            .append("('" + filter.getModel().getModelValues().get(0) + "')");
        break;
      case NEQ:
        filterQuery.append(" <> ").append(UPPER)
            .append("('" + filter.getModel().getModelValues().get(0) + "')");
        break;
    }
    return filterQuery;
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
    String column = null;

    String aliasName = field.getAlias();
    column =
        "count(distinct "
            + artifactName
            + "."
            + field.getColumnName().replace(DOT_KEYWORD, "")
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

  private StringBuilder onlyYearFilter(Filter filter, StringBuilder filterQuery) {
    GregorianCalendar startDate;
    String gte = filter.getModel().getGte();
    String lte = filter.getModel().getLte();
    String gt = filter.getModel().getGt();
    String lt = filter.getModel().getLt();
    Double value = filter.getModel().getValue();

    if (gte != null) {
      int year = value == null ? Integer.parseInt(gte) : value.intValue();
      startDate = new GregorianCalendar(year, 0, 1, 0, 0, 0);
      filterQuery.append(" >= TO_DATE('").append( startDate).append( "')");
    } else if (gt != null) {
      int year = value == null ? Integer.parseInt(gt) : value.intValue();
      startDate = new GregorianCalendar(year, 0, 1, 0, 0, 0);
      filterQuery.append(" > TO_DATE('").append( startDate).append("')");
    } else if (lte != null) {
      int year = value == null ? Integer.parseInt(lte) : value.intValue();
      startDate = new GregorianCalendar(year, 0, 1, 0, 0, 0);
      filterQuery.append(" <= TO_DATE('").append(startDate).append("')");
    } else if (lt != null) {
      int year = value == null ? Integer.parseInt(lt) : value.intValue();
      startDate = new GregorianCalendar(year, 0, 1, 0, 0, 0);
      filterQuery.append(" < TO_DATE('").append(startDate).append("')");
    }
    return filterQuery;
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
    if (!CollectionUtils.isEmpty(groupByColumns)) {
      groupBy = " GROUP BY " + String.join(", ", groupByColumns);
    }

    return groupBy;
  }

  public String dskForManualQuery(
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
          logger.info("dskformed query = {}", dskFormedQuery);
          if (dskFormedQuery != null && !StringUtils.isEmpty(dskFormedQuery)) {
            dskFilter = dskFilter.append(dskFormedQuery).append(" ) as " + artifactName + " ");
            query = query + " ";
            String artName = "FROM " + artifactName;
            logger.trace("dskFilter str = {}", dskFilter);
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
            logger.info("Logged query : {}", query);
          }
        }
      }
    }

    logger.info("DSK applied Query : {}", query);
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

  /**
   * Recursive function call to build Filters.
   *
   * @param sipFilter
   * @return
   */
  public StringBuilder buildSipFilters(Filter sipFilter) {
    boolean flag = true;
    String booleanCriteria = null;
    StringBuilder filterQuery = new StringBuilder();
    if (sipFilter == null) {
      return filterQuery;
    }

    if (sipFilter.getBooleanCriteria() == null && sipFilter.getFilters() == null) {
      logger.error("Invalid dsk object");
      return filterQuery;
    }

    if (sipFilter.getBooleanCriteria() != null) {
      booleanCriteria = " " + sipFilter.getBooleanCriteria() + " ";
    }

      for (Filter filterAttribute : sipFilter.getFilters()) {
        if (filterAttribute.getFilters() != null) {
          StringBuilder childQuery = buildSipFilters(filterAttribute);
          if (childQuery != null && !StringUtils.isEmpty(childQuery.toString())) {
            if (filterQuery != null && filterQuery.length() > 0) {
              filterQuery.append(booleanCriteria);
            }
            filterQuery.append(childQuery);
            flag = false;
          }
        } else {
          if (!flag) {
            filterQuery.append(booleanCriteria);
          }
          if (filterAttribute.getModel() != null) {
            filterQuery = buildFilterUtil(filterAttribute, filterQuery);
            flag = false;
          }
        }
      }

    if (filterQuery.length() != 0) {
      filterQuery.insert(0, "(").append(")");
    }
    return filterQuery;
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
    StringBuilder filterQuery;
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

    Filter fil = buildNestedFilter(sipQuery.getFilters(),booleanCriteria);
    filterQuery = buildSipFilters(fil);

    String filter = filterQuery.toString();
    if (!StringUtils.isEmpty(filter)) {
      select
          .append(SPACE)
          .append(WHERE)
          .append(SPACE)
          .append("(")
          .append(filter)
          .append(")")
          .append(SPACE);
    }
    select.append(queryDskBuilder(sipDskAttribute, sipQuery, filter)).append(buildGroupBy());

    String sort = buildSort(sipQuery.getSorts());
    if (sort != null && !StringUtils.isEmpty(sort)) {
      select.append(SPACE).append(ORDER_BY).append(SPACE).append(sort);
    }
    return select.toString();
  }

  /**
   * @param sipDskAttribute
   * @param sipQuery
   * @param filter
   * @return
   */
  public static String queryDskBuilder(SipDskAttribute sipDskAttribute, SipQuery sipQuery,
      String filter) {
    StringBuilder dskFilter = new StringBuilder();
    if (sipDskAttribute != null
        && (sipDskAttribute.getBooleanCriteria() != null
            || sipDskAttribute.getBooleanQuery() != null)) {

      String condition = null;
      if (sipQuery.getFilters() != null && sipQuery.getFilters().size() > 0 && !StringUtils
          .isEmpty(filter)) {
        condition = AND;
      } else {
        condition = WHERE;
      }
      List<Artifact> artifacts = sipQuery.getArtifacts();
      for (Artifact artifact : artifacts) {
        String artifactName = artifact.getArtifactsName();
        String dskFormedQuery = dskQueryForArtifact(sipDskAttribute, artifactName);
        if (dskFormedQuery != null && !StringUtils.isEmpty(dskFormedQuery)) {
          dskFilter.append(SPACE).append(condition).append(SPACE);
          dskFilter.append(SPACE).append(dskFormedQuery);
          condition = AND;
        }
      }
    }
    return dskFilter.toString();
  }


  /**
   *
   * @param filterQuery
   * @param values
   * @param operator
   * @return
   */
  public StringBuilder buildInNotInClause(StringBuilder filterQuery, List<?> values,
      String operator) {
    filterQuery.append(" ").append(operator).append(" (");
    int initFlag = 0;
    for (Object value : values) {
      filterQuery = initFlag != 0 ? filterQuery.append(", ") : filterQuery;
      filterQuery = filterQuery.append(UPPER).append("('" + value + "')");
      initFlag++;
    }
    filterQuery.append(" )");
    return filterQuery;
  }
}
