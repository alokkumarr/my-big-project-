/* This is an upgrade adapter for using angularjs 1.x services
in angular 2+ code. Follow https://angular.io/guide/upgrade#making-angularjs-dependencies-injectable-to-angular
for more details. Add all upgradable services in analyze module to this
file. */

import { AnalyzeService } from './analyze.service';
import { ChartService } from './chart.service';
import { SortService } from './sort.service';
import { FilterService } from './filter.service';

/* Analyze service adapter */
export function analyzeServiceFactory(i: any) {
  return i.get('AnalyzeService');
}

export const analyzeServiceProvider = {
  provide: AnalyzeService,
  useFactory: analyzeServiceFactory,
  deps: ['$injector']
};

/* Chart service adapter */
export function chartServiceFactory(i: any) {
  return i.get('ChartService');
}

export const chartServiceProvider = {
  provide: ChartService,
  useFactory: chartServiceFactory,
  deps: ['$injector']
}

/* Sort service adapter */
export function sortServiceFactory(i: any) {
  return i.get('SortService');
}

export const sortServiceProvider = {
  provide: SortService,
  useFactory: sortServiceFactory,
  deps: ['$injector']
}

/* Filter service adapter */
export function filterServiceFactory(i: any) {
  return i.get('FilterService');
}

export const filterServiceProvider = {
  provide: FilterService,
  useFactory: filterServiceFactory,
  deps: ['$injector']
}
