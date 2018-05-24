import * as forEach from 'lodash/forEach';
import * as set from 'lodash/set';
import * as fpGet from 'lodash/fp/get';
const MODULE_NAME = 'ANALYZE';
export class ImportService {
  constructor($http, $q, AppConfig, JwtService) {
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
  getAnalysesFor(subCategoryId) {
    const payload = this.getRequestParams([
      ['contents.action', 'search'],
      ['contents.keys.[0].categoryId', subCategoryId]
    ]);
    return this._$http.post(`${this.apiUrl}/analysis`, payload);
  }
  createAnalysis(metricId, type) {
    const params = this.getRequestParams([
      ['contents.action', 'create'],
      ['contents.keys.[0].id', metricId],
      ['contents.keys.[0].analysisType', type]
    ]);
    return this._$http.post(`${this.apiUrl}/analysis`, params).then(fpGet('data.contents.analyze.[0]'));
  }
  updateAnalysis(model) {
    const payload = this.getRequestParams([
      ['contents.action', 'update'],
      ['contents.keys.[0].id', model.id],
      ['contents.keys.[0].type', model.type],
      ['contents.analyze', [model]]
    ]);
    return this._$http.post(`${this.apiUrl}/analysis`, payload).then(fpGet(`data.contents.analyze.[0]`));
  }
}
