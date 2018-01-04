export class HeaderProgressService {
  constructor($rootScope) {
    this._$rootScope = $rootScope;
  }

  show() {
    this._$rootScope.showProgress = true;
  }

  hide() {
    this._$rootScope.showProgress = false;
  }

  toggle() {
    this._$rootScope.showProgress = !this._$rootScope.showProgress;
  }

  get() {
    return this._$rootScope.showProgress;
  }
}
