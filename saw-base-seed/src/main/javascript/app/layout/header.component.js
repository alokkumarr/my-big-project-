import template from './header.component.html';

export const HeaderComponent = {
  template,
  controller: class HeaderController {
    constructor($transitions, UserService) {
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
    }
    logout() {
      this._UserService.logout('logout');
    }
    changePwd() {
      const baseUrl = this._$window.location.origin;
      const appUrl = `${baseUrl}/changePwd`;
      this._$window.location = appUrl;
    }
  }
};
