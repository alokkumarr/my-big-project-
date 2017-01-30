import template from './header.component.html';

export const HeaderComponent = {
  template,
  controller: class HeaderController {
    constructor($window, $transitions, $state, UserService) {
      'ngInject';
      this._$window = $window;
      this._$transitions = $transitions;
      this._$state = $state;
      this._UserService = UserService;
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
