import { flattenReportData } from './dataFlattener';

describe('flattenReportData', () => {
  it('should remove keywords from reports', () => {
    const analysis = { artifacts: [{ columns: [] }] };
    const data = [{ 'string.keyword': 'abc' }];
    expect(flattenReportData(data, analysis)[0].string).toBeTruthy();
  });
});
