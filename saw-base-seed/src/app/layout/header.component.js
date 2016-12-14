import template from './header.component.html';

export const HeaderComponent = {
  template,
  controller: class HeaderController {
    constructor($transitions, UserService, AuthService) {
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
      this._AuthService = AuthService;
    }
    logout() {
      this._UserService.logout('logout');
    }
    changePwd() {
      // const baseUrl = this._$window.location.origin;
      // const appUrl = `${baseUrl}/changePwd`;
      // this._$window.location = appUrl;
      this._AuthService.go('/changePwd');
    }
  }
};
