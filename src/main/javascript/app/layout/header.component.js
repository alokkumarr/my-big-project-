import template from './header.component.html';

export const HeaderComponent = {
  template,
  controller: class HeaderController {
    constructor($transitions, UserService, $window) {
      'ngInject';
      const match = {
        to: state => {
          return state.name === 'observe' ||
            state.name === 'analyze' ||
            state.name === 'alerts';
        }
      };
      $transitions.onEnter(match, (transition, state) => {
        this.stateName = state.name;
      });
      this._UserService = UserService;
      this._$window = $window;
    }
    logout() {
      this._UserService.logout('logout');
    }
    changePwd() {
      this._$window.location = `${this._$window.location.origin}${this._$window.location.pathname}/login.html#!/changePwd`;
    }
  }
};
