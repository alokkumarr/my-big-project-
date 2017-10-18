// import angular from 'angular';
import {AnalyzeModule} from '../../../main/javascript/app/modules/analyze';
import {bubbleChartDataGrouped, columnChartData, columnChartGroupedData} from './chartData';
import {bubbleChartGrouped, columnChart, columnChartGrouped} from './sqlBuilder';

describe('ChartService', () => {

  let ChartService;

  beforeEach(() => {
    angular.mock.module(AnalyzeModule);
    angular.mock.inject($injector => {
      ChartService = $injector.get('ChartService');
    });
  });

  it('ChartService exists', () => {
    expect(ChartService).to.be.an('object');
  });

  describe('parseData(data, sqlBuilder)', () => {

    it('parses bubbleChartGrouped Data', () => {
      const parsedData = ChartService.parseData(bubbleChartDataGrouped, bubbleChartGrouped);
      expect(parsedData).to.be.an('array');
      expect(parsedData[0]).to.be.an('object');
      expect(parsedData[0]).to.have.a.property('AVAILABLE_ITEMS');
      expect(parsedData[0]).to.have.a.property('AVAILABLE_MB');
      expect(parsedData[0]).to.have.a.property('SOURCE_OS.keyword');
      expect(parsedData[0]).to.have.a.property('SOURCE_MANUFACTURER.keyword');
    });

    it('parses columnChart data', () => {
      const parsedData = ChartService.parseData(columnChartData, columnChart);
      expect(parsedData).to.be.an('array');
      expect(parsedData[0]).to.be.an('object');
      expect(parsedData[0]).to.have.a.property('AVAILABLE_MB');
      expect(parsedData[0]).to.have.a.property('SOURCE_MANUFACTURER.keyword');
    });

    it('parses columnChartGrouped data', () => {
      const parsedData = ChartService.parseData(columnChartGroupedData, columnChartGrouped);
      expect(parsedData).to.be.an('array');
      expect(parsedData[0]).to.be.an('object');
      expect(parsedData[0]).to.have.a.property('AVAILABLE_MB');
      expect(parsedData[0]).to.have.a.property('SOURCE_OS.keyword');
      expect(parsedData[0]).to.have.a.property('SOURCE_MANUFACTURER.keyword');
    });
  });
});
