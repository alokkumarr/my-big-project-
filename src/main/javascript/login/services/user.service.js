class UserService {
  constructor($window, $http, $state, AppConfig, JwtService) {
    this._$window = $window;
    this._$http = $http;
    this._$state = $state;
    this._AppConfig = AppConfig;
    this._JwtService = JwtService;
  }

  attemptAuth(formData) {
    const LoginDetails = {
      masterLoginId: formData.masterLoginId,
      password: formData.authpwd
    };

    const route = '/doAuthenticate';

    return this._$http.post(this._AppConfig.login.url + route, LoginDetails)
      .then(response => {
        const base64Url = response.data.token.split('.')[1];
        const base64 = base64Url.replace('-', '+').replace('_', '/');
        const resp = angular.fromJson(this._$window.atob(base64));

        // Store the user's info for easy lookup
        if (resp.ticket.valid) {
          // this._JwtService.destroy();
          this._JwtService.set(response.data.token);
          this._$http.defaults.headers.common.Authorization = 'Bearer ' + response.data.token;
        }

        return resp;
      });
  }

  logout(path) {
    const route = '/auth/doLogout';
    const token = this._JwtService.get();
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace('-', '+').replace('_', '/');
    const resp = angular.fromJson(this._$window.atob(base64));

    this._$http.defaults.headers.common.Authorization = 'Bearer ' + token;

    return this._$http.post(this._AppConfig.login.url + route, resp.ticket.ticketId)
      .then(() => {

        if (path === 'logout') {
          this._JwtService.destroy();
          this._$state.go('login');
        }
      });
  }

  changePwd(credentials) {
    const route = '/auth/changePassword';
    const token = this._JwtService.get();
    if (!token) {
      this.errorMsg = 'Please login to change password';
      return;
    }
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace('-', '+').replace('_', '/');
    const resp = angular.fromJson(this._$window.atob(base64));
    const LoginDetails = {
      masterLoginId: resp.ticket.masterLoginId,
      oldPassword: credentials.formData.oldPwd,
      newPassword: credentials.formData.newPwd,
      cnfNewPassword: credentials.formData.confNewPwd
    };

    this._$http.defaults.headers.common.Authorization = 'Bearer ' + this._JwtService.get();

    return this._$http.post(this._AppConfig.login.url + route, LoginDetails)
      .then(res => {
        if (res.data.valid) {
          this.logout('change');
        }

        return res;
      });
  }

  preResetPwd(credentials) {
    const route = '/resetPassword';
    const productUrl = `${this._$window.location.href.split("/preResetPwd")[0]}/resetPassword`;

    const LoginDetails = {
      masterLoginId: credentials.masterLoginId,
      productUrl
    };

    this._$http.defaults.headers.common.Authorization = 'Bearer ' + this._JwtService.get();

    return this._$http.post(this._AppConfig.login.url + route, LoginDetails)
      .then(res => {
        return res;
      });
  }

  resetPwd(credentials) {
    const route = '/rstChangePassword';
    const ResetPasswordDetails = {
      masterLoginId: credentials.username,
      newPassword: credentials.newPwd,
      cnfNewPassword: credentials.confNewPwd
    };
    this._$http.defaults.headers.common.Authorization = 'Bearer ' + this._JwtService.get();
    return this._$http.post(this._AppConfig.login.url + route, ResetPasswordDetails)
      .then(res => {
        return res;
      });
  }

  verify(hashCode) {
    const route = '/vfyRstPwd';
    return this._$http.post(this._AppConfig.login.url + route, hashCode)
      .then(res => {
        return res;
      });
  }

  redirect(baseURL) {
    const route = '/auth/redirect';
    return this._$http.post(this._AppConfig.login.url + route, baseURL)
      .then(res => {
        return res;
      });
  }
}

export function UserServiceFactory($window, $http, $state, AppConfig, JwtService) {
  'ngInject';
  return new UserService($window, $http, $state, AppConfig, JwtService);
}
