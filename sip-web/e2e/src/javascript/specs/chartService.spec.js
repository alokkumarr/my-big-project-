import {ChartService} from '../../../../src/app/modules/analyze/services/chart.service';
import {bubbleChartDataGrouped, columnChartData, columnChartGroupedData} from './chartData';
import {bubbleChartGrouped, columnChart, columnChartGrouped} from './sqlBuilder';

describe('ChartService', () => {

  let service;

  beforeEach(() => {
    service = new ChartService();
  });

  it('ChartService exists', () => {
    expect(service).to.be.an('object');
  });

  describe('parseData(data, sqlBuilder)', () => {

    it('parses bubbleChartGrouped Data', () => {
      const parsedData = service.parseData(bubbleChartDataGrouped, bubbleChartGrouped);
      expect(parsedData).to.be.an('array');
      expect(parsedData[0]).to.be.an('object');
      expect(parsedData[0]).to.have.a.property('AVAILABLE_ITEMS');
      expect(parsedData[0]).to.have.a.property('AVAILABLE_MB');
      expect(parsedData[0]).to.have.a.property('SOURCE_OS.keyword');
      expect(parsedData[0]).to.have.a.property('SOURCE_MANUFACTURER.keyword');
    });

    it('parses columnChart data', () => {
      const parsedData = service.parseData(columnChartData, columnChart);
      expect(parsedData).to.be.an('array');
      expect(parsedData[0]).to.be.an('object');
      expect(parsedData[0]).to.have.a.property('AVAILABLE_MB');
      expect(parsedData[0]).to.have.a.property('SOURCE_MANUFACTURER.keyword');
    });

    it('parses columnChartGrouped data', () => {
      const parsedData = service.parseData(columnChartGroupedData, columnChartGrouped);
      expect(parsedData).to.be.an('array');
      expect(parsedData[0]).to.be.an('object');
      expect(parsedData[0]).to.have.a.property('AVAILABLE_MB');
      expect(parsedData[0]).to.have.a.property('SOURCE_OS.keyword');
      expect(parsedData[0]).to.have.a.property('SOURCE_MANUFACTURER.keyword');
    });
  });
});
