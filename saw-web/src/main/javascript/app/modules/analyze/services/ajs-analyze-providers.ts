/* This is an upgrade adapter for using angularjs 1.x services
in angular 2+ code. Follow https://angular.io/guide/upgrade#making-angularjs-dependencies-injectable-to-angular
for more details. Add all upgradable services in analyze module to this
file. */

import { AnalyzeService } from './analyze.service';

export function analyzeServiceFactory(i: any) {
  return i.get('AnalyzeService');
}

export const analyzeServiceProvider = {
  provide: AnalyzeService,
  useFactory: analyzeServiceFactory,
  deps: ['$injector']
};
