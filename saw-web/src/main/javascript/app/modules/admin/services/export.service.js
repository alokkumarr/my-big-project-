import * as forEach from 'lodash/forEach';
import * as floor from 'lodash/floor';
import * as set from 'lodash/set';
import * as isEmpty from 'lodash/isEmpty';
import * as has from 'lodash/has';
import * as fpSortBy from 'lodash/fp/sortBy';
import * as fpGet from 'lodash/fp/get';
import * as find from 'lodash/find';
import * as filter from 'lodash/filter';
import * as flatMap from 'lodash/flatMap';
import * as cloneDeep from 'lodash/cloneDeep';
const MODULE_NAME = 'ANALYZE';
export class ExportService {
  'ngInject';
  constructor($http, $q, AppConfig, JwtService, toastMessage, $translate) {
    'ngInject';
    this._$http = $http;
    this._$q = $q;
    this._JwtService = JwtService;
    this.loginUrl = AppConfig.login.url;
    this.apiUrl = AppConfig.api.url;

  }
  getRequestParams(params = []) {
    const reqParams = this._JwtService.getRequestParams();

    set(reqParams, 'contents.keys.[0].module', MODULE_NAME);
    forEach(params, tuple => {
      set(reqParams, tuple[0], tuple[1]);
    });
    return reqParams;
  }
  
  getMetricList() {
    const params = this.getRequestParams([
      ['contents.action', 'search'],
      ['contents.select', 'headers'],
      ['contents.context', 'Semantic']
    ]);
    return this._$http.post(`${this.apiUrl}/md`, params).then(fpGet(`data.contents.[0].${MODULE_NAME}`));
  }

  getAnalysisByMetricIds(object){
    return this._$http.post(`${this.apiUrl}/analysis`, object);
  }
}
