import template from './passwordPreReset.component.html';

export const PasswordPreResetComponent = {
  template,
  controller: class PasswordPreResetController {
    constructor($window, JwtService, UserService) {
      'ngInject';
      this._$window = $window;
      this._JwtService = JwtService;
      this._UserService = UserService;
    }

    resetPwd() {
      const token = this._JwtService.get();
      this._UserService.preResetPwd(this.dataHolder)
        .then(res => {
           this.errorMsg = res.data.validityMessage;
      });      
    }

    login() {
      const baseUrl = this._$window.location.origin;
      const appUrl = `${baseUrl}/saw-base-seed/login.html`;

      this._$window.location = appUrl;
    }
  }
};
