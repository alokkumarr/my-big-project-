'use strict';

const Constants = require('../../Constants');

class RequestModel {
  getEsReportBody(
    customerCode,
    id,
    action,
    dataSetName,
    semanticId,
    userId,
    loginId,
    analysisName,
    analysisDesc,
    subCategoryId,
    currentTimeStamp,
    analysisType,
    subType,
    filters = null
  ) {
    let body;

    let dslUpdateBpdy = {
      type: analysisType,
      semanticId: semanticId,
      metricName: dataSetName,
      name: analysisName,
      description: analysisDesc,
      id: id,
      parentAnalysisId: null,
      category: subCategoryId,
      customerCode: customerCode,
      projectCode: 'workbench',
      module: 'ANALYZE',
      createdTime: currentTimeStamp,
      createdBy: loginId,
      userId: userId,
      modifiedTime: currentTimeStamp,
      modifiedBy: loginId,
      sipQuery: {
        artifacts: [
          {
            artifactsName: 'sample',
            fields: [
              {
                columnName: 'date',
                dataField: 'date',
                displayName: 'Date',
                groupInterval: null,
                name: 'date',
                type: 'date',
                table: 'sample',
                dateFormat: 'yyyy-MM-dd',
                visibleIndex: 0
              },
              {
                columnName: 'double',
                dataField: 'double',
                displayName: 'Double',
                groupInterval: null,
                name: 'double',
                type: 'double',
                table: 'sample',
                format: {
                  precision: 2
                },
                visibleIndex: 1
              },
              {
                columnName: 'float',
                dataField: 'float',
                displayName: 'Float',
                groupInterval: null,
                name: 'float',
                type: 'float',
                table: 'sample',
                format: {
                  precision: 2
                },
                visibleIndex: 2
              },
              {
                columnName: 'integer',
                dataField: 'integer',
                displayName: 'Integer',
                groupInterval: null,
                name: 'integer',
                type: 'integer',
                table: 'sample',
                format: {},
                visibleIndex: 3
              },
              {
                columnName: 'long',
                dataField: 'long',
                displayName: 'Long',
                groupInterval: null,
                name: 'long',
                type: 'long',
                table: 'sample',
                format: {},
                visibleIndex: 4
              },
              {
                columnName: 'string.keyword',
                dataField: 'string',
                displayName: 'String',
                groupInterval: null,
                name: 'string',
                type: 'string',
                table: 'sample',
                visibleIndex: 5
              }
            ]
          }
        ],
        booleanCriteria: 'AND',
        filters: filters ? filters : [],
        sorts: [],
        joins: [],
        store: {
          dataStore: 'sampleAlias/sample',
          storageType: 'ES'
        }
      },
      designerEdit: null,
      scheduled: null,
      supports: [
        {
          category: 'map',
          children: [
            {
              icon: 'icon-geo-chart',
              label: 'Choropleth map',
              type: 'map:chart'
            },
            {
              icon: 'icon-geo-chart',
              label: 'Geo Map',
              type: 'map:map'
            }
          ],
          label: 'Geo Location'
        }
      ],
      categoryId: subCategoryId,
      saved: true
    };
    if (action === 'update') {
      body = dslUpdateBpdy;
    } else {
      throw new Error('Invalid action: ' + action);
    }
    return body;
  }

  getPivotBody(
    customerCode,
    id,
    action,
    dataSetName,
    semanticId,
    userId,
    loginId,
    analysisName,
    analysisDesc,
    subCategoryId,
    currentTimeStamp,
    analysisType,
    subType,
    filters = null
  ) {
    const save = {
      metricName: null,
      semanticId: semanticId,
      type: Constants.PIVOT,
      name: analysisName,
      description: analysisDesc,
      scheduled: null,
      supports: [],
      id,
      parentAnalysisId: null,
      category: subCategoryId,
      customerCode: customerCode,
      projectCode: 'workbench',
      module: 'ANALYZE',
      createdTime: currentTimeStamp,
      createdBy: loginId,
      modifiedTime: currentTimeStamp,
      modifiedBy: loginId,
      sipQuery: {
        artifacts: [
          {
            artifactsName: 'sample',
            fields: [
              {
                aggregate: 'sum',
                area: 'data',
                columnName: 'float',
                dataField: 'float',
                displayName: 'Float',
                name: 'float',
                type: 'float',
                table: 'sample',
                areaIndex: 0
              },
              {
                area: 'row',
                columnName: 'string.keyword',
                dataField: 'string',
                displayName: 'String',
                groupInterval: 'day',
                name: 'string',
                type: 'string',
                table: 'sample',
                areaIndex: 0
              }
            ]
          }
        ],
        booleanCriteria: 'AND',
        filters: filters ? filters : [],
        sorts: [],
        store: {
          dataStore: 'sampleAlias/sample',
          storageType: 'ES'
        }
      },
      designerEdit: null,
      chartOptions: {
        isInverted: false,
        legend: {
          align: 'right',
          layout: 'vertical'
        },
        chartTitle: 'Untitled Analysis',
        chartType: 'column',
        xAxis: {
          title: null
        },
        yAxis: {
          title: null
        }
      },
      artifacts: [
        {
          artifactName: 'sample',
          columns: [
            {
              aliasName: '',
              columnName: 'string.keyword',
              displayName: 'String',
              filterEligible: true,
              joinEligible: false,
              name: 'string',
              table: 'sample',
              type: 'string',
              area: 'row',
              checked: true,
              dateInterval: 'day',
              areaIndex: 0
            },
            {
              aliasName: '',
              columnName: 'long',
              displayName: 'Long',
              filterEligible: true,
              joinEligible: false,
              kpiEligible: true,
              name: 'long',
              table: 'sample',
              type: 'long'
            },
            {
              aliasName: '',
              columnName: 'float',
              displayName: 'Float',
              filterEligible: true,
              joinEligible: false,
              kpiEligible: true,
              name: 'float',
              table: 'sample',
              type: 'float',
              area: 'data',
              checked: true,
              aggregate: 'sum',
              areaIndex: 0
            },
            {
              aliasName: '',
              columnName: 'date',
              displayName: 'Date',
              filterEligible: true,
              joinEligible: false,
              kpiEligible: true,
              name: 'date',
              table: 'sample',
              type: 'date'
            },
            {
              aliasName: '',
              columnName: 'integer',
              displayName: 'Integer',
              filterEligible: true,
              joinEligible: false,
              kpiEligible: true,
              name: 'integer',
              table: 'sample',
              type: 'integer'
            },
            {
              aliasName: '',
              columnName: 'double',
              displayName: 'Double',
              filterEligible: true,
              joinEligible: false,
              kpiEligible: true,
              name: 'double',
              table: 'sample',
              type: 'double'
            }
          ]
        }
      ],
      sqlBuilder: {
        booleanCriteria: 'AND',
        filters: filters ? filters : [],
        sorts: [],
        rowFields: [
          {
            type: 'string',
            columnName: 'string.keyword',
            area: 'row',
            areaIndex: 0,
            aggregate: null,
            name: null,
            dateInterval: null
          }
        ],
        columnFields: [],
        dataFields: [
          {
            type: 'float',
            columnName: 'float',
            area: 'data',
            areaIndex: 0,
            aggregate: 'sum',
            name: 'float',
            dateInterval: null
          }
        ]
      },
      categoryId: subCategoryId,
      saved: true
    };

    if (action === 'update') {
      return save;
    } else {
      throw new Error('Invalid action: ' + action);
    }
    return null;
  }

  getReportBody(
    customerCode,
    id,
    action,
    dataSetName,
    semanticId,
    userId,
    loginId,
    analysisName,
    analysisDesc,
    subCategoryId,
    currentTimeStamp,
    analysisType,
    subType,
    filters = null
  ) {
    let body;

    let update = {
      contents: {
        keys: [
          {
            customerCode: customerCode,
            module: 'ANALYZE',
            id: id,
            type: analysisType
          }
        ],
        action: action,
        analyze: [
          {
            type: analysisType,
            semanticId: semanticId,
            metricName: dataSetName,
            name: analysisName,
            description: analysisDesc,
            scheduled: null,
            statusMessage: 'Entity has retrieved successfully',
            id: id,
            createdBy: 'sipadmin@synchronoss.com',
            customerCode: customerCode,
            projectCode: 'workbench',
            saved: true,
            username: 'sipadmin@synchronoss.com',
            module: 'ANALYZE',
            artifacts: [
              {
                artifactName: 'SALES',
                columns: [
                  {
                    aliasName: '',
                    columnName: 'string',
                    displayName: 'String',
                    filterEligible: true,
                    joinEligible: true,
                    name: 'string',
                    table: 'sales',
                    type: 'string',
                    visibleIndex: 0
                  },
                  {
                    aliasName: '',
                    columnName: 'long',
                    displayName: 'Long',
                    filterEligible: true,
                    joinEligible: false,
                    name: 'long',
                    table: 'sample',
                    type: 'long',
                    visibleIndex: 1
                  },
                  {
                    aliasName: '',
                    columnName: 'float',
                    displayName: 'Float',
                    filterEligible: true,
                    joinEligible: false,
                    name: 'float',
                    table: 'sales',
                    type: 'float',
                    format: {
                      precision: 2
                    },
                    visibleIndex: 3
                  },
                  {
                    aliasName: '',
                    columnName: 'date',
                    displayName: 'Date',
                    filterEligible: true,
                    joinEligible: false,
                    name: 'date',
                    table: 'sales',
                    type: 'date',
                    format: 'yyyy-MM-dd',
                    visibleIndex: 5
                  },
                  {
                    aliasName: '',
                    columnName: 'integer',
                    displayName: 'Integer',
                    filterEligible: true,
                    joinEligible: false,
                    name: 'integer',
                    table: 'sample',
                    type: 'integer',
                    visibleIndex: 2
                  },
                  {
                    aliasName: '',
                    columnName: 'double',
                    displayName: 'Double',
                    filterEligible: true,
                    joinEligible: false,
                    name: 'double',
                    table: 'sales',
                    type: 'double',
                    format: {
                      precision: 2
                    },
                    visibleIndex: 4
                  }
                ],
                artifactPosition: [19, 8]
              },
              {
                artifactName: 'PRODUCT',
                columns: [
                  {
                    aliasName: '',
                    columnName: 'string_2',
                    displayName: 'String_2',
                    filterEligible: true,
                    joinEligible: true,
                    name: 'string_2',
                    table: 'product',
                    type: 'string'
                  },
                  {
                    aliasName: '',
                    columnName: 'long_2',
                    displayName: 'Long_2',
                    filterEligible: true,
                    joinEligible: false,
                    name: 'long_2',
                    table: 'sample',
                    type: 'long'
                  },
                  {
                    aliasName: '',
                    columnName: 'float_2',
                    displayName: 'Float_2',
                    filterEligible: true,
                    joinEligible: false,
                    name: 'float_2',
                    table: 'product',
                    type: 'float'
                  },
                  {
                    aliasName: '',
                    columnName: 'date_2',
                    displayName: 'Date_2',
                    filterEligible: true,
                    joinEligible: false,
                    name: 'date_2',
                    table: 'product',
                    type: 'date'
                  },
                  {
                    aliasName: '',
                    columnName: 'integer_2',
                    displayName: 'Integer_2',
                    filterEligible: true,
                    joinEligible: false,
                    name: 'integer_2',
                    table: 'sample',
                    type: 'integer'
                  },
                  {
                    aliasName: '',
                    columnName: 'double_2',
                    displayName: 'Double_2',
                    filterEligible: true,
                    joinEligible: false,
                    name: 'double_2',
                    table: 'sales',
                    type: 'double'
                  }
                ],
                artifactPosition: [420, 0]
              }
            ],
            repository: [
              {
                format: 'ndjson',
                name: 'SALES',
                physicalLocation:
                  '/var/sip/services/saw-analyze-samples/sample-spark/data-sales.ndjson'
              },
              {
                format: 'ndjson',
                name: 'PRODUCT',
                physicalLocation:
                  '/var/sip/services/saw-analyze-samples/sample-spark/data-product.ndjson'
              }
            ],
            parentDataSetIds: [
              'SALES::json::1539937726998',
              'PRODUCT::json::1539937737836'
            ],
            modifiedTime: currentTimeStamp,
            createdTime: currentTimeStamp,
            parentDataSetNames: ['SALES', 'PRODUCT'],
            createdTimestamp: currentTimeStamp,
            userId: userId,
            userFullName: loginId,
            sqlBuilder: {
              booleanCriteria: 'AND',
              filters: filters ? filters : [],
              orderByColumns: [],
              dataFields: [
                {
                  tableName: 'SALES',
                  columns: [
                    {
                      aliasName: '',
                      columnName: 'string',
                      displayName: 'String',
                      filterEligible: true,
                      joinEligible: true,
                      name: 'string',
                      table: 'sales',
                      type: 'string',
                      visibleIndex: 0
                    },
                    {
                      aliasName: '',
                      columnName: 'long',
                      displayName: 'Long',
                      filterEligible: true,
                      joinEligible: false,
                      name: 'long',
                      table: 'sample',
                      type: 'long',
                      visibleIndex: 1
                    },
                    {
                      aliasName: '',
                      columnName: 'float',
                      displayName: 'Float',
                      filterEligible: true,
                      joinEligible: false,
                      name: 'float',
                      table: 'sales',
                      type: 'float',
                      format: {
                        precision: 2
                      },
                      visibleIndex: 3
                    },
                    {
                      aliasName: '',
                      columnName: 'date',
                      displayName: 'Date',
                      filterEligible: true,
                      joinEligible: false,
                      name: 'date',
                      table: 'sales',
                      type: 'date',
                      format: 'yyyy-MM-dd',
                      visibleIndex: 5
                    },
                    {
                      aliasName: '',
                      columnName: 'integer',
                      displayName: 'Integer',
                      filterEligible: true,
                      joinEligible: false,
                      name: 'integer',
                      table: 'sample',
                      type: 'integer',
                      visibleIndex: 2
                    },
                    {
                      aliasName: '',
                      columnName: 'double',
                      displayName: 'Double',
                      filterEligible: true,
                      joinEligible: false,
                      name: 'double',
                      table: 'sales',
                      type: 'double',
                      format: {
                        precision: 2
                      },
                      visibleIndex: 4
                    }
                  ]
                }
              ],
              joins: []
            },
            edit: false,
            categoryId: subCategoryId
          }
        ]
      }
    };

    let execute = {
      contents: {
        keys: [
          {
            customerCode: customerCode,
            module: 'ANALYZE',
            id: id,
            type: analysisType
          }
        ],
        action: action,
        executedBy: loginId,
        page: 1,
        pageSize: 25,
        analyze: [
          {
            type: analysisType,
            semanticId: semanticId,
            metricName: dataSetName,
            name: analysisName,
            description: analysisDesc,
            scheduled: null,
            statusMessage: 'Entity has retrieved successfully',
            id: id,
            createdBy: 'sipadmin@synchronoss.com',
            customerCode: customerCode,
            projectCode: 'workbench',
            saved: false,
            username: 'sipadmin@synchronoss.com',
            module: 'ANALYZE',
            artifacts: [
              {
                artifactName: 'SALES',
                columns: [
                  {
                    aliasName: '',
                    columnName: 'string',
                    displayName: 'String',
                    filterEligible: true,
                    joinEligible: true,
                    name: 'string',
                    table: 'sales',
                    type: 'string',
                    visibleIndex: 0
                  },
                  {
                    aliasName: '',
                    columnName: 'long',
                    displayName: 'Long',
                    filterEligible: true,
                    joinEligible: false,
                    name: 'long',
                    table: 'sample',
                    type: 'long',
                    visibleIndex: 1
                  },
                  {
                    aliasName: '',
                    columnName: 'float',
                    displayName: 'Float',
                    filterEligible: true,
                    joinEligible: false,
                    name: 'float',
                    table: 'sales',
                    type: 'float',
                    format: {
                      precision: 2
                    },
                    visibleIndex: 3
                  },
                  {
                    aliasName: '',
                    columnName: 'date',
                    displayName: 'Date',
                    filterEligible: true,
                    joinEligible: false,
                    name: 'date',
                    table: 'sales',
                    type: 'date',
                    format: 'yyyy-MM-dd',
                    visibleIndex: 5
                  },
                  {
                    aliasName: '',
                    columnName: 'integer',
                    displayName: 'Integer',
                    filterEligible: true,
                    joinEligible: false,
                    name: 'integer',
                    table: 'sample',
                    type: 'integer',
                    visibleIndex: 2
                  },
                  {
                    aliasName: '',
                    columnName: 'double',
                    displayName: 'Double',
                    filterEligible: true,
                    joinEligible: false,
                    name: 'double',
                    table: 'sales',
                    type: 'double',
                    format: {
                      precision: 2
                    },
                    visibleIndex: 4
                  }
                ],
                artifactPosition: [19, 8]
              },
              {
                artifactName: 'PRODUCT',
                columns: [
                  {
                    aliasName: '',
                    columnName: 'string_2',
                    displayName: 'String_2',
                    filterEligible: true,
                    joinEligible: true,
                    name: 'string_2',
                    table: 'product',
                    type: 'string'
                  },
                  {
                    aliasName: '',
                    columnName: 'long_2',
                    displayName: 'Long_2',
                    filterEligible: true,
                    joinEligible: false,
                    name: 'long_2',
                    table: 'sample',
                    type: 'long'
                  },
                  {
                    aliasName: '',
                    columnName: 'float_2',
                    displayName: 'Float_2',
                    filterEligible: true,
                    joinEligible: false,
                    name: 'float_2',
                    table: 'product',
                    type: 'float'
                  },
                  {
                    aliasName: '',
                    columnName: 'date_2',
                    displayName: 'Date_2',
                    filterEligible: true,
                    joinEligible: false,
                    name: 'date_2',
                    table: 'product',
                    type: 'date'
                  },
                  {
                    aliasName: '',
                    columnName: 'integer_2',
                    displayName: 'Integer_2',
                    filterEligible: true,
                    joinEligible: false,
                    name: 'integer_2',
                    table: 'sample',
                    type: 'integer'
                  },
                  {
                    aliasName: '',
                    columnName: 'double_2',
                    displayName: 'Double_2',
                    filterEligible: true,
                    joinEligible: false,
                    name: 'double_2',
                    table: 'sales',
                    type: 'double'
                  }
                ],
                artifactPosition: [420, 0]
              }
            ],
            repository: [
              {
                format: 'ndjson',
                name: 'SALES',
                physicalLocation:
                  '/var/sip/services/saw-analyze-samples/sample-spark/data-sales.ndjson'
              },
              {
                format: 'ndjson',
                name: 'PRODUCT',
                physicalLocation:
                  '/var/sip/services/saw-analyze-samples/sample-spark/data-product.ndjson'
              }
            ],
            parentDataSetIds: [
              'SALES::json::1539937726998',
              'PRODUCT::json::1539937737836'
            ],
            modifiedTime: currentTimeStamp,
            createdTime: currentTimeStamp,
            parentDataSetNames: ['SALES', 'PRODUCT'],
            createdTimestamp: currentTimeStamp,
            userId: userId,
            userFullName: loginId,
            sqlBuilder: {
              booleanCriteria: 'AND',
              filters: filters ? filters : [],
              orderByColumns: [],
              dataFields: [
                {
                  tableName: 'SALES',
                  columns: [
                    {
                      aliasName: '',
                      columnName: 'string',
                      displayName: 'String',
                      filterEligible: true,
                      joinEligible: true,
                      name: 'string',
                      table: 'sales',
                      type: 'string',
                      visibleIndex: 0
                    },
                    {
                      aliasName: '',
                      columnName: 'long',
                      displayName: 'Long',
                      filterEligible: true,
                      joinEligible: false,
                      name: 'long',
                      table: 'sample',
                      type: 'long',
                      visibleIndex: 1
                    },
                    {
                      aliasName: '',
                      columnName: 'float',
                      displayName: 'Float',
                      filterEligible: true,
                      joinEligible: false,
                      name: 'float',
                      table: 'sales',
                      type: 'float',
                      format: {
                        precision: 2
                      },
                      visibleIndex: 3
                    },
                    {
                      aliasName: '',
                      columnName: 'date',
                      displayName: 'Date',
                      filterEligible: true,
                      joinEligible: false,
                      name: 'date',
                      table: 'sales',
                      type: 'date',
                      format: 'yyyy-MM-dd',
                      visibleIndex: 5
                    },
                    {
                      aliasName: '',
                      columnName: 'integer',
                      displayName: 'Integer',
                      filterEligible: true,
                      joinEligible: false,
                      name: 'integer',
                      table: 'sample',
                      type: 'integer',
                      visibleIndex: 2
                    },
                    {
                      aliasName: '',
                      columnName: 'double',
                      displayName: 'Double',
                      filterEligible: true,
                      joinEligible: false,
                      name: 'double',
                      table: 'sales',
                      type: 'double',
                      format: {
                        precision: 2
                      },
                      visibleIndex: 4
                    }
                  ]
                }
              ],
              joins: []
            },
            edit: false,
            categoryId: subCategoryId,
            query:
              'SELECT SALES.string, SALES.long, SALES.float, SALES.date, SALES.integer, SALES.double FROM SALES',
            executionType: 'publish'
          }
        ]
      }
    };

    if (action === 'update') {
      body = update;
    } else if (action === 'execute') {
      body = execute;
    } else {
      throw new Error('Invalid action: ' + action);
    }
    return body;
  }

  getChartBody(
    customerCode,
    id,
    action,
    dataSetName,
    semanticId,
    userId,
    loginId,
    analysisName,
    analysisDesc,
    subCategoryId,
    currentTimeStamp,
    analysisType,
    subType,
    filters = null
  ) {
    const save = {
      metricName: null,
      semanticId: semanticId,
      type: analysisType,
      chartType: subType,
      name: analysisName,
      description: analysisDesc,
      scheduled: null,
      supports: [],
      id: id,
      parentAnalysisId: null,
      category: subCategoryId,
      customerCode: customerCode,
      projectCode: 'workbench',
      module: 'ANALYZE',
      createdTime: currentTimeStamp,
      createdBy: loginId,
      modifiedTime: currentTimeStamp,
      modifiedBy: loginId,
      sipQuery: {
        artifacts: [
          {
            artifactsName: 'sample',
            fields: [
              {
                aggregate: 'sum',
                area: 'y',
                columnName: 'double',
                displayType: 'column',
                dataField: 'double',
                displayName: 'Double',
                name: 'double',
                type: 'double',
                table: 'sample',
                areaIndex: 0
              },
              {
                area: 'x',
                columnName: 'float',
                dataField: 'float',
                displayName: 'Float',
                name: 'float',
                type: 'float',
                table: 'sample',
                areaIndex: 0
              }
            ]
          }
        ],
        booleanCriteria: 'AND',
        filters: filters ? filters : [],
        sorts: [
          {
            order: 'asc',
            columnName: 'float',
            type: 'float'
          }
        ],
        store: {
          dataStore: 'sampleAlias/sample',
          storageType: 'ES'
        }
      },
      chartOptions: {
        isInverted: false,
        legend: {
          align: 'right',
          layout: 'vertical'
        },
        chartTitle: analysisName,
        chartType: subType,
        xAxis: {
          title: null
        },
        yAxis: {
          title: null
        },
        labelOptions: {
          enabled: false,
          value: ''
        }
      },
      designerEdit: null,
      artifacts: [
        {
          artifactName: 'sample',
          columns: [
            {
              aliasName: '',
              columnName: 'string.keyword',
              displayName: 'String',
              filterEligible: true,
              joinEligible: false,
              name: 'string',
              table: 'sample',
              type: 'string'
            },
            {
              aliasName: '',
              columnName: 'long',
              displayName: 'Long',
              filterEligible: true,
              joinEligible: false,
              kpiEligible: true,
              name: 'long',
              table: 'sample',
              type: 'long'
            },
            {
              aliasName: '',
              columnName: 'float',
              displayName: 'Float',
              filterEligible: true,
              joinEligible: false,
              kpiEligible: true,
              name: 'float',
              table: 'sample',
              type: 'float',
              area: 'x',
              checked: true,
              areaIndex: 0
            },
            {
              aliasName: '',
              columnName: 'date',
              displayName: 'Date',
              filterEligible: true,
              joinEligible: false,
              kpiEligible: true,
              name: 'date',
              table: 'sample',
              type: 'date'
            },
            {
              aliasName: '',
              columnName: 'integer',
              displayName: 'Integer',
              filterEligible: true,
              joinEligible: false,
              kpiEligible: true,
              name: 'integer',
              table: 'sample',
              type: 'integer'
            },
            {
              aliasName: '',
              columnName: 'double',
              displayName: 'Double',
              filterEligible: true,
              joinEligible: false,
              kpiEligible: true,
              name: 'double',
              table: 'sample',
              type: 'double',
              area: 'y',
              checked: true,
              aggregate: 'sum',
              comboType: 'column',
              areaIndex: 0
            }
          ]
        }
      ],
      sqlBuilder: {
        booleanCriteria: 'AND',
        filters: filters ? filters : [],
        sorts: [
          {
            order: 'asc',
            columnName: 'float',
            type: 'float'
          }
        ],
        dataFields: [
          {
            aggregate: 'sum',
            checked: 'y',
            columnName: 'double',
            name: 'double',
            displayName: 'Double',
            table: 'sample',
            tableName: 'sample',
            type: 'double'
          }
        ],
        nodeFields: [
          {
            checked: 'x',
            columnName: 'float',
            name: 'float',
            displayName: 'Float',
            table: 'sample',
            tableName: 'sample',
            type: 'float'
          }
        ]
      },
      categoryId: subCategoryId,
      saved: true
    };

    if (action === 'update') {
      return save;
    } else {
      throw new Error('Invalid action: ' + action);
    }
    return null;
  }

  /**
   * @description Builds delete playload for analyze module
   * @param {String} customerCode
   * @param {String} id
   * @returns {Object}
   */
  getAnalyzeDeletePayload(customerCode, id) {
    let deletePayload = {
      contents: {
        keys: [
          {
            customerCode: customerCode,
            module: 'ANALYZE',
            id: id
          }
        ],
        action: 'delete'
      }
    };
    return deletePayload;
  }

  getAnalysisCreatePayload(semanticId, analysisType, customerCode) {
    if (
      analysisType === Constants.CHART ||
      analysisType === Constants.PIVOT ||
      analysisType === Constants.ES_REPORT
    ) {
      return {
        type: analysisType,
        semanticId: semanticId,
        name: 'Untitled Analysis',
        description: '',
        customerCode: customerCode,
        projectCode: 'workbench',
        module: 'ANALYZE',
        sipQuery: {
          artifacts: [],
          booleanCriteria: 'AND',
          filters: [],
          sorts: [],
          store: {
            dataStore: 'sampleAlias/sample',
            storageType: 'ES'
          }
        }
      };
    } else {
      return {
        contents: {
          keys: [
            {
              customerCode: customerCode,
              module: 'ANALYZE',
              id: semanticId,
              analysisType: analysisType
            }
          ],
          action: 'create'
        }
      };
    }
  }
}

module.exports = RequestModel;
