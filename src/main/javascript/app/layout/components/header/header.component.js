import get from 'lodash/get';
import template from './header.component.html';
import style from './header.component.scss';

export const LayoutHeaderComponent = {
  template,
  styles: [style],
  controller: class HeaderController {
    constructor($window, $transitions, $state, UserService, JwtService, $rootScope) {
      'ngInject';
      this._$window = $window;
      this._$transitions = $transitions;
      this._$state = $state;
      this._UserService = UserService;
      this._JwtService = JwtService;
      this._$rootScope = $rootScope;

      this.hideObserve = true;
      this.hideAnalyze = true;

      const token = this._JwtService.getTokenObj();
      if (!token) {
        $window.location.assign('/login.html');
        return;
      }
      const product = get(token, 'ticket.products.[0]');
      for (let i = 0; i < product.productModules.length; i++) {
        if (product.productModules[i].productModCode === 'OBSRV00001') {
          this.hideObserve = false;
        } else if (product.productModules[i].productModCode === 'ANLYS00001') {
          this.hideAnalyze = false;
        }
      }
    }

    get showProgress() {
      return this._$rootScope.showProgress;
    }

    isState(stateName) {
      return Boolean(this._$state.is(stateName));
    }

    logout() {
      this._UserService.logout('logout').then(() => {
        this._$window.location.assign('./login.html');
      });
    }

    changePwd() {
      this._$window.location.assign('./login.html#!/changePwd');
    }
  }
};
