import angular from 'angular';
import HttpMock from './httpMock';

export const MockModule = 'Mock';

angular
  .module(MockModule, ['ngMockE2E'])
  .run(HttpMock);
