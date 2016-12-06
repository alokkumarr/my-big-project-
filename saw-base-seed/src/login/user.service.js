/**
 * Created by ssom0002 on 12/6/2016.
 */
import AppConstants from './app.constants';
export class UserService {
  constructor($http, JwtService, $window) {
    'ngInject';
    this.$http = $http;
    this._JwtService = JwtService;
    this.current = null;
    this.$window = $window;
  }

  attemptAuth(formData) {
    const LoginDetails = {
      masterLoginId: formData.masterLoginId,
      password: formData.authpwd
    };
    const route = '/doAuthenticate';
    return this.$http.post(AppConstants.api + route, LoginDetails).then(
      response => {
        const base64Url = response.data.token.split('.')[1];
        const base64 = base64Url.replace('-', '+').replace('_', '/');
        const resp = angular.fromJson(this.$window.atob(base64));
        // Store the user's info for easy lookup
        this._JwtService.save(resp.ticket);
        this.current = resp.ticket;
        return resp;
      }
    );
  }
  logout() {
    const route = '/doLogout';
    const ticketID = this.current.ticketId;
    this._$http({
      url: AppConstants.api + route,
      method: 'POST',
      data: ticketID
    }).then(
      res => {
        console.log('before' + this.current);
        this.current = null;
        console.log('after' + this.current);
      }
    );
  }
}
