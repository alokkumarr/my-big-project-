import template from './header.component.html';

export const HeaderComponent = {
  template,
  controller: class HeaderController {
    constructor(UserService) {
      'ngInject';
      this._UserService = UserService;
    }
    logout() {
      this._UserService.logout();
    }
}
};
