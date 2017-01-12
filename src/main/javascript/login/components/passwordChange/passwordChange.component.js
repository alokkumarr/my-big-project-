import template from './passwordChange.component.html';

export const PasswordChangeComponent = {
  template,
  controller: class PasswordChangeController {
    constructor($window, JwtService, UserService) {
      'ngInject';
      this._$window = $window;
      this._JwtService = JwtService;
      this._UserService = UserService;
    }

    changePwd() {
      const token = this._JwtService.get();

      if (!token) {
        this.errorMsg = 'Please login to change password';
      } else {
        this._UserService.changePwd(this)
          .then(res => {
            this.errorMsg = res.data.validityMessage;
          });
      }
    }

    login() {
      const baseUrl = this._$window.location.origin;
      const appUrl = `${baseUrl}/login.html`;

      this._$window.location = appUrl;
    }
  }
};
