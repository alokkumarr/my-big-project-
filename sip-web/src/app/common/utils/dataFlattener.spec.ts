import { flattenReportData, wrapFieldValues } from './dataFlattener';

describe('flattenReportData', () => {
  it('should remove keywords from reports', () => {
    const analysis = { artifacts: [{ columns: [] }] };
    const data = [{ 'string.keyword': 'abc' }];
    expect(flattenReportData(data, analysis)[0].string).toBeTruthy();
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
