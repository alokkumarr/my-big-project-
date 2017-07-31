import template from './login.component.html';

export const LoginComponent = {
  template,
  controller: class LoginController {
    constructor($window, $state, UserService, JwtService) {
      'ngInject';
      this._$window = $window;
      this._$state = $state;
      this._UserService = UserService;
      this._JwtService = JwtService;

      this.dataHolder = {
        username: null,
        password: null
      };

      this.states = {
        error: null
      };
    }

    login() {
      this._UserService.attemptAuth({
        masterLoginId: this.dataHolder.username,
        authpwd: this.dataHolder.password
      })
        .then(res => {
          if (this._JwtService.isValid(res)) {
            this._$window.location.assign('./');
          } else {
            this.states.error = this._JwtService.getValidityReason(res);
          }
        })
        .catch(err => {
          this.states.error = 'Network Error!';
          throw err;
        });
    }

    reset() {
      this._$state.go('preResetPassword');
    }
  }
};
