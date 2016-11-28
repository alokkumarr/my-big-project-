import angular from 'angular';
import lodash from 'lodash';
import sd from 'lodash/get';
import Highcharts from 'highcharts';

import {wrapChart} from './chartkit';
import {highchartsConfig} from './highcharts.config';
import {setSVGRenderer} from './chart-svg-renderer';
import {lineChart, barChart, snapshotBarChart, areaChart} from './charts';

export const ChartsModule = 'components.charts';

const origModule = angular.module;

angular.module = function (...args) {
  const module = origModule.apply(angular, args);

  module.chart = (name, factory) => {
    return module.directive(`${name}Chart`, wrapChart(`${name}Chart`, factory));
  };
  console.log('something');
  console.log('something');
  return module;
};

setSVGRenderer(Highcharts);

angular.module(ChartsModule, [])
        .constant('Highcharts', Highcharts)
        .config(highchartsConfig)
        .chart('line', lineChart)
        .chart('bar', barChart)
        .chart('area', areaChart)
        .chart('snapshotBar', snapshotBarChart);
