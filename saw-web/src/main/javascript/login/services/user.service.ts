import { Inject, Injectable, Component } from '@angular/core';
import { JwtService } from './jwt.service';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import * as get from 'lodash/get';
import * as fpGet from 'lodash/fp/get';

import 'rxjs/add/observable/of';

import APP_CONFIG from '../../../../../appConfig';

@Component({
/* . . . */
  providers: [JwtService]
})

@Injectable()
export class UserService {
  constructor(private _JwtService: JwtService, private _$http: HttpClient) {}
  
  private loginUrl = fpGet('login.url', APP_CONFIG);
  private refreshTokenEndpoint = 'getNewAccessToken';


  attemptAuth(formData): Observable<any> {
    const LoginDetails = {
      masterLoginId: formData.masterLoginId,
      password: formData.authpwd
    };

    const route = '/doAuthenticate';

    return this._$http
      .post(this.loginUrl + route, LoginDetails)
      .map(response => {
        const resp = this._JwtService.parseJWT(get(response, 'aToken'));

        // Store the user's info for easy lookup
        if (this._JwtService.isValid(resp)) {
          // this._JwtService.destroy();
          this._JwtService.set(get(response, 'aToken'), get(response, 'rToken'));
        }
        return resp;
      });
  }

  /**
   * Exchanges a single-sign-on token for actual login tokens
   *
   * @param {any} token
   * @returns
   * @memberof UserService
   */
  exchangeLoginToken(token) {
    const route = '/authentication';

    return this._$http.get(this.loginUrl + route, {
      params: {
        jwt: token
      }
    }).then(response => {
      const resp = this._JwtService.parseJWT(get(response, 'aToken'));

      // Store the user's info for easy lookup
      if (this._JwtService.isValid(resp)) {
        // this._JwtService.destroy();
        this._JwtService.set(get(response, 'aToken'), get(response, 'rToken'));
      }

      return true;
    });
  }

  logout(path) {
    const route = '/auth/doLogout';
    const token = this._JwtService.get();
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace('-', '+').replace('_', '/');
    const resp = angular.fromJson(window.atob(base64));

    this._$http.defaults.headers.common.Authorization = 'Bearer ' + token;

    return this._$http.post(this.loginUrl + route, resp.ticket.ticketId)
      .then(() => {
        this._JwtService.destroy();
        if (path === 'logout') {
          window.location.reload();
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
    const resp = angular.fromJson(window.atob(base64));
    const LoginDetails = {
      masterLoginId: resp.ticket.masterLoginId,
      oldPassword: credentials.formData.oldPwd,
      newPassword: credentials.formData.newPwd,
      cnfNewPassword: credentials.formData.confNewPwd
    };

    this._$http.defaults.headers.common.Authorization = 'Bearer ' + this._JwtService.get();

    return this._$http.post(this.loginUrl + route, LoginDetails)
      .then(res => {
        if (res.data.valid) {
          this.logout('change');
        }

        return res;
      });
  }

  preResetPwd(credentials) {
    const route = '/resetPassword';
    const productUrl = `${window.location.href.split('/preResetPwd')[0]}/resetPassword`;

    const LoginDetails = {
      masterLoginId: credentials.masterLoginId,
      productUrl
    };

    this._$http.defaults.headers.common.Authorization = 'Bearer ' + this._JwtService.get();

    return this._$http.post(this.loginUrl + route, LoginDetails)
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
    return this._$http.post(this.loginUrl + route, ResetPasswordDetails)
      .then(res => {
        return res;
      });
  }

  verify(hashCode) {
    const route = '/vfyRstPwd';
    return this._$http.post(this.loginUrl + route, hashCode)
      .then(res => {
        return res;
      });
  }

  redirect(baseURL) {
    const route = '/auth/redirect';
    return this._$http.post(this.loginUrl + route, baseURL)
      .then(res => {
        return res;
      });
  }

  refreshAccessToken(rtoken = this._JwtService.getRefreshToken()) {
    const route = `/${this.refreshTokenEndpoint}`;
    return this._$http.post(this.loginUrl + route, rtoken)
      .then(response => {
        const resp = this._JwtService.parseJWT(get(response, 'aToken'));
        // Store the user's info for easy lookup
        if (this._JwtService.isValid(resp)) {
          // this._JwtService.destroy();
          this._JwtService.set(get(response, 'aToken'), get(response, 'rToken'));
        }
        return resp;
      }, err => {
        throw err;
      });
  }
}
