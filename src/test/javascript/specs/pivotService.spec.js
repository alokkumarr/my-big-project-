import forEach from 'lodash/forEach';
import find from 'lodash/find';
import {AnalyzeModule} from '../../../main/javascript/app/modules/analyze';
import {data} from './pivotData';
import {pivot as pivotSqlBuilder} from './sqlBuilder';
import {artifacts} from './artifacts';
import {NUMBER_TYPES} from '../../../main/javascript/app/modules/analyze/consts';
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
      console.log('fields: ', pivotFields);
      expect(pivotFields).to.be.an('array');
      expect(elem).to.be.an('object');
      expect(elem).to.have.property('dataType');
      expect(elem).to.have.property('caption');
      expect(elem).to.have.property('dataField');
      const numberElem = find(pivotFields, field => NUMBER_TYPES.includes(field.datatType));
      if (numberElem) {
        expect(numberElem).toBe.have.property('summaryType');
        expect(numberElem).toBe.have.property('format');
      }

    });
  });

});

function elemContainsFields(fields, elem) {
  forEach(fields, field => {
    expect(elem).to.have.a.property(field.columnName);
  });
}
