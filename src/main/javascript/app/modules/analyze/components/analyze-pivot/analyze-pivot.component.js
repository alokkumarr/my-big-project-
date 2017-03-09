import template from './analyze-pivot.component.html';
import style from './analyze-pivot.component.scss';

export const AnalyzePivotComponent = {
  template,
  styles: [style],
  controller: class AnalyzePivotController {
    constructor() {
      this.pivotGridOptions1 = {
        allowSortingBySummary: false,
        allowSorting: false,
        allowFiltering: true,
        allowExpandAll: true,
        fieldChooser: {
          enabled: false
        },
        export: {
          enabled: true,
          fileName: 'Sales'
        },
        dataSource: {
          fields: [{
            caption: 'Region',
            width: 120,
            dataField: 'region',
            area: 'row'
          }, {
            caption: 'City',
            dataField: 'city',
            width: 150,
            area: 'row',
            selector(data) {
              return `${data.city} (${data.country})`;
            }
          }, {
            dataField: 'date',
            dataType: 'date',
            area: 'column'
          }, {
            caption: 'Sales',
            dataField: 'amount',
            dataType: 'number',
            summaryType: 'sum',
            format: 'currency',
            area: 'data'
          }],
          store: getData()
        }
      };
    }
  }
};

function getData() {
  return [{
    'id': 1,
    'region': 'North America',
    'country': 'USA',
    'city': 'New York',
    'amount': 1740,
    'date': '2013/01/06'
  }, {
    'id': 2,
    'region': 'North America',
    'country': 'USA',
    'city': 'Los Angeles',
    'amount': 850,
    'date': '2013/01/13'
  }, {
    'id': 3,
    'region': 'North America',
    'country': 'USA',
    'city': 'Denver',
    'amount': 2235,
    'date': '2013/01/07'
  }, {
    'id': 4,
    'region': 'North America',
    'country': 'CAN',
    'city': 'Vancouver',
    'amount': 1965,
    'date': '2013/01/03'
  }, {
    'id': 5,
    'region': 'North America',
    'country': 'CAN',
    'city': 'Edmonton',
    'amount': 880,
    'date': '2013/01/10'
  }, {
    'id': 6,
    'region': 'South America',
    'country': 'BRA',
    'city': 'Rio de Janeiro',
    'amount': 5260,
    'date': '2013/01/17'
  }, {
    'id': 7,
    'region': 'South America',
    'country': 'ARG',
    'city': 'Buenos Aires',
    'amount': 2790,
    'date': '2013/01/21'
  }, {
    'id': 8,
    'region': 'South America',
    'country': 'PRY',
    'city': 'Asuncion',
    'amount': 3140,
    'date': '2013/01/01'
  }, {
    'id': 9,
    'region': 'Europe',
    'country': 'GBR',
    'city': 'London',
    'amount': 6175,
    'date': '2013/01/24'
  }, {
    'id': 10,
    'region': 'Europe',
    'country': 'DEU',
    'city': 'Berlin',
    'amount': 4575,
    'date': '2013/01/11'
  }, {
    'id': 11,
    'region': 'Europe',
    'country': 'ESP',
    'city': 'Madrid',
    'amount': 3680,
    'date': '2013/01/12'
  }, {
    'id': 12,
    'region': 'Europe',
    'country': 'RUS',
    'city': 'Moscow',
    'amount': 2260,
    'date': '2013/01/01'
  }, {
    'id': 13,
    'region': 'Asia',
    'country': 'CHN',
    'city': 'Beijing',
    'amount': 2910,
    'date': '2013/01/26'
  }, {
    'id': 14,
    'region': 'Asia',
    'country': 'JPN',
    'city': 'Tokio',
    'amount': 8400,
    'date': '2013/01/05'
  }, {
    'id': 15,
    'region': 'Asia',
    'country': 'KOR',
    'city': 'Seoul',
    'amount': 1325,
    'date': '2013/01/14'
  }, {
    'id': 16,
    'region': 'Australia',
    'country': 'AUS',
    'city': 'Sydney',
    'amount': 3920,
    'date': '2013/01/05'
  }, {
    'id': 17,
    'region': 'Australia',
    'country': 'AUS',
    'city': 'Melbourne',
    'amount': 2220,
    'date': '2013/01/15'
  }, {
    'id': 18,
    'region': 'Africa',
    'country': 'ZAF',
    'city': 'Pretoria',
    'amount': 940,
    'date': '2013/01/01'
  }, {
    'id': 19,
    'region': 'Africa',
    'country': 'EGY',
    'city': 'Cairo',
    'amount': 1630,
    'date': '2013/01/10'
  }, {
    'id': 20,
    'region': 'North America',
    'country': 'CAN',
    'city': 'Edmonton',
    'amount': 2910,
    'date': '2013/01/23'
  }, {
    'id': 21,
    'region': 'North America',
    'country': 'USA',
    'city': 'Los Angeles',
    'amount': 2600,
    'date': '2013/01/14'
  }, {
    'id': 22,
    'region': 'Europe',
    'country': 'ESP',
    'city': 'Madrid',
    'amount': 4340,
    'date': '2013/01/26'
  }, {
    'id': 23,
    'region': 'Europe',
    'country': 'GBR',
    'city': 'London',
    'amount': 6650,
    'date': '2013/01/24'
  }, {
    'id': 24,
    'region': 'North America',
    'country': 'CAN',
    'city': 'Edmonton',
    'amount': 490,
    'date': '2013/01/22'
  }, {
    'id': 25,
    'region': 'North America',
    'country': 'USA',
    'city': 'New York',
    'amount': 3390,
    'date': '2013/01/25'
  }, {
    'id': 26,
    'region': 'North America',
    'country': 'USA',
    'city': 'New York',
    'amount': 5160,
    'date': '2013/02/20'
  }, {
    'id': 27,
    'region': 'North America',
    'country': 'USA',
    'city': 'Los Angeles',
    'amount': 5750,
    'date': '2013/02/12'
  }, {
    'id': 28,
    'region': 'North America',
    'country': 'USA',
    'city': 'Denver',
    'amount': 2805,
    'date': '2013/02/13'
  }, {
    'id': 29,
    'region': 'North America',
    'country': 'CAN',
    'city': 'Vancouver',
    'amount': 2505,
    'date': '2013/02/09'
  }, {
    'id': 30,
    'region': 'North America',
    'country': 'CAN',
    'city': 'Edmonton',
    'amount': 930,
    'date': '2013/02/04'
  }, {
    'id': 31,
    'region': 'South America',
    'country': 'BRA',
    'city': 'Rio de Janeiro',
    'amount': 1240,
    'date': '2013/02/03'
  }, {
    'id': 32,
    'region': 'South America',
    'country': 'ARG',
    'city': 'Buenos Aires',
    'amount': 315,
    'date': '2013/02/04'
  }, {
    'id': 33,
    'region': 'South America',
    'country': 'PRY',
    'city': 'Asuncion',
    'amount': 2870,
    'date': '2013/02/18'
  }, {
    'id': 34,
    'region': 'Europe',
    'country': 'GBR',
    'city': 'London',
    'amount': 5150,
    'date': '2013/02/18'
  }, {
    'id': 35,
    'region': 'Europe',
    'country': 'DEU',
    'city': 'Berlin',
    'amount': 2725,
    'date': '2013/02/20'
  }, {
    'id': 36,
    'region': 'Europe',
    'country': 'ESP',
    'city': 'Madrid',
    'amount': 2840,
    'date': '2013/02/04'
  }, {
    'id': 37,
    'region': 'Europe',
    'country': 'RUS',
    'city': 'Moscow',
    'amount': 5840,
    'date': '2013/02/13'
  }, {
    'id': 38,
    'region': 'Asia',
    'country': 'CHN',
    'city': 'Beijing',
    'amount': 6750,
    'date': '2013/02/11'
  }, {
    'id': 39,
    'region': 'Asia',
    'country': 'JPN',
    'city': 'Tokio',
    'amount': 1200,
    'date': '2013/02/03'
  }, {
    'id': 40,
    'region': 'Asia',
    'country': 'KOR',
    'city': 'Seoul',
    'amount': 4550,
    'date': '2013/02/08'
  }, {
    'id': 41,
    'region': 'Australia',
    'country': 'AUS',
    'city': 'Sydney',
    'amount': 6040,
    'date': '2013/02/17'
  }];
}
