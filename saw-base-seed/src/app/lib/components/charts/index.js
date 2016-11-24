import angular from 'angular';
import Highcharts from 'highcharts';

import {wrapChart} from './chartkit';
import {highchartsConfig} from './highcharts.config';
import {setSVGRenderer} from './chart-svg-renderer';
import {chartDateService} from './chart-date.service';
import {lineChart, barChart, snapshotBarChart, areaChart} from './charts';

export const ChartsModule = 'components.charts';

const origModule = angular.module;

angular.module = function (...args) {
  const module = origModule.apply(angular, args);

  module.chart = (name, factory) => {
    return module.directive(`${name}Chart`, wrapChart(`${name}Chart`, factory));
  };

  return module;
};

setSVGRenderer(Highcharts);

angular.module(ChartsModule, [])
        .constant('Highcharts', Highcharts)
        .config(highchartsConfig)
        .factory('chartDateService', chartDateService)
        .chart('line', lineChart)
        .chart('bar', barChart)
        .chart('area', areaChart)
        .chart('snapshotBar', snapshotBarChart);
