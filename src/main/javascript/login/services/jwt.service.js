import get from 'lodash/get';

class JwtService {
  constructor($window, AppConfig) {
    this._$window = $window;
    this._AppConfig = AppConfig;
  }

  set(token) {
    this._$window.localStorage[this._AppConfig.login.jwtKey] = token;
  }

  get() {
    return this._$window.localStorage[this._AppConfig.login.jwtKey];
  }

  destroy() {
    this._$window.localStorage.removeItem(this._AppConfig.login.jwtKey);
  }

  /* Returs the parsed json object from the jwt token */
  getTokenObj() {
    const base64Url = this.get().split('.')[1];
    const base64 = base64Url.replace('-', '+').replace('_', '/');
    return angular.fromJson(this._$window.atob(base64));
  }

  /* Bootstraps request structure with necessary auth data */
  getRequestParams() {
    const token = this.getTokenObj();
    return {
      contents: {
        keys: {
          customerCode: 'ATT'
          // dataSecurityKey: get(token, 'ticket.dataSecurityKey')
        }
      }
    };
  }
}

export function JwtServiceFactory($window, AppConfig) {
  'ngInject';
  return new JwtService($window, AppConfig);
}
