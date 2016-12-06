import template from './login.component.html';

export const loginComponent = {
  template,
  controller: class LoginController {
    constructor($window, UserService) {
      this._$window = $window;
      this._UserService = UserService;
    }
    login() {
      this._UserService.attemptAuth(this.formData).then(
        res => {
          if (res.ticket.valid) {
            const baseUrl = this._$window.location.origin;
            const appUrl = `${baseUrl}/observe`;
            this._$window.location = appUrl;
          } else {
            console.log('fail' + res.ticket.valid);
            this.errorMsg = res.ticket.validityReason;
          }
        }
      );
    }
  }
};
