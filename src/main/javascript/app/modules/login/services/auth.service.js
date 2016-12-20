class AuthService {
  constructor($window) {
    this._$window = $window;

    this.authorized = false;
    this.memorizedState = null;
  }

  clear() {
    this.authorized = false;
    this.memorizedState = null;
  }

  go(fallback) {
    this.authorized = true;

    const targetState = this.memorizedState || fallback;
    const baseUrl = this._$window.location.origin;
    const appUrl = `${baseUrl}` + targetState;

    this._$window.location = appUrl;
  }
}

export function AuthServiceFactory($window) {
  'ngInject';
  return new AuthService($window);
}
