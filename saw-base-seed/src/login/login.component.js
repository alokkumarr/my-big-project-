import template from './login.component.html';

export const loginComponent = {
  template,
  controller: class LoginController {
    constructor($window, UserService, AuthService) {
      this._$window = $window;
      this._UserService = UserService;
      this._AuthService = AuthService;
    }
    login() {
      this._UserService.attemptAuth(this.formData).then(
        res => {
          if (res.ticket.valid) {
            // const baseUrl = this._$window.location.origin;
            // const appUrl = `${baseUrl}/observe`;
            // this._$window.location = appUrl;
            this._AuthService.go('saw-base-seed/dist/#observe');
          } else {
            console.log('fail' + res.ticket.valid);
            this.errorMsg = res.ticket.validityReason;
          }
        }
      );
    }
  }
};
