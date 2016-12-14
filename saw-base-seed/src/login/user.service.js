/**
 * Created by ssom0002 on 12/6/2016.
 */
import AppConstants from './app.constants';
export class UserService {
  constructor($http, JwtService, $window, AuthService, $state) {
    'ngInject';
    this._$http = $http;
    this._JwtService = JwtService;
    this._$window = $window;
    this._AuthService = AuthService;
    this._$state = $state;
  }

  attemptAuth(formData) {
    const LoginDetails = {
      masterLoginId: formData.masterLoginId,
      password: formData.authpwd
    };
    const route = '/doAuthenticate';
    return this._$http.post(AppConstants.api + route, LoginDetails).then(
      response => {
        const base64Url = response.data.token.split('.')[1];
        const base64 = base64Url.replace('-', '+').replace('_', '/');
        const resp = angular.fromJson(this._$window.atob(base64));
        // Store the user's info for easy lookup
        this._JwtService.save(resp.ticket);
        return resp;
      }
    );
  }
  logout(path) {
    const route = '/doLogout';
    const ticketId = angular.fromJson(this._$window.localStorage[AppConstants.jwtKey]).ticketId;
    this._$http({
      url: AppConstants.api + route,
      method: 'POST',
      data: ticketId
    }).then(
      res => {
        console.log('before' + this._$window.localStorage[AppConstants.jwtKey]);
        this._JwtService.destroy();
        console.log('after' + this._$window.localStorage[AppConstants.jwtKey]);
        if (path === 'logout') {
          // const baseUrl = this._$window.location.origin;
          // const appUrl = `${baseUrl}/login`;
          // this._$window.location = appUrl;
          this._AuthService.clear();
          this._AuthService.go('/');
        }
      }
    );
  }
  changePwd(credentials) {
    const route = '/changePassword';
    const LoginDetails = {
      masterLoginId: angular.fromJson(this._$window.localStorage[AppConstants.jwtKey]).masterLoginId,
      oldPassword: credentials.oldPwd,
      newPassword: credentials.newPwd,
      cnfNewPassword: credentials.confNewPwd
    };
    return this._$http({
      url: AppConstants.api + route,
      method: 'POST',
      data: LoginDetails
    }).then(
      res => {
        if (res.data.valid) {
          this.logout('change');
        }
        return res;
      }
    );
  }
}
