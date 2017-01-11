import template from './password-pre-reset.component.html';

export const PasswordPreResetComponent = {
  template,
  controller: class PasswordPreResetController {
    constructor($window, JwtService, UserService) {
      'ngInject';
      this._$window = $window;
      this._JwtService = JwtService;
      this._UserService = UserService;

      this.dataHolder = {
        masterLoginId: null
      };
    }

    resetPwd() {
      this._UserService.preResetPwd(this.dataHolder)
        .then(res => {
          this.errorMsg = res.data.validityMessage;
        });
    }

    login() {
      const baseUrl = this._$window.location.origin;
      const appUrl = `${baseUrl}/login`;

      this._$window.location = appUrl;
    }
  }
};
