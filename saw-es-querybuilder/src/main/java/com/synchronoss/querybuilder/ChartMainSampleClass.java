package com.synchronoss.querybuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;
import com.synchronoss.SAWElasticTransportService;
import com.synchronoss.querybuilder.model.chart.Filter.Type;
import com.synchronoss.querybuilder.model.pivot.Model.Operator;
import com.synchronoss.querybuilder.model.pivot.SqlBuilder.BooleanCriteria;

public class ChartMainSampleClass {

  public static void main(String[] args) throws JsonProcessingException, IOException,
      ProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    System.setProperty("host", "10.48.72.74");
    System.setProperty("port", "9300");
    System.setProperty("cluster", "sncr-salesdemo");
    System.setProperty("schema.chart", "D:\\Work\\SAW2_0\\saw-services\\saw-es-querybuilder\\src\\main\\resources\\schema\\chart_querybuilder_schema.json");

    // This is the entry point for /analysis service as JSONString not as file
    JsonNode objectNode =
        objectMapper.readTree(new File(
            "C:\\Users\\saurav.paul\\Desktop\\Sergey\\chart_type_data.json"));
    String chart = System.getProperty("schema.chart");
    // JsonNode objectNode = objectMapper.readTree(new File(args[0]));

    String json = "{ \"sqlBuilder\" :" + objectNode.get("sqlBuilder").toString() + "}";
    JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
    JsonValidator validator = factory.getValidator();
    final JsonNode data = JsonLoader.fromString(json);
    final JsonNode schema =
        JsonLoader.fromFile(new File(chart));
    ProcessingReport report = validator.validate(schema, data);
    if (report.isSuccess() == false) {
      throw new ProcessingException(report.toString());
    }
    JsonNode objectNode1 = objectMapper.readTree(json);
    com.synchronoss.querybuilder.model.chart.SqlBuilderChart sqlBuilderNodeChart =
        objectMapper.treeToValue(objectNode1,
            com.synchronoss.querybuilder.model.chart.SqlBuilderChart.class);
    com.synchronoss.querybuilder.model.chart.SqlBuilder sqlBuilderNode =
        sqlBuilderNodeChart.getSqlBuilder();

    int size = 0;
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.size(size);
    if (sqlBuilderNode.getSorts() == null || sqlBuilderNode.getFilters() == null) {
      throw new NullPointerException(
          "Please add sort[] & filter[] block.It can be empty but these blocks are important.");
    }

    // The below block adding the sort block
    List<com.synchronoss.querybuilder.model.chart.Sort> sortNode = sqlBuilderNode.getSorts();
    for (com.synchronoss.querybuilder.model.chart.Sort item : sortNode) {
      SortOrder sortOrder =
          item.getOrder().name().equals(SortOrder.ASC.name()) ? SortOrder.ASC : SortOrder.DESC;
      FieldSortBuilder sortBuilder = SortBuilders.fieldSort(item.getColumnName()).order(sortOrder);
      searchSourceBuilder.sort(sortBuilder);
    }

    // The below block adding filter block
    final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();;
    if (sqlBuilderNode.getBooleanCriteria() != null) {
      List<com.synchronoss.querybuilder.model.chart.Filter> filters = sqlBuilderNode.getFilters();
      List<QueryBuilder> builder = new ArrayList<QueryBuilder>();
      for (com.synchronoss.querybuilder.model.chart.Filter item : filters) {
        if (!item.getIsRuntimeFilter().value()) {
          if (item.getType().value().equals(Type.DATE.value())
              || item.getType().value().equals(Type.TIMESTAMP.value())) {
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
            rangeQueryBuilder.lte(item.getModel().getLte());
            rangeQueryBuilder.gte(item.getModel().getGte());
            builder.add(rangeQueryBuilder);
          }
          if (item.getType().value().equals(Type.STRING.value())) {
            TermsQueryBuilder termsQueryBuilder =
                new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
            builder.add(termsQueryBuilder);
          }
          if ((item.getType().value().toLowerCase().equals(Type.DOUBLE.value().toLowerCase()) || item
              .getType().value().toLowerCase().equals(Type.INT.value().toLowerCase()))
              || item.getType().value().toLowerCase().equals(Type.FLOAT.value().toLowerCase())
              || item.getType().value().toLowerCase().equals(Type.LONG.value().toLowerCase())) {
            
            if (item.getModel().getOperator().value().equals(Operator.BTW.value())) {
              RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
              rangeQueryBuilder.lte(item.getModel().getOtherValue());
              rangeQueryBuilder.gte(item.getModel().getValue());
              builder.add(rangeQueryBuilder);
            }
            if (item.getModel().getOperator().value().equals(Operator.GT.value())) {
              RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
              rangeQueryBuilder.gt(item.getModel().getValue());
              builder.add(rangeQueryBuilder);
            }
            if (item.getModel().getOperator().value().equals(Operator.GTE.value())) {
              RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
              rangeQueryBuilder.gte(item.getModel().getValue());
              builder.add(rangeQueryBuilder);
            }
            if (item.getModel().getOperator().value().equals(Operator.LT.value())) {
              RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
              rangeQueryBuilder.lt(item.getModel().getValue());
              builder.add(rangeQueryBuilder);
            }
            if (item.getModel().getOperator().value().equals(Operator.LTE.value())) {
              RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
              rangeQueryBuilder.lte(item.getModel().getValue());
              builder.add(rangeQueryBuilder);
            }
            if (item.getModel().getOperator().value().equals(Operator.EQ.value())) {
              TermQueryBuilder termQueryBuilder =
                  new TermQueryBuilder(item.getColumnName(), item.getModel().getValue());
              builder.add(termQueryBuilder);
            }
            if (item.getModel().getOperator().value().equals(Operator.NEQ.value())) {
              BoolQueryBuilder boolQueryBuilderIn = new BoolQueryBuilder();
              boolQueryBuilderIn.mustNot(new TermQueryBuilder(item.getColumnName(), item.getModel()
                  .getValue()));
              builder.add(boolQueryBuilderIn);
            }
          }
        }
        if (item.getIsRuntimeFilter().value() && item.getModel() != null) {
          if (item.getType().value().equals(Type.DATE.value())
              || item.getType().value().equals(Type.TIMESTAMP.value())) {
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
            rangeQueryBuilder.lte(item.getModel().getLte());
            rangeQueryBuilder.gte(item.getModel().getGte());
            builder.add(rangeQueryBuilder);
          }
          if (item.getType().value().equals(Type.STRING.value())) {
            TermsQueryBuilder termsQueryBuilder =
                new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
            builder.add(termsQueryBuilder);
          }
          if ((item.getType().value().toLowerCase().equals(Type.DOUBLE.value().toLowerCase()) || item
              .getType().value().toLowerCase().equals(Type.INT.value().toLowerCase()))
              || item.getType().value().toLowerCase().equals(Type.FLOAT.value().toLowerCase())
              || item.getType().value().toLowerCase().equals(Type.LONG.value().toLowerCase())) {
            if (item.getModel().getOperator().value().equals(Operator.BTW.value())) {
              RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
              rangeQueryBuilder.lte(item.getModel().getOtherValue());
              rangeQueryBuilder.gte(item.getModel().getValue());
              builder.add(rangeQueryBuilder);
            }
            if (item.getModel().getOperator().value().equals(Operator.EQ.value())) {
              TermQueryBuilder termQueryBuilder =
                  new TermQueryBuilder(item.getColumnName(), item.getModel().getValue());
              builder.add(termQueryBuilder);
            }
            if (item.getModel().getOperator().value().equals(Operator.NEQ.value())) {
              BoolQueryBuilder boolQueryBuilderIn = new BoolQueryBuilder();
              boolQueryBuilderIn.mustNot(new TermQueryBuilder(item.getColumnName(), item.getModel()
                  .getValue()));
              builder.add(boolQueryBuilderIn);
            }
          }
        }
      }
      if (sqlBuilderNode.getBooleanCriteria().value().equals(BooleanCriteria.AND.value())) {
        builder.forEach(item -> {
          boolQueryBuilder.must(item);
        });
      } else {
        builder.forEach(item -> {
          boolQueryBuilder.should(item);
        });
      }
      searchSourceBuilder.query(boolQueryBuilder);
    }
    List<com.synchronoss.querybuilder.model.chart.NodeField>  nodeFields = sqlBuilderNode.getNodeFields();
    List<com.synchronoss.querybuilder.model.chart.DataField> dataFields =  sqlBuilderNode.getDataFields();
    if (nodeFields != null && dataFields !=null)
    {
      if (!nodeFields.isEmpty() && nodeFields.size() <=3){
      searchSourceBuilder = AxesFieldDataFieldsAvailable.rowDataFieldsAvailable
          (nodeFields, dataFields, searchSourceBuilder, boolQueryBuilder);
      }
      else {
        
          throw new IllegalArgumentException("Charts cannot have more than three axes");
      }
    } 
    else {
        throw new IllegalArgumentException("Please select appropriate value for the axes & metrices");
    }
    String query = searchSourceBuilder.toString();
    System.out.println(query);
    System.setProperty("url", "http://mapr-dev02.sncrbda.dev.vacum-np.sncrcorp.net:9200/");
    String response =
        SAWElasticTransportService.executeReturnAsString(query, objectNode.toString(), "some",
            "xssds", "login");
    System.out.println(response);
  }
}
