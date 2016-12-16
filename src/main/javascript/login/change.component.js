/**
 * Created by ssom0002 on 12/7/2016.
 */
import template from './change.component.html';
import AppConstants from './app.constants';

export const ChangeComponent = {
  template,
  controller: class ChangeController {
    constructor($window, UserService) {
      this._$window = $window;
      this._UserService = UserService;
    }
    changePwd() {
      const token = angular.fromJson(this._$window.localStorage[AppConstants.jwtKey]);
      if (angular.isUndefined(token)) {
        this.errorMsg = 'Please login to change password';
      } else {
        this._UserService.changePwd(this.formData).then(
          res => {
            this.errorMsg = res.data.validityMessage;
          }
        );
      }
    }
    login() {
      const baseUrl = this._$window.location.origin;
      const appUrl = `${baseUrl}/login`;
      this._$window.location = appUrl;
    }
  }
};
