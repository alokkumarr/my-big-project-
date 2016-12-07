import template from './login.component.html';

export const loginComponent = {
  template,
  controller: class LoginController {
    constructor($window) {
      this.$window = $window;
    }
    login() {
      const baseUrl = this.$window.location.origin;
      const appUrl = `${baseUrl}/observe`;

      this.$window.location = appUrl;
    }
  }
};
