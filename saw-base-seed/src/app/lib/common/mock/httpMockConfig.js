import angularMocks from 'angular-mocks';
import flatten from 'lodash/flatten';
import values from 'lodash/values';

const req = require.context('app', true, /^(.*\.mock\.(js$))[^.]*$/igm);
const mocks = flatten(req.keys().map(key => {
  return values(req(key));
}));

class HttpMockConfig {
  constructor($httpBackend) {
    this.angularMocks = angularMocks;

    (mocks || []).forEach(item => {
      console.log(item); // eslint-disable-line
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
