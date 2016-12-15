import angular from 'angular';
import HttpMockConfig from './httpMockConfig';

export const MockModule = 'Mock';

angular
  .module(MockModule, ['ngMockE2E'])
  .run(HttpMockConfig);
