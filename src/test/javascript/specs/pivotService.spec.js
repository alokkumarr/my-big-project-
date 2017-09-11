import * as forEach from 'lodash/forEach';
import * as find from 'lodash/find';
import {AnalyzeModule} from '../../../main/javascript/app/modules/analyze';
import {data} from './pivotData';
import {pivot as pivotSqlBuilder} from './sqlBuilder';
import {artifacts} from './artifacts';
const artifactColumns = artifacts[0].columns;

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

  describe('artifactColumns2PivotFields()(artifactColumns)', () => {
    it('Transforms artifact columns to pivotFields', () => {
      const pivotFields = PivotService.artifactColumns2PivotFields()(artifactColumns);
      const elem = pivotFields[0];
      expect(pivotFields).to.be.an('array');
      expect(elem).to.be.an('object');
      expect(elem).to.have.property('dataType');
      expect(elem).to.have.property('caption');
      expect(elem).to.have.property('dataField');
      const numberElem = find(pivotFields, field => field.dataType === 'number');
      expect(numberElem).to.have.property('summaryType');
      expect(numberElem).to.have.property('format');
    });
  });

  describe('takeOutKeywordFromArtifactColumns(artifactColumns)', () => {
    it('takes out the ".keyword" suffix form the solumnNames of columns of type string', () => {
      const result = PivotService.takeOutKeywordFromArtifactColumns(artifactColumns);
      const stringElem = find(result, field => field.type === 'string');
      expect(stringElem.columnName).to.not.include('.keyword');
    });
  });
});

function elemContainsFields(fields, elem) {
  forEach(fields, field => {
    expect(elem).to.have.a.property(field.columnName);
  });
}
