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
import com.synchronoss.BuilderUtil;
import com.synchronoss.DynamicConvertor;
import com.synchronoss.SAWElasticTransportService;
import com.synchronoss.querybuilder.model.chart.Filter.Type;
import com.synchronoss.querybuilder.model.chart.Model;
import com.synchronoss.querybuilder.model.pivot.Model.Operator;
import com.synchronoss.querybuilder.model.pivot.SqlBuilder.BooleanCriteria;

public class PivotMainSampleClass {

  public static void main(String[] args) throws JsonProcessingException, IOException, ProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    System.setProperty("host", "10.48.72.74");
    System.setProperty("port", "9300");
    System.setProperty("cluster", "sncr-salesdemo");
    System.setProperty("schema.pivot", "/Users/Shared/WORK/saw-services/saw-es-querybuilder/src/main/resources/schema/pivot_querybuilder_schema.json");

    // This is the entry point for /analysis service as JSONString not as file
    JsonNode objectNode =
        objectMapper.readTree(new File(
            "/Users/spau0004/Desktop/Sergey/pivot_type_data_1.json"));
    String pivot = System.getProperty("schema.pivot");
    // JsonNode objectNode = objectMapper.readTree(new File(args[0]));
    String json = "{ \"sqlBuilder\" :" + objectNode.get("sqlBuilder").toString() + "}";
    JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
    JsonValidator validator = factory.getValidator();
    final JsonNode data = JsonLoader.fromString(json);
    final JsonNode schema =
        JsonLoader.fromFile(new File(pivot));
    ProcessingReport report = validator.validate(schema, data);
    if (report.isSuccess() == false) {
      throw new ProcessingException(report.toString());
    }
    JsonNode objectNode1 = objectMapper.readTree(json);
    com.synchronoss.querybuilder.model.pivot.SqlBuilderPivot sqlBuilderNodeChart =
        objectMapper.treeToValue(objectNode1,
            com.synchronoss.querybuilder.model.pivot.SqlBuilderPivot.class);
    com.synchronoss.querybuilder.model.pivot.SqlBuilder sqlBuilderNode =
        sqlBuilderNodeChart.getSqlBuilder();

    int size = 0;
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.size(size);

    // The below block adding the sort block
    List<com.synchronoss.querybuilder.model.pivot.Sort> sortNode = sqlBuilderNode.getSorts();
    for (com.synchronoss.querybuilder.model.pivot.Sort item : sortNode) {
      SortOrder sortOrder =
          item.getOrder().name().equals(SortOrder.ASC.name()) ? SortOrder.ASC : SortOrder.DESC;
      FieldSortBuilder sortBuilder = SortBuilders.fieldSort(item.getColumnName()).order(sortOrder);
      searchSourceBuilder.sort(sortBuilder);
    }

    // The below block adding filter block
    final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();;
    if (sqlBuilderNode.getBooleanCriteria() !=null ){
    List<com.synchronoss.querybuilder.model.pivot.Filter> filters = sqlBuilderNode.getFilters();
    List<QueryBuilder> builder = new ArrayList<QueryBuilder>();
    for (com.synchronoss.querybuilder.model.pivot.Filter item : filters) 
    {
      if (!item.getIsRuntimeFilter().value()){
        if (item.getType().value().equals(Type.DATE.value()) || item.getType().value().equals(Type.TIMESTAMP.value())) {
          if (item.getModel().getPreset()!=null  && !item.getModel().getPreset().value().equals(Model.Preset.NA.toString()))
          {
            DynamicConvertor dynamicConvertor = BuilderUtil.dynamicDecipher(item.getModel().getPreset().value());
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
            rangeQueryBuilder.lte(dynamicConvertor.getLte());
            rangeQueryBuilder.gte(dynamicConvertor.getGte());
            builder.add(rangeQueryBuilder);
          }
          else {
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
            rangeQueryBuilder.lte(item.getModel().getLte());
            rangeQueryBuilder.gte(item.getModel().getGte());
            builder.add(rangeQueryBuilder);
          }
        }
        if (item.getType().value().equals(Type.STRING.value())) {
          TermsQueryBuilder termsQueryBuilder =
              new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
          builder.add(termsQueryBuilder);
        }
        if ((item.getType().value().toLowerCase().equals(Type.DOUBLE.value().toLowerCase()) || item.getType().value().toLowerCase().equals(Type.INT.value().toLowerCase()))
            || item.getType().value().toLowerCase().equals(Type.FLOAT.value().toLowerCase()) || item.getType().value().toLowerCase().equals(Type.LONG.value().toLowerCase())) 
        {
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
      if (item.getIsRuntimeFilter().value() && item.getModel()!=null)
      {
        if (item.getType().value().equals(Type.DATE.value()) || item.getType().value().equals(Type.TIMESTAMP.value())) {
          if (item.getModel().getPreset()!=null && !item.getModel().getPreset().value().equals(Model.Preset.NA.toString()))
          {
            DynamicConvertor dynamicConvertor = BuilderUtil.dynamicDecipher(item.getModel().getPreset().value());
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
            rangeQueryBuilder.lte(dynamicConvertor.getLte());
            rangeQueryBuilder.gte(dynamicConvertor.getGte());
            builder.add(rangeQueryBuilder);
          }
          else {
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
            rangeQueryBuilder.lte(item.getModel().getLte());
            rangeQueryBuilder.gte(item.getModel().getGte());
            builder.add(rangeQueryBuilder);
          }
        }
        if (item.getType().value().equals(Type.STRING.value())) {
          TermsQueryBuilder termsQueryBuilder =
              new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
          builder.add(termsQueryBuilder);
        }
        if ((item.getType().value().toLowerCase().equals(Type.DOUBLE.value().toLowerCase()) || item.getType().value().toLowerCase().equals(Type.INT.value().toLowerCase()))
            || item.getType().value().toLowerCase().equals(Type.FLOAT.value().toLowerCase()) || item.getType().value().toLowerCase().equals(Type.LONG.value().toLowerCase())) 
        {
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
    // The below block adding only if row level & column level aggregation is available
    List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield =
        sqlBuilderNode.getRowFields();
    List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields =
        sqlBuilderNode.getColumnFields();
    List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields =
        sqlBuilderNode.getDataFields();

    // Use case I: The below block is only when column & Data Field is not empty & row field is
    // empty
    if ((rowfield.isEmpty() && rowfield.size() == 0)) {
      if ((columnFields != null && columnFields.size() <= 5)
          && (dataFields != null && dataFields.size() <= 5)) {
        searchSourceBuilder =
            ColumnDataFieldsAvailable.columnDataFieldsAvailable(rowfield, columnFields, dataFields,
                searchSourceBuilder, boolQueryBuilder);
      } else {
        throw new IllegalArgumentException(
            "Pivot in column/data fields wise are allowed until five levels. Please verify & recreate your request.");
      }
    }

    // Use case II: The below block is only when column & row Field
    if (((dataFields.isEmpty()) && dataFields.size() == 0)) {
      if ((rowfield != null && rowfield.size() <= 5)
          && (columnFields != null && columnFields.size() <= 5)) {
        searchSourceBuilder =
            RowColumnFieldsAvailable.rowColumnFieldsAvailable(rowfield, columnFields, dataFields,
                searchSourceBuilder, boolQueryBuilder);
      } else {
        throw new IllegalArgumentException(
            "Pivot in row/column wise are allowed until five levels. Please verify & recreate your request.");
      }
    }

    // Use case III: The below block is only when row field with column field & data field
    if ((rowfield != null && rowfield.size() <= 5)
        && ((columnFields != null && columnFields.size() <= 5) && ((dataFields != null && dataFields
            .size() <= 5)))) {
      searchSourceBuilder =
          AllFieldsAvailablePivot.allFieldsAvailable(rowfield, columnFields, dataFields,
              searchSourceBuilder, boolQueryBuilder);
    } // end of rowField, columnField & dataField
    else {
      throw new IllegalArgumentException(
          "Pivot in row/column/datafield wise are allowed until five levels. Please verify & recreate your request.");
    }

    // Use case IV: The below block is only when row field is not empty but column field & data
    // field are empty
    if ((columnFields.isEmpty() && columnFields.size() == 0)
        && (dataFields.isEmpty() && dataFields.size() == 0)) {
      if ((!rowfield.isEmpty()) && rowfield.size() <= 5) {
        searchSourceBuilder =
            RowFieldsAvailable.rowFieldsAvailable(rowfield, columnFields, dataFields,
                searchSourceBuilder, boolQueryBuilder);
      } else {
        throw new IllegalArgumentException(
            "Pivot in row wise are allowed until five levels. Please verify & recreate your request.");
      }
    }


    // Use case V: The below block is only when row field is not empty but column field & data field
    // are empty
    if ((rowfield.isEmpty() && rowfield.size() == 0)
        && (dataFields.isEmpty() && dataFields.size() == 0)) {

      if ((!columnFields.isEmpty()) && columnFields.size() <= 5) {
        searchSourceBuilder =
            ColumnFieldsAvailable.columnFieldsAvailable(rowfield, columnFields, dataFields,
                searchSourceBuilder, boolQueryBuilder);
      } else {
        throw new IllegalArgumentException(
            "Pivot in column wise are allowed until five levels. Please verify & recreate your request.");
      }
    }

    // Use case VI: The below block is only when column & Data Field is not empty & row field is
    // empty
    if ((columnFields.isEmpty() && columnFields.size() == 0)) {
      if ((rowfield != null && rowfield.size() <= 5)
          && (dataFields != null && dataFields.size() <= 5)) {
        searchSourceBuilder =
            RowDataFieldsAvailable.rowDataFieldsAvailable(rowfield, columnFields, dataFields,
                searchSourceBuilder, boolQueryBuilder);
      } else {
        throw new IllegalArgumentException(
            "Pivot in column/data fields wise are allowed until five levels. Please verify & recreate your request.");
      }
    }
    
    // Use case VII: The below block is only when data Field is not empty
    // empty
    if ((columnFields.isEmpty() && columnFields.size() == 0) && (rowfield.isEmpty() && rowfield.size() == 0)) 
    {
      if  (dataFields != null && dataFields.size() <= 5)
      {
       searchSourceBuilder = OnlyDataFieldsAvailable.dataFieldsAvailable(dataFields, searchSourceBuilder, boolQueryBuilder);
      } else {
        throw new IllegalArgumentException(
            "In Pivot data fields wise are allowed until five levels. Please verify & recreate your request.");
      }
    }
    
    System.setProperty("url", "http://mapr-dev02.sncrbda.dev.vacum-np.sncrcorp.net:9200/");
    System.out.println(searchSourceBuilder.toString());
    String response =
        SAWElasticTransportService.executeReturnAsString(searchSourceBuilder.toString(),
            objectNode.toString(), "some", "xssds", "login",3);
    System.out.println(response);

  }

}
