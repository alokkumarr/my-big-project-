import template from './header.component.html';
import style from './header.component.scss';

export const LayoutHeaderComponent = {
  template,
  styles: [style],
  controller: class HeaderController {
    constructor($window, $transitions, $state, UserService, JwtService) {
      'ngInject';
      this._$window = $window;
      this._$transitions = $transitions;
      this._$state = $state;
      this._UserService = UserService;
      this._JwtService = JwtService;

      this.hideObserve = true;
      this.hideAnalyze = true;

      const token = this._JwtService.get();
      if (!token) {
        $window.location.assign('/login.html');
        return;
      }
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace('-', '+').replace('_', '/');
      const resp = angular.fromJson(this._$window.atob(base64));
      if (resp.ticket.roleType === 'ADMIN') {
        this.showAdmin = true;
      }
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
    admin() {
      this._$state.go('admin');
    }
  }
};
