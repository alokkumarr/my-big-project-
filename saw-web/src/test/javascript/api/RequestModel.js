'use strict';
class RequestModel {
    //Payload to create,update,execute analysis
    getPayloadPivotChart(customerCode, chartID, action, dataSetName, semanticId, userId, loginId, analysisName,
                analysisDesc, subCategoryId, currentTimeStamp, analysisType, subType) {

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
                    'filters': [

                    ],
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
      analysisDesc, subCategoryId, currentTimeStamp, analysisType, subType) {
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
                'filters': [
                  
                ],
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
              'filters': [
                
              ],
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
            'executionType': 'preview'
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
      analysisDesc, subCategoryId, currentTimeStamp, analysisType, subType) {
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
              'createdTimestamp': customerCode,
              'userId': userId,
              'userFullName':loginId,
              'sqlBuilder': {
                'booleanCriteria': 'AND',
                'filters': [
                  
                ],
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
              'executionType': 'preview',
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
                'filters': [
                  
                ],
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
              'executionType': 'preview',
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
      analysisDesc, subCategoryId, currentTimeStamp, analysisType, subType) {
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
              'metric': 'sample-spark',
              'customerCode': customerCode,
              'disabled': 'false',
              'checked': 'false',
              'artifacts': [
                {
                  'artifactName': 'SALES',
                  'columns': [
                    {
                      'name': 'string',
                      'type': 'string',
                      'columnName': 'string',
                      'displayName': 'String',
                      'aliasName': '',
                      'table': 'sales',
                      'joinEligible': true,
                      'filterEligible': true,
                      'checked': true,
                      'visibleIndex': 0
                    },
                    {
                      'name': 'long',
                      'type': 'long',
                      'columnName': 'long',
                      'displayName': 'Long',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'filterEligible': true,
                      'checked': true,
                      'visibleIndex': 1
                    },
                    {
                      'name': 'float',
                      'type': 'float',
                      'columnName': 'float',
                      'displayName': 'Float',
                      'aliasName': '',
                      'table': 'sales',
                      'joinEligible': false,
                      'filterEligible': true,
                      'checked': true,
                      'format': {
                        'precision': 2
                      },
                      'visibleIndex': 2
                    },
                    {
                      'name': 'date',
                      'type': 'date',
                      'columnName': 'date',
                      'displayName': 'Date',
                      'aliasName': '',
                      'table': 'sales',
                      'joinEligible': false,
                      'filterEligible': true,
                      'checked': true,
                      'format': 'yyyy-MM-dd',
                      'visibleIndex': 3
                    },
                    {
                      'name': 'integer',
                      'type': 'integer',
                      'columnName': 'integer',
                      'displayName': 'Integer',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'filterEligible': true,
                      'checked': true,
                      'visibleIndex': 4
                    },
                    {
                      'name': 'double',
                      'type': 'double',
                      'columnName': 'double',
                      'displayName': 'Double',
                      'aliasName': '',
                      'table': 'sales',
                      'joinEligible': false,
                      'filterEligible': true,
                      'checked': true,
                      'format': {
                        'precision': 2
                      },
                      'visibleIndex': 5
                    }
                  ],
                  'artifactPosition': [
                    8,
                    20
                  ]
                },
                {
                  'artifactName': 'PRODUCT',
                  'columns': [
                    {
                      'name': 'string_2',
                      'type': 'string',
                      'columnName': 'string_2',
                      'displayName': 'String_2',
                      'aliasName': '',
                      'table': 'product',
                      'joinEligible': true,
                      'filterEligible': true,
                      'checked': false,
                      'visibleIndex': 5
                    },
                    {
                      'name': 'long_2',
                      'type': 'long',
                      'columnName': 'long_2',
                      'displayName': 'Long_2',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'filterEligible': true,
                      'checked': false,
                      'visibleIndex': 1
                    },
                    {
                      'name': 'float_2',
                      'type': 'float',
                      'columnName': 'float_2',
                      'displayName': 'Float_2',
                      'aliasName': '',
                      'table': 'product',
                      'joinEligible': false,
                      'filterEligible': true,
                      'checked': false,
                      'format': {
                        'precision': 2
                      },
                      'visibleIndex': 2
                    },
                    {
                      'name': 'date_2',
                      'type': 'date',
                      'columnName': 'date_2',
                      'displayName': 'Date_2',
                      'aliasName': '',
                      'table': 'product',
                      'joinEligible': false,
                      'filterEligible': true,
                      'checked': false,
                      'format': 'yyyy-MM-dd',
                      'visibleIndex': 4
                    },
                    {
                      'name': 'integer_2',
                      'type': 'integer',
                      'columnName': 'integer_2',
                      'displayName': 'Integer_2',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'filterEligible': true,
                      'checked': false,
                      'visibleIndex': 6
                    },
                    {
                      'name': 'double_2',
                      'type': 'double',
                      'columnName': 'double_2',
                      'displayName': 'Double_2',
                      'aliasName': '',
                      'table': 'sales',
                      'joinEligible': false,
                      'filterEligible': true,
                      'checked': false,
                      'format': {
                        'precision': 2
                      },
                      'visibleIndex': 7
                    }
                  ],
                  'artifactPosition': [
                    420,
                    20
                  ]
                }
              ],
              'repository': {
                'storageType': 'DL',
                'objects': [
                  {
                    'EnrichedDataObjectId': 'SALES::json::1530537450786',
                    'displayName': 'Sample Metric',
                    'EnrichedDataObjectName': 'SALES',
                    'description': 'Sample Metric',
                    'lastUpdatedTimestamp': 'undefined'
                  },
                  {
                    'EnrichedDataObjectId': 'PRODUCT::json::1530537460684',
                    'displayName': 'Product',
                    'EnrichedDataObjectName': 'PRODUCT',
                    'description': 'Product',
                    'lastUpdatedTimestamp': 'undefined'
                  }
                ],
                '_number_of_elements': 2
              },
              'createdTimestamp': currentTimeStamp,
              'userId': userId,
              'userFullName': loginId,
              'sqlBuilder': {
                'booleanCriteria': 'AND',
                'filters': [
                  
                ],
                'orderByColumns': [
                  
                ],
                'joins': [
                  
                ]
              },
              'edit': false,
              'executionType': 'preview',
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
              'metric': 'sample-spark',
              'customerCode': customerCode,
              'disabled': 'false',
              'checked': 'false',
              'artifacts': [
                {
                  'artifactName': 'SALES',
                  'columns': [
                    {
                      'name': 'string',
                      'type': 'string',
                      'columnName': 'string',
                      'displayName': 'String',
                      'aliasName': '',
                      'table': 'sales',
                      'joinEligible': true,
                      'filterEligible': true,
                      'checked': true,
                      'visibleIndex': 0
                    },
                    {
                      'name': 'long',
                      'type': 'long',
                      'columnName': 'long',
                      'displayName': 'Long',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'filterEligible': true,
                      'checked': true,
                      'visibleIndex': 1
                    },
                    {
                      'name': 'float',
                      'type': 'float',
                      'columnName': 'float',
                      'displayName': 'Float',
                      'aliasName': '',
                      'table': 'sales',
                      'joinEligible': false,
                      'filterEligible': true,
                      'checked': true,
                      'format': {
                        'precision': 2
                      },
                      'visibleIndex': 2
                    },
                    {
                      'name': 'date',
                      'type': 'date',
                      'columnName': 'date',
                      'displayName': 'Date',
                      'aliasName': '',
                      'table': 'sales',
                      'joinEligible': false,
                      'filterEligible': true,
                      'checked': true,
                      'format': 'yyyy-MM-dd',
                      'visibleIndex': 3
                    },
                    {
                      'name': 'integer',
                      'type': 'integer',
                      'columnName': 'integer',
                      'displayName': 'Integer',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'filterEligible': true,
                      'checked': true,
                      'visibleIndex': 4
                    },
                    {
                      'name': 'double',
                      'type': 'double',
                      'columnName': 'double',
                      'displayName': 'Double',
                      'aliasName': '',
                      'table': 'sales',
                      'joinEligible': false,
                      'filterEligible': true,
                      'checked': true,
                      'format': {
                        'precision': 2
                      },
                      'visibleIndex': 5
                    }
                  ],
                  'artifactPosition': [
                    8,
                    20
                  ]
                },
                {
                  'artifactName': 'PRODUCT',
                  'columns': [
                    {
                      'name': 'string_2',
                      'type': 'string',
                      'columnName': 'string_2',
                      'displayName': 'String_2',
                      'aliasName': '',
                      'table': 'product',
                      'joinEligible': true,
                      'filterEligible': true,
                      'checked': false,
                      'visibleIndex': 5
                    },
                    {
                      'name': 'long_2',
                      'type': 'long',
                      'columnName': 'long_2',
                      'displayName': 'Long_2',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'filterEligible': true,
                      'checked': false,
                      'visibleIndex': 1
                    },
                    {
                      'name': 'float_2',
                      'type': 'float',
                      'columnName': 'float_2',
                      'displayName': 'Float_2',
                      'aliasName': '',
                      'table': 'product',
                      'joinEligible': false,
                      'filterEligible': true,
                      'checked': false,
                      'format': {
                        'precision': 2
                      },
                      'visibleIndex': 2
                    },
                    {
                      'name': 'date_2',
                      'type': 'date',
                      'columnName': 'date_2',
                      'displayName': 'Date_2',
                      'aliasName': '',
                      'table': 'product',
                      'joinEligible': false,
                      'filterEligible': true,
                      'checked': false,
                      'format': 'yyyy-MM-dd',
                      'visibleIndex': 4
                    },
                    {
                      'name': 'integer_2',
                      'type': 'integer',
                      'columnName': 'integer_2',
                      'displayName': 'Integer_2',
                      'aliasName': '',
                      'table': 'sample',
                      'joinEligible': false,
                      'filterEligible': true,
                      'checked': false,
                      'visibleIndex': 6
                    },
                    {
                      'name': 'double_2',
                      'type': 'double',
                      'columnName': 'double_2',
                      'displayName': 'Double_2',
                      'aliasName': '',
                      'table': 'sales',
                      'joinEligible': false,
                      'filterEligible': true,
                      'checked': false,
                      'format': {
                        'precision': 2
                      },
                      'visibleIndex': 7
                    }
                  ],
                  'artifactPosition': [
                    420,
                    20
                  ]
                }
              ],
              'repository': {
                'storageType': 'DL',
                'objects': [
                  {
                    'EnrichedDataObjectId': 'SALES::json::1530537450786',
                    'displayName': 'Sample Metric',
                    'EnrichedDataObjectName': 'SALES',
                    'description': 'Sample Metric',
                    'lastUpdatedTimestamp': 'undefined'
                  },
                  {
                    'EnrichedDataObjectId': 'PRODUCT::json::1530537460684',
                    'displayName': 'Product',
                    'EnrichedDataObjectName': 'PRODUCT',
                    'description': 'Product',
                    'lastUpdatedTimestamp': 'undefined'
                  }
                ],
                '_number_of_elements': 2
              },
              'createdTimestamp': currentTimeStamp,
              'userId': userId,
              'userFullName': loginId,
              'sqlBuilder': {
                'booleanCriteria': 'AND',
                'filters': [
                  
                ],
                'orderByColumns': [
                  
                ],
                'joins': [
                  
                ]
              },
              'edit': false,
              'executionType': 'regularExecution',
              'categoryId': subCategoryId,
              'saved': true,
              'query': 'SELECT SALES.string, SALES.long, SALES.float, SALES.date, SALES.integer, SALES.double FROM SALES',
              'outputFile': {
                'outputFormat': 'json',
                'outputFileName': 'test.json'
              }
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
      analysisDesc, subCategoryId, currentTimeStamp, analysisType, subType) {

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
                'filters': [
                  
                ],
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
              'executionType': 'preview',
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
                'filters': [
                  
                ],
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
              'executionType': 'preview',
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
