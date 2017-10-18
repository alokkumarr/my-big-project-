import 'angular-mocks';
import * as flatten from 'lodash/flatten';
import * as values from 'lodash/values';

const req = require.context('api/', true, /^(.*\.mock\.(js$))[^.]*$/igm);
const mocks = flatten(req.keys().map(key => {
  return values(req(key));
}));

class HttpMock {
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

  return new HttpMock($httpBackend);
};
