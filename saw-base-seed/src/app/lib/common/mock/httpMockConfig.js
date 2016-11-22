import angularMocks from 'angular-mocks';
import mockData from './mock';

class HttpMockConfig {
  constructor($httpBackend) {
    this.angularMocks = angularMocks;

    (mockData || []).forEach(item => {
      $httpBackend
        .whenRoute(item.method, item.url)
        .respond(item.response);
    });

    $httpBackend.whenGET(/(.+)/).passThrough();
    $httpBackend.whenPOST(/(.+)/).passThrough();
    $httpBackend.whenPUT(/(.+)/).passThrough();
    $httpBackend.whenDELETE(/(.+)/).passThrough();
  }
}

/** @ngInject */
export default $httpBackend => {
  return new HttpMockConfig($httpBackend);
};
