import 'angular-mocks';
import flatten from 'lodash/flatten';
import values from 'lodash/values';

const req = require.context('app', true, /^(.*\.mock\.(js$))[^.]*$/igm);
const mocks = flatten(req.keys().map(key => {
  return values(req(key));
}));

class HttpMockConfig {
  constructor($httpBackend) {
    mocks.forEach(item => {
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

export default $httpBackend => {
  'ngInject';

  return new HttpMockConfig($httpBackend);
};
