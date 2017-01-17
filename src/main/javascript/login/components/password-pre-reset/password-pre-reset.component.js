import template from './password-pre-reset.component.html';

export const PasswordPreResetComponent = {
  template,
  controller: class PasswordPreResetController {
    constructor($window, $state, JwtService, UserService) {
      'ngInject';
      this._$window = $window;
      this._$state = $state;
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
      this._$state.go('login');
    }
  }
};
