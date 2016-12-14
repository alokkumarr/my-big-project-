/**
 * Created by ssom0002 on 12/6/2016.
 */
import AppConstants from './app.constants';
export class JwtService {
  constructor($window) {
    'ngInject';
    this._$window = $window;
  }
  save(token) {
    this._$window.localStorage[AppConstants.jwtKey] = angular.toJson(token);
  }
  get() {
    return this._$window.localStorage[AppConstants.jwtKey];
  }
  destroy() {
    this._$window.localStorage.removeItem(AppConstants.jwtKey);
  }
}
