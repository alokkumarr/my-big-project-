import * as get from 'lodash/get';
import * as template from './header.component.html';
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

      const token = this._JwtService.getTokenObj();
      const product = get(token, 'ticket.products.[0]');
      this.modules = product.productModules;

      if (JwtService.isAdmin(token)) {
        this.showAdmin = true;
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
