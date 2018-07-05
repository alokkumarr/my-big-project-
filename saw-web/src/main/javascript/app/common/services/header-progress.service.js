export class HeaderProgressService {
  constructor($rootScope) {
    this._$rootScope = $rootScope;
    this._$rootScope.progressCounter = 0;
    this._$rootScope.showProgress = this.get();
  }

  show() {
    this._$rootScope.progressCounter++;
    this._$rootScope.showProgress = this.get();
  }

  hide() {
    this._$rootScope.progressCounter--;
    this._$rootScope.showProgress = this.get();
  }

  toggle() {
    this._$rootScope.showProgress = !this._$rootScope.showProgress;
    if (this.showProgress) {
      this._$rootScope.progressCounter--;
    } else {
      this._$rootScope.progressCounter++;
    }
    this._$rootScope.showProgress = this.get();
  }

  get() {
    return this._$rootScope.progressCounter > 0;
  }
}
