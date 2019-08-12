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
    let updatenew = {
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
            artifactsName: 'sales',
            fields: [
              {
                columnName: 'date',
                dataField: 'date',
                displayName: 'Date',
                groupInterval: null,
                name: 'date',
                type: 'date',
                table: 'sales',
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
                table: 'sales',
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
                table: 'sales',
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
                table: 'sales',
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
                table: 'sales',
                format: {},
                visibleIndex: 4
              },
              {
                columnName: 'string',
                dataField: 'string',
                displayName: 'String',
                groupInterval: null,
                name: 'string',
                type: 'string',
                table: 'sales',
                visibleIndex: 5
              }
            ]
          }
        ],
        booleanCriteria: 'AND',
        filters: filters ? filters : [],
        sorts: [],
        joins: [],
        store: {},
        semanticId: semanticId,
        query:
          'SELECT sales.date, sales.double, sales.float, sales.integer, sales.long, sales.string FROM sales'
      },
      designerEdit: false,
      categoryId: subCategoryId,
      scheduled: null,
      supports: [
        {
          category: 'table',
          children: [
            {
              icon: 'icon-report',
              label: 'Report',
              type: 'table:report'
            }
          ],
          label: 'TABLES'
        }
      ],
      saved: true
    };

    if (action === 'update') {
      return updatenew;
    } else {
      throw new Error('Invalid action: ' + action);
    }
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
      analysisType === Constants.ES_REPORT ||
      analysisType === Constants.REPORT
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
            dataStore:
              analysisType === Constants.REPORT ? null : 'sampleAlias/sample',
            storageType: analysisType === Constants.REPORT ? null : 'ES'
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
