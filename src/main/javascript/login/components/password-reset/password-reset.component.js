import template from './password-reset.component.html';

export const PasswordResetComponent = {
  template,
  controller: class PasswordResetController {
    constructor($window, JwtService, UserService) {
      'ngInject';
      this._$window = $window;
      this._JwtService = JwtService;
      this._UserService = UserService;
    }

    $onInit() {
      if (this._$window.location.href.indexOf('/resetPassword?rhc') != -1) {
        const hashCode = this._$window.location.href;
        const rhc = hashCode.split('rhc=')[1];
        const rData = {
          rhc: rhc
        }
        this._UserService.verify(rData).then(res => {
          if (res.data.valid) {
            this.username = res.data.masterLoginID;
          }
          else {
            this.errorMsg = res.data.validityReason + ". Please regenerate the link once again";
          }
        });
      }
    }

    resetPwd() {
      this._UserService.resetPwd(this)
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
