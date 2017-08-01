import forEach from 'lodash/forEach';
import {AnalyzeModule} from '../../../main/javascript/app/modules/analyze';
import {data} from './pivotData';
import {pivot as pivotSqlBuilder} from './sqlBuilder';

describe('PivotService', () => {

  let PivotService;

  beforeEach(() => {
    angular.mock.module(AnalyzeModule);
    angular.mock.inject($injector => {
      PivotService = $injector.get('PivotService');
    });
  });

  it('PivotService exists', () => {
    expect(PivotService).to.be.an('object');
  });

  describe('parseData(data, sqlBuilder)', () => {
    it('parses bubbleChartGrouped Data', () => {
      const parsedData = PivotService.parseData(data, pivotSqlBuilder);
      const elem = parsedData[0];
      expect(parsedData).to.be.an('array');
      expect(elem).be.an('object');
      elemContainsFields(pivotSqlBuilder.rowFields, elem);
      elemContainsFields(pivotSqlBuilder.columnFields, elem);
      elemContainsFields(pivotSqlBuilder.dataFields, elem);
    });
  });
});

function elemContainsFields(fields, elem) {
  forEach(fields, field => {
    expect(elem).to.have.a.property(field.columnName);
  });
}
