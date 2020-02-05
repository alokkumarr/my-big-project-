import {
  flattenReportData,
  wrapFieldValues,
  alterDateInData
} from './dataFlattener';

describe('flattenReportData', () => {
  it('should remove keywords from reports', () => {
    const analysis = {
      artifacts: [
        {
          columns: []
        }
      ],
      sipQuery: {
        artifacts: [
          {
            columns: []
          }
        ]
      }
    };
    const data = [{ 'string.keyword': 'abc' }];
    expect(flattenReportData(data, analysis)[0].string).toBeTruthy();
  });
});

describe('alterDateData', () => {
  it('should format dates without adding a day', () => {
    const sipQuery = {
      artifacts: [
        {
          artifactsName: 'tmobile_cell_sites',
          fields: [
            {
              columnName: 'timestamp',
              type: 'date',
              aggregate: 'sum'
            }
          ]
        }
      ]
    };
    const data = [{ timestamp: '01/16/2019 20:31:23' }];
    expect(alterDateInData(data, sipQuery)).toEqual([
      { timestamp: '01/16/2019 20:31:23' }
    ]);
  });

  it('should handle alias keys correctly for DL reports', () => {
    const sipQuery = {
      artifacts: [
        {
          artifactsName: 'tmobile_cell_sites',
          fields: [
            {
              columnName: 'timestamp',
              type: 'date',
              alias: 'MyAlias',
              aggregate: 'sum'
            }
          ]
        }
      ]
    };
    const data = [{ MyAlias: '01/16/2019 20:31:23' }];
    expect(alterDateInData(data, sipQuery, 'report')).toEqual([
      { MyAlias: '01/16/2019 20:31:23' }
    ]);
  });
});

describe('wrapFieldValues', () => {
  it('should wrap field values in quotes', () => {
    const data = [{ a: 1 }];
    expect(wrapFieldValues(data)[0].a).toEqual('"1"');
  });

  it('should not affect null values', () => {
    const data = [{ a: 1, b: null }];
    expect(wrapFieldValues(data)[0].a).toEqual('"1"');
    expect(wrapFieldValues(data)[0].b).toBeNull();
  });
});
