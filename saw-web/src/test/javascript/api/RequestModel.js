'use strict';
class RequsetModel {
    //Payload to create,update,execute analysis
    getPayload(customerCode, chartID, action, dataSetName, semanticId, userId, loginId, analysisName,
                analysisDesc, subCategoryId, currentTimeStamp, type) {

        let chartBody = {
            'contents': {
              'keys': [
                {
                  'customerCode': customerCode,
                  'module': 'ANALYZE',
                  'id': chartID,
                  'type': 'chart'
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
                  'chartType': type,
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

}

module.exports = RequsetModel;