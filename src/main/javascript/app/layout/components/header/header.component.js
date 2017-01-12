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
      return !!this._$state.is(stateName);
    }

    logout() {
      this._UserService.logout('logout');
    }

    changePwd() {
      const baseUrl = this._$window.location.origin;
      const appUrl = `${baseUrl}/saw-base-seed/login.html#!/changePwd`;

      this._$window.location = appUrl;
    }
  }
};
