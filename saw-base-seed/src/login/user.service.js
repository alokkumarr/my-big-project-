/**
 * Created by ssom0002 on 12/6/2016.
 */
import AppConstants from './app.constants';

export function UserService($http, JwtService, $window) {
  'ngInject';

  return {
    UserService,
    attemptAuth,
    logout,
    changePwd
  };

  function attemptAuth(formData) {
    const LoginDetails = {
      masterLoginId: formData.masterLoginId,
      password: formData.authpwd
    };
    const route = '/doAuthenticate';
    return $http.post(AppConstants.api + route, LoginDetails).then(
      response => {
        const base64Url = response.data.token.split('.')[1];
        const base64 = base64Url.replace('-', '+').replace('_', '/');
        const resp = angular.fromJson($window.atob(base64));
        // Store the user's info for easy lookup
        if (resp.ticket.valid) {
          JwtService.save(response.data.token);
          $http.defaults.headers.common.Authorization = 'Bearer ' + response.data.token;
        }
        return resp;
      }
    );
  }
  function logout(path) {
    const route = '/auth/doLogout';
    const token = $window.localStorage[AppConstants.jwtKey];
    $http.defaults.headers.common.Authorization = 'Bearer ' + token;
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace('-', '+').replace('_', '/');
    const resp = angular.fromJson($window.atob(base64));
    $http.post(AppConstants.api + route, resp.ticket.ticketId).then(
      res => {
        JwtService.destroy();
        $http.defaults.headers.common.Authorization = 'Basic';
        if (path === 'logout') {
          const baseUrl = this._$window.location.origin;
          const appUrl = `${baseUrl}/login`;
          $window.location = appUrl;
        }
      }
    );
  }
  function changePwd(credentials) {
    const route = '/auth/changePassword';
    $http.defaults.headers.common.Authorization = 'Bearer ' + $window.localStorage[AppConstants.jwtKey];
    const LoginDetails = {
      masterLoginId: angular.toJson($window.localStorage[AppConstants.jwtKey]).masterLoginId,
      oldPassword: credentials.oldPwd,
      newPassword: credentials.newPwd,
      cnfNewPassword: credentials.confNewPwd
    };
    return $http.post(AppConstants.api + route, LoginDetails).then(
      res => {
        if (res.data.valid) {
          this.logout('change');
        }
        return res;
      }
    );
  }
}
