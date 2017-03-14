import template from './password-change.component.html';

export const PasswordChangeComponent = {
  template,
  controller: class PasswordChangeController {
    constructor($window, $state, JwtService, UserService) {
      'ngInject';
      this._$window = $window;
      this._$state = $state;
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
      this._$state.go('login');
    }
  }
};
