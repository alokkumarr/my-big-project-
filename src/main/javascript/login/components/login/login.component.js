import template from './login.component.html';

export const LoginComponent = {
  template,
  controller: class LoginController {
    constructor($window, $state, UserService) {
      'ngInject';
      this._$window = $window;
      this._$state = $state;
      this._UserService = UserService;

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
          if (res.ticket.valid) {
            this._$window.location.assign('./');
          } else {
            this.states.error = res.ticket.validityReason;
          }
        })
        .catch(() => {
          this.states.error = 'Network Error!';
        });
    }

    reset() {
      this._$state.go('preResetPassword');
    }
  }
};
