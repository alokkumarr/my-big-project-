import template from './header.component.html';

export const LayoutHeaderComponent = {
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
      this._UserService.logout('logout');
    }

    changePwd() {
      this._$window.location.assign('./login#!/changePwd');
    }
  }
};
