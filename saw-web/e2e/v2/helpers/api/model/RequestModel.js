'use strict';
class RequestModel {
    //Payload to create,update,execute analysis
    getPayloadPivotChart(customerCode, chartID, action, dataSetName, semanticId, userId, loginId, analysisName,
                analysisDesc, subCategoryId, currentTimeStamp, analysisType, subType, filters = null) {

        let chartBody = {
            'contents': {
              'keys': [
                {
                  'customerCode': customerCode,
                  'module': 'ANALYZE',
                  'id': chartID,
                  'type': analysisType
                }
              ],
              'action': action,
              'analyze': [
                {
                  'id': chartID,
                  'dataSecurityKey': '',
                  'type': 'chart',
                  'module': 'ANALYZE',
                  'metric': 'sample-elasticsearch',
                  'metricName': dataSetName,
                  'customerCode': customerCode,
                  'disabled': 'false',
                  'checked': 'false',
                  'esRepository': {
                    'storageType': 'ES',
                    'indexName': 'sample',
                    'type': 'sample'
                  },
                  'artifacts': [
                    {
                      'artifactName': 'sample',
                      'columns': [
                        {
                          'name': 'string.keyword',
                          'type': 'string',
                          'columnName': 'string.keyword',
                          'displayName': 'String',
                          'aliasName': '',
                          'table': 'sample',
                          'joinEligible': false,
                          'filterEligible': true,
                          'tableName': 'sample',
                          'checked': 'g'
                        },
                        {
                          'name': 'long',
                          'type': 'long',
                          'columnName': 'long',
                          'displayName': 'Long',
                          'aliasName': '',
                          'table': 'sample',
                          'joinEligible': false,
                          'kpiEligible': true,
                          'filterEligible': true,
                          'tableName': 'sample'
                        },
                        {
                          'name': 'float',
                          'type': 'float',
                          'columnName': 'float',
                          'displayName': 'Float',
                          'aliasName': '',
                          'table': 'sample',
                          'joinEligible': false,
                          'kpiEligible': true,
                          'filterEligible': true,
                          'tableName': 'sample'
                        },
                        {
                          'name': 'date',
                          'type': 'date',
                          'columnName': 'date',
                          'displayName': 'Date',
                          'aliasName': '',
                          'table': 'sample',
                          'joinEligible': false,
                          'kpiEligible': true,
                          'filterEligible': true,
                          'tableName': 'sample',
                          'checked': 'x',
                          'dateFormat': 'MMM d YYYY'
                        },
                        {
                          'name': 'integer',
                          'type': 'integer',
                          'columnName': 'integer',
                          'displayName': 'Integer',
                          'aliasName': '',
                          'table': 'sample',
                          'joinEligible': false,
                          'kpiEligible': true,
                          'filterEligible': true,
                          'tableName': 'sample'
                        },
                        {
                          'name': 'double',
                          'type': 'double',
                          'columnName': 'double',
                          'displayName': 'Double',
                          'aliasName': '',
                          'table': 'sales',
                          'joinEligible': false,
                          'kpiEligible': true,
                          'filterEligible': true,
                          'tableName': 'sample',
                          'checked': 'y',
                          'aggregate': 'sum',
                          'comboType': 'column'
                        }
                      ]
                    }
                  ],
                  'repository': {
                    'storageType': 'DL',
                    'objects': [

                    ],
                    '_number_of_elements': 0
                  },
                  'semanticId': semanticId,
                  'createdTimestamp': currentTimeStamp,
                  'userId': userId,
                  'userFullName': loginId,
                  'chartType': subType,
                  'name': analysisName,
                  'description': analysisDesc,
                  'categoryId': subCategoryId,
                  'scheduled': null,
                  'sqlBuilder': {
                    'booleanCriteria': 'AND',
                    'filters': filters ? filters :[],
                    'dataFields': [
                      {
                        'name': 'double',
                        'type': 'double',
                        'columnName': 'double',
                        'displayName': 'Double',
                        'aliasName': '',
                        'table': 'sales',
                        'joinEligible': false,
                        'kpiEligible': true,
                        'filterEligible': true,
                        'tableName': 'sample',
                        'checked': 'y',
                        'aggregate': 'sum',
                        'comboType': 'column'
                      }
                    ],
                    'nodeFields': [
                      {
                        'name': 'string.keyword',
                        'type': 'string',
                        'columnName': 'string.keyword',
                        'displayName': 'String',
                        'aliasName': '',
                        'table': 'sample',
                        'joinEligible': false,
                        'filterEligible': true,
                        'tableName': 'sample',
                        'checked': 'g'
                      },
                      {
                        'name': 'date',
                        'type': 'date',
                        'columnName': 'date',
                        'displayName': 'Date',
                        'aliasName': '',
                        'table': 'sample',
                        'joinEligible': false,
                        'kpiEligible': true,
                        'filterEligible': true,
                        'tableName': 'sample',
                        'checked': 'x',
                        'dateFormat': 'MMM d YYYY'
                      }
                    ],
                    'sorts': [
                      {
                        'columnName': 'string.keyword',
                        'type': 'string',
                        'order': 'asc'
                      },
                      {
                        'columnName': 'date',
                        'type': 'date',
                        'order': 'asc'
                      }
                    ]
                  },
                  'xAxis': {
                    'title': null
                  },
                  'yAxis': {
                    'title': null
                  },
                  'isInverted': false,
                  'isStockChart': false,
                  'legend': {
                    'align': 'right',
                    'layout': 'vertical'
                  },
                  'saved': true
                }
              ]
            }
          }

        return chartBody;
    }

    getEsReportBody(customerCode, id, action, dataSetName, semanticId, userId, loginId, analysisName,
      analysisDesc, subCategoryId, currentTimeStamp, analysisType, subType, filters = null) {
      let body;
      let update = {
        'contents': {
          'keys': [
            {
              'customerCode': customerCode,
              'module': 'ANALYZE',
              'id': id,
              'type': analysisType
            }
          ],
          'action': action,
          'analyze': [
            {
              'type': analysisType,
              'semanticId': semanticId,
              'metricName': dataSetName,
              'name': analysisName,
              'description': analysisDesc,
              'scheduled': null,
              'id': id,
              'dataSecurityKey': '',
              'module': 'ANALYZE',
              'metric': 'sample-elasticsearch',
              'customerCode': customerCode,
              'disabled': 'false',
              'checked': 'false',
              'esRepository': {
                'storageType': 'ES',
                'indexName': 'sample',
                'type': 'sample'
              },
              'artifacts': [
                {
                  'artifactName': 'sample',
                  'columns': [
                    {
                      'name': 'string',
                      'type': 'string',
                      'columnName': 'string.keyword',
                      'displayName': 'String',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'filterEligible': true,
                      'checked': true
                    },
                    {
                      'name': 'long',
                      'type': 'long',
                      'columnName': 'long',
                      'displayName': 'Long',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true,
                      'checked': true
                    },
                    {
                      'name': 'float',
                      'type': 'float',
                      'columnName': 'float',
                      'displayName': 'Float',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true,
                      'checked': true,
                      'format': {
                        'precision': 2
                      }
                    },
                    {
                      'name': 'date',
                      'type': 'date',
                      'columnName': 'date',
                      'displayName': 'Date',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true,
                      'checked': true,
                      'format': 'yyyy-MM-dd'
                    },
                    {
                      'name': 'integer',
                      'type': 'integer',
                      'columnName': 'integer',
                      'displayName': 'Integer',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true,
                      'checked': true
                    },
                    {
                      'name': 'double',
                      'type': 'double',
                      'columnName': 'double',
                      'displayName': 'Double',
                      'aliasName': '',
                      'table': 'sales',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true,
                      'checked': true,
                      'format': {
                        'precision': 2
                      }
                    }
                  ],
                  'artifactPosition': [
                    20,
                    4
                  ]
                }
              ],
              'repository': {
                'storageType': 'DL',
                'objects': [

                ],
                '_number_of_elements': 0
              },
              'createdTimestamp': currentTimeStamp,
              'userId': userId,
              'userFullName': loginId,
              'sqlBuilder': {
                'booleanCriteria': 'AND',
                'filters': filters ? filters :[],
                'sorts': [

                ],
                'dataFields': [
                  {
                    'name': 'string',
                    'type': 'string',
                    'columnName': 'string.keyword',
                    'displayName': 'String',
                    'aliasName': '',
                    'table': 'sample',
                    'joinEligible': false,
                    'filterEligible': true,
                    'checked': true
                  },
                  {
                    'name': 'long',
                    'type': 'long',
                    'columnName': 'long',
                    'displayName': 'Long',
                    'aliasName': '',
                    'table': 'sample',
                    'joinEligible': false,
                    'kpiEligible': true,
                    'filterEligible': true,
                    'checked': true
                  },
                  {
                    'name': 'float',
                    'type': 'float',
                    'columnName': 'float',
                    'displayName': 'Float',
                    'aliasName': '',
                    'table': 'sample',
                    'joinEligible': false,
                    'kpiEligible': true,
                    'filterEligible': true,
                    'checked': true,
                    'format': {
                      'precision': 2
                    }
                  },
                  {
                    'name': 'date',
                    'type': 'date',
                    'columnName': 'date',
                    'displayName': 'Date',
                    'aliasName': '',
                    'table': 'sample',
                    'joinEligible': false,
                    'kpiEligible': true,
                    'filterEligible': true,
                    'checked': true,
                    'format': 'yyyy-MM-dd'
                  },
                  {
                    'name': 'integer',
                    'type': 'integer',
                    'columnName': 'integer',
                    'displayName': 'Integer',
                    'aliasName': '',
                    'table': 'sample',
                    'joinEligible': false,
                    'kpiEligible': true,
                    'filterEligible': true,
                    'checked': true
                  },
                  {
                    'name': 'double',
                    'type': 'double',
                    'columnName': 'double',
                    'displayName': 'Double',
                    'aliasName': '',
                    'table': 'sales',
                    'joinEligible': false,
                    'kpiEligible': true,
                    'filterEligible': true,
                    'checked': true,
                    'format': {
                      'precision': 2
                    }
                  }
                ]
              },
              'edit': false,
              'categoryId': subCategoryId,
              'saved': true
            }
          ]
        }
      };

     let executeBody = {
      'contents': {
        'keys': [
          {
            'customerCode': customerCode,
            'module': 'ANALYZE',
            'id': id,
            'type': analysisType
          }
        ],
        'action': action,
        "executedBy": loginId,
        'page': 1,
        'pageSize': 10,
        'analyze': [
          {
            'type': analysisType,
            'semanticId': semanticId,
            'metricName': dataSetName,
            'name': analysisName,
            'description': analysisDesc,
            'scheduled': null,
            'id': id,
            'dataSecurityKey': '',
            'module': 'ANALYZE',
            'metric': 'sample-elasticsearch',
            'customerCode': customerCode,
            'disabled': 'false',
            'checked': 'false',
            'esRepository': {
              'storageType': 'ES',
              'indexName': 'sample',
              'type': 'sample'
            },
            'artifacts': [
              {
                'artifactName': 'sample',
                'columns': [
                  {
                    'name': 'string',
                    'type': 'string',
                    'columnName': 'string.keyword',
                    'displayName': 'String',
                    'aliasName': '',
                    'table': 'sample',
                    'joinEligible': false,
                    'filterEligible': true,
                    'checked': true
                  },
                  {
                    'name': 'long',
                    'type': 'long',
                    'columnName': 'long',
                    'displayName': 'Long',
                    'aliasName': '',
                    'table': 'sample',
                    'joinEligible': false,
                    'kpiEligible': true,
                    'filterEligible': true,
                    'checked': true
                  },
                  {
                    'name': 'float',
                    'type': 'float',
                    'columnName': 'float',
                    'displayName': 'Float',
                    'aliasName': '',
                    'table': 'sample',
                    'joinEligible': false,
                    'kpiEligible': true,
                    'filterEligible': true,
                    'checked': true,
                    'format': {
                      'precision': 2
                    }
                  },
                  {
                    'name': 'date',
                    'type': 'date',
                    'columnName': 'date',
                    'displayName': 'Date',
                    'aliasName': '',
                    'table': 'sample',
                    'joinEligible': false,
                    'kpiEligible': true,
                    'filterEligible': true,
                    'checked': true,
                    'format': 'yyyy-MM-dd'
                  },
                  {
                    'name': 'integer',
                    'type': 'integer',
                    'columnName': 'integer',
                    'displayName': 'Integer',
                    'aliasName': '',
                    'table': 'sample',
                    'joinEligible': false,
                    'kpiEligible': true,
                    'filterEligible': true,
                    'checked': true
                  },
                  {
                    'name': 'double',
                    'type': 'double',
                    'columnName': 'double',
                    'displayName': 'Double',
                    'aliasName': '',
                    'table': 'sales',
                    'joinEligible': false,
                    'kpiEligible': true,
                    'filterEligible': true,
                    'checked': true,
                    'format': {
                      'precision': 2
                    }
                  }
                ],
                'artifactPosition': [
                  20,
                  4
                ]
              }
            ],
            'repository': {
              'storageType': 'DL',
              'objects': [

              ],
              '_number_of_elements': 0
            },
            'createdTimestamp': currentTimeStamp,
            'userId': userId,
            'userFullName': loginId,
            'sqlBuilder': {
              'booleanCriteria': 'AND',
              'filters': filters ? filters :[],
              'sorts': [

              ],
              'dataFields': [
                {
                  'name': 'string',
                  'type': 'string',
                  'columnName': 'string.keyword',
                  'displayName': 'String',
                  'aliasName': '',
                  'table': 'sample',
                  'joinEligible': false,
                  'filterEligible': true,
                  'checked': true
                },
                {
                  'name': 'long',
                  'type': 'long',
                  'columnName': 'long',
                  'displayName': 'Long',
                  'aliasName': '',
                  'table': 'sample',
                  'joinEligible': false,
                  'kpiEligible': true,
                  'filterEligible': true,
                  'checked': true
                },
                {
                  'name': 'float',
                  'type': 'float',
                  'columnName': 'float',
                  'displayName': 'Float',
                  'aliasName': '',
                  'table': 'sample',
                  'joinEligible': false,
                  'kpiEligible': true,
                  'filterEligible': true,
                  'checked': true,
                  'format': {
                    'precision': 2
                  }
                },
                {
                  'name': 'date',
                  'type': 'date',
                  'columnName': 'date',
                  'displayName': 'Date',
                  'aliasName': '',
                  'table': 'sample',
                  'joinEligible': false,
                  'kpiEligible': true,
                  'filterEligible': true,
                  'checked': true,
                  'format': 'yyyy-MM-dd'
                },
                {
                  'name': 'integer',
                  'type': 'integer',
                  'columnName': 'integer',
                  'displayName': 'Integer',
                  'aliasName': '',
                  'table': 'sample',
                  'joinEligible': false,
                  'kpiEligible': true,
                  'filterEligible': true,
                  'checked': true
                },
                {
                  'name': 'double',
                  'type': 'double',
                  'columnName': 'double',
                  'displayName': 'Double',
                  'aliasName': '',
                  'table': 'sales',
                  'joinEligible': false,
                  'kpiEligible': true,
                  'filterEligible': true,
                  'checked': true,
                  'format': {
                    'precision': 2
                  }
                }
              ]
            },
            'edit': false,
            'categoryId': subCategoryId,
            'saved': true,
            'executionType': 'publish'
          }
        ]
      }
      };

      if(action === 'update') {
        body = update;
      } else if(action === 'execute') {
        body = executeBody;
      } else {
        throw new Error('Invalid action: '+action);
      }

      return body;

    }

    getPivotBody(customerCode, id, action, dataSetName, semanticId, userId, loginId, analysisName,
      analysisDesc, subCategoryId, currentTimeStamp, analysisType, subType, filters = null) {
      let body;
      let update = {
        'contents': {
          'keys': [
            {
              'customerCode':customerCode,
              'module': 'ANALYZE',
              'id': id,
              'type': analysisType
            }
          ],
          'action': action,
          'analyze': [
            {
              'type': analysisType,
              'semanticId': semanticId,
              'metricName': dataSetName,
              'name': analysisName,
              'description': analysisDesc,
              'scheduled': null,
              'id': id,
              'dataSecurityKey': '',
              'module': 'ANALYZE',
              'metric': 'sample-elasticsearch',
              'customerCode': customerCode,
              'disabled': 'false',
              'checked': 'false',
              'esRepository': {
                'storageType': 'ES',
                'indexName': 'sample',
                'type': 'sample'
              },
              'artifacts': [
                {
                  'artifactName': 'sample',
                  'columns': [
                    {
                      'name': 'string',
                      'type': 'string',
                      'columnName': 'string.keyword',
                      'displayName': 'String',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'filterEligible': true,
                      'area': 'row',
                      'checked': true,
                      'dateInterval': 'day',
                      'areaIndex': 0
                    },
                    {
                      'name': 'long',
                      'type': 'long',
                      'columnName': 'long',
                      'displayName': 'Long',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true
                    },
                    {
                      'name': 'float',
                      'type': 'float',
                      'columnName': 'float',
                      'displayName': 'Float',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true,
                      'area': null,
                      'checked': false,
                      'aggregate': 'sum',
                      'areaIndex': null
                    },
                    {
                      'name': 'date',
                      'type': 'date',
                      'columnName': 'date',
                      'displayName': 'Date',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true,
                      'area': null,
                      'checked': false,
                      'dateInterval': 'day',
                      'areaIndex': null
                    },
                    {
                      'name': 'integer',
                      'type': 'integer',
                      'columnName': 'integer',
                      'displayName': 'Integer',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true
                    },
                    {
                      'name': 'double',
                      'type': 'double',
                      'columnName': 'double',
                      'displayName': 'Double',
                      'aliasName': '',
                      'table': 'sales',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true,
                      'area': 'data',
                      'checked': true,
                      'aggregate': 'sum',
                      'areaIndex': 0
                    }
                  ]
                }
              ],
              'repository': {
                'storageType': 'DL',
                'objects': [

                ],
                '_number_of_elements': 0
              },
              'createdTimestamp': currentTimeStamp,
              'userId': userId,
              'userFullName':loginId,
              'sqlBuilder': {
                'booleanCriteria': 'AND',
                'filters': filters ? filters :[],
                'sorts': [

                ],
                'rowFields': [
                  {
                    'type': 'string',
                    'columnName': 'string.keyword',
                    'aggregate': null,
                    'name': null,
                    'dateInterval': null
                  }
                ],
                'columnFields': [

                ],
                'dataFields': [
                  {
                    'type': 'double',
                    'columnName': 'double',
                    'aggregate': 'sum',
                    'name': 'double',
                    'dateInterval': null
                  }
                ]
              },
              'edit': false,
              'categoryId': subCategoryId,
              'saved': true
            }
          ]
        }
      };

      let execute = {
        'contents': {
          'keys': [
            {
              'customerCode': customerCode,
              'module': 'ANALYZE',
              'id': id,
              'type': analysisType
            }
          ],
          'action': action,
          "executedBy": loginId,
          'page': 1,
          'pageSize': 10,
          'analyze': [
            {
              'type': analysisType,
              'semanticId': semanticId,
              'metricName': dataSetName,
              'name': analysisName,
              'description': analysisDesc,
              'scheduled': null,
              'id': id,
              'dataSecurityKey': '',
              'module': 'ANALYZE',
              'metric': 'sample-elasticsearch',
              'customerCode': customerCode,
              'disabled': 'false',
              'checked': 'false',
              'esRepository': {
                'storageType': 'ES',
                'indexName': 'sample',
                'type': 'sample'
              },
              'artifacts': [
                {
                  'artifactName': 'sample',
                  'columns': [
                    {
                      'name': 'string',
                      'type': 'string',
                      'columnName': 'string.keyword',
                      'displayName': 'String',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'filterEligible': true,
                      'area': 'row',
                      'checked': true,
                      'dateInterval': 'day',
                      'areaIndex': 0
                    },
                    {
                      'name': 'long',
                      'type': 'long',
                      'columnName': 'long',
                      'displayName': 'Long',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true
                    },
                    {
                      'name': 'float',
                      'type': 'float',
                      'columnName': 'float',
                      'displayName': 'Float',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true,
                      'area': null,
                      'checked': false,
                      'aggregate': 'sum',
                      'areaIndex': null
                    },
                    {
                      'name': 'date',
                      'type': 'date',
                      'columnName': 'date',
                      'displayName': 'Date',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true,
                      'area': null,
                      'checked': false,
                      'dateInterval': 'day',
                      'areaIndex': null
                    },
                    {
                      'name': 'integer',
                      'type': 'integer',
                      'columnName': 'integer',
                      'displayName': 'Integer',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true
                    },
                    {
                      'name': 'double',
                      'type': 'double',
                      'columnName': 'double',
                      'displayName': 'Double',
                      'aliasName': '',
                      'table': 'sales',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true,
                      'area': 'data',
                      'checked': true,
                      'aggregate': 'sum',
                      'areaIndex': 0
                    }
                  ]
                }
              ],
              'repository': {
                'storageType': 'DL',
                'objects': [

                ],
                '_number_of_elements': 0
              },
              'createdTimestamp': currentTimeStamp,
              'userId': userId,
              'userFullName': loginId,
              'sqlBuilder': {
                'booleanCriteria': 'AND',
                'filters': filters ? filters :[],
                'sorts': [

                ],
                'rowFields': [
                  {
                    'type': 'string',
                    'columnName': 'string.keyword',
                    'aggregate': null,
                    'name': null,
                    'dateInterval': null
                  }
                ],
                'columnFields': [

                ],
                'dataFields': [
                  {
                    'type': 'double',
                    'columnName': 'double',
                    'aggregate': 'sum',
                    'name': 'double',
                    'dateInterval': null
                  }
                ]
              },
              'edit': false,
              'executionType': 'publish',
              'categoryId': subCategoryId,
              'saved': true
            }
          ]
        }
      };

      if(action === 'update') {
        body = update;
      } else if(action === 'execute') {
        body = execute;
      } else {
        throw new Error('Invalid action: '+action);
      }
      return body;


    }

    getReportBody(customerCode, id, action, dataSetName, semanticId, userId, loginId, analysisName,
      analysisDesc, subCategoryId, currentTimeStamp, analysisType, subType, filters = null) {
      let body;

      let update = {
        'contents': {
          'keys': [
            {
              'customerCode': customerCode,
              'module': 'ANALYZE',
              'id': id,
              'type': analysisType
            }
          ],
          'action': action,
          'analyze': [
            {
              'type': analysisType,
              'semanticId': semanticId,
              'metricName': dataSetName,
              'name': analysisName,
              'description': analysisDesc,
              'scheduled': null,
              'statusMessage': 'Entity has retrieved successfully',
              'id': id,
              'createdBy': 'sipadmin@synchronoss.com',
              'customerCode': customerCode,
              'projectCode': 'workbench',
              'saved': true,
              'username': 'sipadmin@synchronoss.com',
              'module': 'ANALYZE',
              'artifacts': [
                {
                  'artifactName': 'SALES',
                  'columns': [
                    {
                      'aliasName': '',
                      'columnName': 'string',
                      'displayName': 'String',
                      'filterEligible': true,
                      'joinEligible': true,
                      'name': 'string',
                      'table': 'sales',
                      'type': 'string',
                      'visibleIndex': 0
                    },
                    {
                      'aliasName': '',
                      'columnName': 'long',
                      'displayName': 'Long',
                      'filterEligible': true,
                      'joinEligible': false,
                      'name': 'long',
                      'table': 'sample',
                      'type': 'long',
                      'visibleIndex': 1
                    },
                    {
                      'aliasName': '',
                      'columnName': 'float',
                      'displayName': 'Float',
                      'filterEligible': true,
                      'joinEligible': false,
                      'name': 'float',
                      'table': 'sales',
                      'type': 'float',
                      'format': {
                        'precision': 2
                      },
                      'visibleIndex': 3
                    },
                    {
                      'aliasName': '',
                      'columnName': 'date',
                      'displayName': 'Date',
                      'filterEligible': true,
                      'joinEligible': false,
                      'name': 'date',
                      'table': 'sales',
                      'type': 'date',
                      'format': 'yyyy-MM-dd',
                      'visibleIndex': 5
                    },
                    {
                      'aliasName': '',
                      'columnName': 'integer',
                      'displayName': 'Integer',
                      'filterEligible': true,
                      'joinEligible': false,
                      'name': 'integer',
                      'table': 'sample',
                      'type': 'integer',
                      'visibleIndex': 2
                    },
                    {
                      'aliasName': '',
                      'columnName': 'double',
                      'displayName': 'Double',
                      'filterEligible': true,
                      'joinEligible': false,
                      'name': 'double',
                      'table': 'sales',
                      'type': 'double',
                      'format': {
                        'precision': 2
                      },
                      'visibleIndex': 4
                    }
                  ],
                  'artifactPosition': [19, 8]
                },
                {
                  'artifactName': 'PRODUCT',
                  'columns': [
                    {
                      'aliasName': '',
                      'columnName': 'string_2',
                      'displayName': 'String_2',
                      'filterEligible': true,
                      'joinEligible': true,
                      'name': 'string_2',
                      'table': 'product',
                      'type': 'string'
                    },
                    {
                      'aliasName': '',
                      'columnName': 'long_2',
                      'displayName': 'Long_2',
                      'filterEligible': true,
                      'joinEligible': false,
                      'name': 'long_2',
                      'table': 'sample',
                      'type': 'long'
                    },
                    {
                      'aliasName': '',
                      'columnName': 'float_2',
                      'displayName': 'Float_2',
                      'filterEligible': true,
                      'joinEligible': false,
                      'name': 'float_2',
                      'table': 'product',
                      'type': 'float'
                    },
                    {
                      'aliasName': '',
                      'columnName': 'date_2',
                      'displayName': 'Date_2',
                      'filterEligible': true,
                      'joinEligible': false,
                      'name': 'date_2',
                      'table': 'product',
                      'type': 'date'
                    },
                    {
                      'aliasName': '',
                      'columnName': 'integer_2',
                      'displayName': 'Integer_2',
                      'filterEligible': true,
                      'joinEligible': false,
                      'name': 'integer_2',
                      'table': 'sample',
                      'type': 'integer'
                    },
                    {
                      'aliasName': '',
                      'columnName': 'double_2',
                      'displayName': 'Double_2',
                      'filterEligible': true,
                      'joinEligible': false,
                      'name': 'double_2',
                      'table': 'sales',
                      'type': 'double'
                    }
                  ],
                  'artifactPosition': [420, 0]
                }
              ],
              'repository': [
                {
                  'format': 'ndjson',
                  'name': 'SALES',
                  'physicalLocation': '/var/sip/services/saw-analyze-samples/sample-spark/data-sales.ndjson'
                },
                {
                  'format': 'ndjson',
                  'name': 'PRODUCT',
                  'physicalLocation': '/var/sip/services/saw-analyze-samples/sample-spark/data-product.ndjson'
                }
              ],
              'parentDataSetIds': [
                'SALES::json::1539937726998',
                'PRODUCT::json::1539937737836'
              ],
              'modifiedTime': currentTimeStamp,
              'createdTime': currentTimeStamp,
              'parentDataSetNames': ['SALES', 'PRODUCT'],
              'createdTimestamp': currentTimeStamp,
              'userId': userId,
              'userFullName': loginId,
              'sqlBuilder': {
                'booleanCriteria': 'AND',
                'filters': filters ? filters : [],
                'orderByColumns': [],
                'dataFields': [
                  {
                    'tableName': 'SALES',
                    'columns': [
                      {
                        'aliasName': '',
                        'columnName': 'string',
                        'displayName': 'String',
                        'filterEligible': true,
                        'joinEligible': true,
                        'name': 'string',
                        'table': 'sales',
                        'type': 'string',
                        'visibleIndex': 0
                      },
                      {
                        'aliasName': '',
                        'columnName': 'long',
                        'displayName': 'Long',
                        'filterEligible': true,
                        'joinEligible': false,
                        'name': 'long',
                        'table': 'sample',
                        'type': 'long',
                        'visibleIndex': 1
                      },
                      {
                        'aliasName': '',
                        'columnName': 'float',
                        'displayName': 'Float',
                        'filterEligible': true,
                        'joinEligible': false,
                        'name': 'float',
                        'table': 'sales',
                        'type': 'float',
                        'format': {
                          'precision': 2
                        },
                        'visibleIndex': 3
                      },
                      {
                        'aliasName': '',
                        'columnName': 'date',
                        'displayName': 'Date',
                        'filterEligible': true,
                        'joinEligible': false,
                        'name': 'date',
                        'table': 'sales',
                        'type': 'date',
                        'format': 'yyyy-MM-dd',
                        'visibleIndex': 5
                      },
                      {
                        'aliasName': '',
                        'columnName': 'integer',
                        'displayName': 'Integer',
                        'filterEligible': true,
                        'joinEligible': false,
                        'name': 'integer',
                        'table': 'sample',
                        'type': 'integer',
                        'visibleIndex': 2
                      },
                      {
                        'aliasName': '',
                        'columnName': 'double',
                        'displayName': 'Double',
                        'filterEligible': true,
                        'joinEligible': false,
                        'name': 'double',
                        'table': 'sales',
                        'type': 'double',
                        'format': {
                          'precision': 2
                        },
                        'visibleIndex': 4
                      }
                    ]
                  }
                ],
                'joins': []
              },
              'edit': false,
              'categoryId': subCategoryId
            }
          ]
        }
      };

      let execute = {
        'contents': {
          'keys': [
            {
              'customerCode': customerCode,
              'module': 'ANALYZE',
              'id': id,
              'type': analysisType
            }
          ],
          'action': action,
          'executedBy': loginId,
          'page': 1,
          'pageSize': 25,
          'analyze': [
            {
              'type': analysisType,
              'semanticId': semanticId,
              'metricName': dataSetName,
              'name': analysisName,
              'description': analysisDesc,
              'scheduled': null,
              'statusMessage': 'Entity has retrieved successfully',
              'id': id,
              'createdBy': 'sipadmin@synchronoss.com',
              'customerCode': customerCode,
              'projectCode': 'workbench',
              'saved': false,
              'username': 'sipadmin@synchronoss.com',
              'module': 'ANALYZE',
              'artifacts': [
                {
                  'artifactName': 'SALES',
                  'columns': [
                    {
                      'aliasName': '',
                      'columnName': 'string',
                      'displayName': 'String',
                      'filterEligible': true,
                      'joinEligible': true,
                      'name': 'string',
                      'table': 'sales',
                      'type': 'string',
                      'visibleIndex': 0
                    },
                    {
                      'aliasName': '',
                      'columnName': 'long',
                      'displayName': 'Long',
                      'filterEligible': true,
                      'joinEligible': false,
                      'name': 'long',
                      'table': 'sample',
                      'type': 'long',
                      'visibleIndex': 1
                    },
                    {
                      'aliasName': '',
                      'columnName': 'float',
                      'displayName': 'Float',
                      'filterEligible': true,
                      'joinEligible': false,
                      'name': 'float',
                      'table': 'sales',
                      'type': 'float',
                      'format': {
                        'precision': 2
                      },
                      'visibleIndex': 3
                    },
                    {
                      'aliasName': '',
                      'columnName': 'date',
                      'displayName': 'Date',
                      'filterEligible': true,
                      'joinEligible': false,
                      'name': 'date',
                      'table': 'sales',
                      'type': 'date',
                      'format': 'yyyy-MM-dd',
                      'visibleIndex': 5
                    },
                    {
                      'aliasName': '',
                      'columnName': 'integer',
                      'displayName': 'Integer',
                      'filterEligible': true,
                      'joinEligible': false,
                      'name': 'integer',
                      'table': 'sample',
                      'type': 'integer',
                      'visibleIndex': 2
                    },
                    {
                      'aliasName': '',
                      'columnName': 'double',
                      'displayName': 'Double',
                      'filterEligible': true,
                      'joinEligible': false,
                      'name': 'double',
                      'table': 'sales',
                      'type': 'double',
                      'format': {
                        'precision': 2
                      },
                      'visibleIndex': 4
                    }
                  ],
                  'artifactPosition': [19, 8]
                },
                {
                  'artifactName': 'PRODUCT',
                  'columns': [
                    {
                      'aliasName': '',
                      'columnName': 'string_2',
                      'displayName': 'String_2',
                      'filterEligible': true,
                      'joinEligible': true,
                      'name': 'string_2',
                      'table': 'product',
                      'type': 'string'
                    },
                    {
                      'aliasName': '',
                      'columnName': 'long_2',
                      'displayName': 'Long_2',
                      'filterEligible': true,
                      'joinEligible': false,
                      'name': 'long_2',
                      'table': 'sample',
                      'type': 'long'
                    },
                    {
                      'aliasName': '',
                      'columnName': 'float_2',
                      'displayName': 'Float_2',
                      'filterEligible': true,
                      'joinEligible': false,
                      'name': 'float_2',
                      'table': 'product',
                      'type': 'float'
                    },
                    {
                      'aliasName': '',
                      'columnName': 'date_2',
                      'displayName': 'Date_2',
                      'filterEligible': true,
                      'joinEligible': false,
                      'name': 'date_2',
                      'table': 'product',
                      'type': 'date'
                    },
                    {
                      'aliasName': '',
                      'columnName': 'integer_2',
                      'displayName': 'Integer_2',
                      'filterEligible': true,
                      'joinEligible': false,
                      'name': 'integer_2',
                      'table': 'sample',
                      'type': 'integer'
                    },
                    {
                      'aliasName': '',
                      'columnName': 'double_2',
                      'displayName': 'Double_2',
                      'filterEligible': true,
                      'joinEligible': false,
                      'name': 'double_2',
                      'table': 'sales',
                      'type': 'double'
                    }
                  ],
                  'artifactPosition': [420, 0]
                }
              ],
              'repository': [
                {
                  'format': 'ndjson',
                  'name': 'SALES',
                  'physicalLocation': '/var/sip/services/saw-analyze-samples/sample-spark/data-sales.ndjson'
                },
                {
                  'format': 'ndjson',
                  'name': 'PRODUCT',
                  'physicalLocation': '/var/sip/services/saw-analyze-samples/sample-spark/data-product.ndjson'
                }
              ],
              'parentDataSetIds': [
                'SALES::json::1539937726998',
                'PRODUCT::json::1539937737836'
              ],
              'modifiedTime': currentTimeStamp,
              'createdTime': currentTimeStamp,
              'parentDataSetNames': ['SALES', 'PRODUCT'],
              'createdTimestamp': currentTimeStamp,
              'userId': userId,
              'userFullName': loginId,
              'sqlBuilder': {
                'booleanCriteria': 'AND',
                'filters': filters ? filters : [],
                'orderByColumns': [],
                'dataFields': [
                  {
                    'tableName': 'SALES',
                    'columns': [
                      {
                        'aliasName': '',
                        'columnName': 'string',
                        'displayName': 'String',
                        'filterEligible': true,
                        'joinEligible': true,
                        'name': 'string',
                        'table': 'sales',
                        'type': 'string',
                        'visibleIndex': 0
                      },
                      {
                        'aliasName': '',
                        'columnName': 'long',
                        'displayName': 'Long',
                        'filterEligible': true,
                        'joinEligible': false,
                        'name': 'long',
                        'table': 'sample',
                        'type': 'long',
                        'visibleIndex': 1
                      },
                      {
                        'aliasName': '',
                        'columnName': 'float',
                        'displayName': 'Float',
                        'filterEligible': true,
                        'joinEligible': false,
                        'name': 'float',
                        'table': 'sales',
                        'type': 'float',
                        'format': {
                          'precision': 2
                        },
                        'visibleIndex': 3
                      },
                      {
                        'aliasName': '',
                        'columnName': 'date',
                        'displayName': 'Date',
                        'filterEligible': true,
                        'joinEligible': false,
                        'name': 'date',
                        'table': 'sales',
                        'type': 'date',
                        'format': 'yyyy-MM-dd',
                        'visibleIndex': 5
                      },
                      {
                        'aliasName': '',
                        'columnName': 'integer',
                        'displayName': 'Integer',
                        'filterEligible': true,
                        'joinEligible': false,
                        'name': 'integer',
                        'table': 'sample',
                        'type': 'integer',
                        'visibleIndex': 2
                      },
                      {
                        'aliasName': '',
                        'columnName': 'double',
                        'displayName': 'Double',
                        'filterEligible': true,
                        'joinEligible': false,
                        'name': 'double',
                        'table': 'sales',
                        'type': 'double',
                        'format': {
                          'precision': 2
                        },
                        'visibleIndex': 4
                      }
                    ]
                  }
                ],
                'joins': []
              },
              'edit': false,
              'categoryId': subCategoryId,
              'query': 'SELECT SALES.string, SALES.long, SALES.float, SALES.date, SALES.integer, SALES.double FROM SALES',
              'executionType': 'publish'
            }
          ]
        }
      };

      if(action === 'update') {
        body = update;
      } else if(action === 'execute') {
        body = execute;
      } else {
        throw new Error('Invalid action: '+action);
      }
      return body;

    }

    getChartBody(customerCode, id, action, dataSetName, semanticId, userId, loginId, analysisName,
      analysisDesc, subCategoryId, currentTimeStamp, analysisType, subType, filtr = null) {

      let body;

      let update = {
        'contents': {
          'keys': [
            {
              'customerCode': customerCode,
              'module': 'ANALYZE',
              'id': id,
              'type': analysisType
            }
          ],
          'action': action,
          'analyze': [
            {
              'type': analysisType,
              'chartType': subType,
              'semanticId': semanticId,
              'metricName': dataSetName,
              'name': analysisName,
              'description': analysisDesc,
              'scheduled': null,
              'id': id,
              'dataSecurityKey': '',
              'module': 'ANALYZE',
              'metric': 'sample-elasticsearch',
              'customerCode': customerCode,
              'disabled': 'false',
              'checked': 'false',
              'esRepository': {
                'storageType': 'ES',
                'indexName': 'sample',
                'type': 'sample'
              },
              'artifacts': [
                {
                  'artifactName': 'sample',
                  'columns': [
                    {
                      'name': 'string',
                      'type': 'string',
                      'columnName': 'string.keyword',
                      'displayName': 'String',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'filterEligible': true,
                      'area': 'g',
                      'checked': true,
                      'areaIndex': 0
                    },
                    {
                      'name': 'long',
                      'type': 'long',
                      'columnName': 'long',
                      'displayName': 'Long',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true
                    },
                    {
                      'name': 'float',
                      'type': 'float',
                      'columnName': 'float',
                      'displayName': 'Float',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true
                    },
                    {
                      'name': 'date',
                      'type': 'date',
                      'columnName': 'date',
                      'displayName': 'Date',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true,
                      'area': 'x',
                      'checked': true,
                      'dateFormat': 'MMM d YYYY',
                      'areaIndex': 0
                    },
                    {
                      'name': 'integer',
                      'type': 'integer',
                      'columnName': 'integer',
                      'displayName': 'Integer',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true
                    },
                    {
                      'name': 'double',
                      'type': 'double',
                      'columnName': 'double',
                      'displayName': 'Double',
                      'aliasName': '',
                      'table': 'sales',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true,
                      'area': 'y',
                      'checked': true,
                      'aggregate': 'sum',
                      'comboType': 'column',
                      'areaIndex': 0
                    }
                  ]
                }
              ],
              'repository': {
                'storageType': 'DL',
                'objects': [

                ],
                '_number_of_elements': 0
              },
              'createdTimestamp': currentTimeStamp,
              'userId': userId,
              'userFullName': loginId,
              'sqlBuilder': {
                'booleanCriteria': 'AND',
                'filters': filtr ? filtr :[],
                'sorts': [
                  {
                    'order': 'asc',
                    'columnName': 'date',
                    'type': 'date'
                  },
                  {
                    'order': 'asc',
                    'columnName': 'string.keyword',
                    'type': 'string'
                  }
                ],
                'dataFields': [
                  {
                    'aggregate': 'sum',
                    'alias': '',
                    'checked': 'y',
                    'columnName': 'double',
                    'comboType': 'column',
                    'displayName': 'Double',
                    'table': 'sales',
                    'tableName': 'sales',
                    'name': 'double',
                    'type': 'double'
                  }
                ],
                'nodeFields': [
                  {
                    'alias': '',
                    'checked': 'x',
                    'columnName': 'date',
                    'displayName': 'Date',
                    'table': 'sample',
                    'tableName': 'sample',
                    'name': 'date',
                    'type': 'date',
                    'dateFormat': 'MMM d YYYY'
                  },
                  {
                    'alias': '',
                    'checked': 'g',
                    'columnName': 'string.keyword',
                    'displayName': 'String',
                    'table': 'sample',
                    'tableName': 'sample',
                    'name': 'string.keyword',
                    'type': 'string'
                  }
                ]
              },
              'isInverted': false,
              'isStockChart': false,
              'edit': false,
              'legend': {
                'align': 'right',
                'layout': 'vertical'
              },
              'categoryId': subCategoryId,
              'saved': true
            }
          ]
        }
      };

      let execute = {
        'contents': {
          'keys': [
            {
              'customerCode': customerCode,
              'module': 'ANALYZE',
              'id': id,
              'type': analysisType
            }
          ],
          'action': action,
          "executedBy": loginId,
          'page': 1,
          'pageSize': 10,
          'analyze': [
            {
              'type': analysisType,
              'chartType': subType,
              'semanticId': semanticId,
              'metricName': dataSetName,
              'name': analysisName,
              'description': analysisDesc,
              'scheduled': null,
              'id': id,
              'dataSecurityKey': '',
              'module': 'ANALYZE',
              'metric': 'sample-elasticsearch',
              'customerCode': customerCode,
              'disabled': 'false',
              'checked': 'false',
              'esRepository': {
                'storageType': 'ES',
                'indexName': 'sample',
                'type': 'sample'
              },
              'artifacts': [
                {
                  'artifactName': 'sample',
                  'columns': [
                    {
                      'name': 'string',
                      'type': 'string',
                      'columnName': 'string.keyword',
                      'displayName': 'String',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'filterEligible': true,
                      'area': 'g',
                      'checked': true,
                      'areaIndex': 0
                    },
                    {
                      'name': 'long',
                      'type': 'long',
                      'columnName': 'long',
                      'displayName': 'Long',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true
                    },
                    {
                      'name': 'float',
                      'type': 'float',
                      'columnName': 'float',
                      'displayName': 'Float',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true
                    },
                    {
                      'name': 'date',
                      'type': 'date',
                      'columnName': 'date',
                      'displayName': 'Date',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true,
                      'area': 'x',
                      'checked': true,
                      'dateFormat': 'MMM d YYYY',
                      'areaIndex': 0
                    },
                    {
                      'name': 'integer',
                      'type': 'integer',
                      'columnName': 'integer',
                      'displayName': 'Integer',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true
                    },
                    {
                      'name': 'double',
                      'type': 'double',
                      'columnName': 'double',
                      'displayName': 'Double',
                      'aliasName': '',
                      'table': 'sales',
                      'joinEligible': false,
                      'kpiEligible': true,
                      'filterEligible': true,
                      'area': 'y',
                      'checked': true,
                      'aggregate': 'sum',
                      'comboType': 'column',
                      'areaIndex': 0
                    }
                  ]
                }
              ],
              'repository': {
                'storageType': 'DL',
                'objects': [

                ],
                '_number_of_elements': 0
              },
              'createdTimestamp': currentTimeStamp,
              'userId': userId,
              'userFullName': loginId,
              'sqlBuilder': {
                'booleanCriteria': 'AND',
                'filters': filtr ? filtr :[],
                'sorts': [
                  {
                    'order': 'asc',
                    'columnName': 'date',
                    'type': 'date'
                  },
                  {
                    'order': 'asc',
                    'columnName': 'string.keyword',
                    'type': 'string'
                  }
                ],
                'dataFields': [
                  {
                    'aggregate': 'sum',
                    'alias': '',
                    'checked': 'y',
                    'columnName': 'double',
                    'comboType': 'column',
                    'displayName': 'Double',
                    'table': 'sales',
                    'tableName': 'sales',
                    'name': 'double',
                    'type': 'double'
                  }
                ],
                'nodeFields': [
                  {
                    'alias': '',
                    'checked': 'x',
                    'columnName': 'date',
                    'displayName': 'Date',
                    'table': 'sample',
                    'tableName': 'sample',
                    'name': 'date',
                    'type': 'date',
                    'dateFormat': 'MMM d YYYY'
                  },
                  {
                    'alias': '',
                    'checked': 'g',
                    'columnName': 'string.keyword',
                    'displayName': 'String',
                    'table': 'sample',
                    'tableName': 'sample',
                    'name': 'string.keyword',
                    'type': 'string'
                  }
                ]
              },
              'isInverted': false,
              'isStockChart': false,
              'edit': false,
              'legend': {
                'align': 'right',
                'layout': 'vertical'
              },
              'executionType': 'publish',
              'categoryId': subCategoryId,
              'saved': true
            }
          ]
        }
      };

      if(action === 'update') {
        body = update;
      } else if(action === 'execute') {
        body = execute;
      } else {
        throw new Error('Invalid action: '+action);
      }
      return body;
    }

    /**
     * @description Builds delete playload for analyze module
     * @param {String} customerCode
     * @param {String} id
     * @returns {Object}
     */
    getAnalyzeDeletePayload(customerCode, id){
      let deletePayload = {
        'contents': {
          'keys': [
            {
              'customerCode': customerCode,
              'module': 'ANALYZE',
              'id': id
            }
          ],
          'action': 'delete'
        }
      };
      return deletePayload;
    }

    getAnalysisCreatePayload(semanticId, analysisType, customerCode) {
      const createPayload = {
        'contents': {
          'keys': [{
            'customerCode': customerCode,
            'module': 'ANALYZE',
            'id': semanticId,
            'analysisType': analysisType
          }], 'action': 'create'
        }
      };
      return createPayload;
    }

}

module.exports = RequestModel;
