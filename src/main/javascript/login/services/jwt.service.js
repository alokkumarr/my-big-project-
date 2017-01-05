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
}

export function JwtServiceFactory($window, AppConfig) {
  'ngInject';
  return new JwtService($window, AppConfig);
}
