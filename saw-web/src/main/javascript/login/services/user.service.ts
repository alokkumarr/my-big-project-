import * as get from 'lodash/get';
import { Injectable } from '@angular/core';
import { StateService } from '@uirouter/angular';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import AppConfig from '../../../../../appConfig';
import { JwtService } from './jwt.service';
const loginUrl = AppConfig.login.url;
const refreshTokenEndpoint = 'getNewAccessToken';

@Injectable()
export class UserService {
  constructor(
    private _http : HttpClient,
    private _state: StateService,
    private _jwtService: JwtService
  ) {}

  attemptAuth(formData) {
    const LoginDetails = {
      masterLoginId: formData.masterLoginId,
      password: formData.authpwd
    };

    const route = '/doAuthenticate';

    return this._http.post(loginUrl + route, LoginDetails).toPromise()
      .then(response => {
        const resp = this._jwtService.parseJWT(get(response, 'data.aToken'));

        // Store the user's info for easy lookup
        if (this._jwtService.isValid(resp)) {
          // this._jwtService.destroy();
          this._jwtService.set(get(response, 'data.aToken'), get(response, 'data.rToken'));
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

    return this._http.get(loginUrl + route, {
      params: {
        jwt: token
      }
    }).toPromise().then(response => {
      const resp = this._jwtService.parseJWT(get(response, 'data.aToken'));

      // Store the user's info for easy lookup
      if (this._jwtService.isValid(resp)) {
        // this._jwtService.destroy();
        this._jwtService.set(get(response, 'data.aToken'), get(response, 'data.rToken'));
      }

      return true;
    });
  }

  logout(path) {
    const route = '/auth/doLogout';
    const token = this._jwtService.get();
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace('-', '+').replace('_', '/');
    const resp = JSON.parse(window.atob(base64));
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type':  'application/json',
        'Authorization': `Bearer ${token}`
      })
    };
    return this._http.post(loginUrl + route, resp.ticket.ticketId, httpOptions).toPromise()
      .then(() => {
        this._jwtService.destroy();
        if (path === 'logout') {
          this._state.reload();
        }
      });
  }

  changePwd(credentials) {
    const route = '/auth/changePassword';
    const token = this._jwtService.get();

    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace('-', '+').replace('_', '/');
    const resp = JSON.parse(window.atob(base64));
    const LoginDetails = {
      masterLoginId: resp.ticket.masterLoginId,
      oldPassword: credentials.formData.oldPwd,
      newPassword: credentials.formData.newPwd,
      cnfNewPassword: credentials.formData.confNewPwd
    };
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type':  'application/json',
        'Authorization': `Bearer ${this._jwtService.get()}`
      })
    };

    return this._http.post(loginUrl + route, LoginDetails, httpOptions).toPromise()
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
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type':  'application/json',
        'Authorization': `Bearer ${this._jwtService.get()}`
      })
    };

    return this._http.post(loginUrl + route, LoginDetails, httpOptions).toPromise()
      .then(res => {
        return res;
      });
  }

  resetPwd(credentials) {
    const route = '/rstChangePassword';
    const ResetPasswordDetails = {
      masterLoginId: credentials.username,
      newPassword: credentials.newPwd,
      cnfNewPassword: credentials.confNewPwd,
      rfc: credentials.rhcToken
    };
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type':  'application/json',
        'Authorization': `Bearer ${this._jwtService.get()}`
      })
    };
    return this._http.post(loginUrl + route, ResetPasswordDetails, httpOptions).toPromise()
      .then(res => {
        return res;
      });
  }

  verify(hashCode) {
    const route = '/vfyRstPwd';
    return this._http.post(loginUrl + route, hashCode).toPromise()
      .then(res => {
        return res;
      });
  }

  redirect(baseURL) {
    const route = '/auth/redirect';
    return this._http.post(loginUrl + route, baseURL).toPromise()
      .then(res => {
        return res;
      });
  }

  refreshAccessToken(rtoken = this._jwtService.getRefreshToken()) {
    const route = `/${refreshTokenEndpoint}`;
    return this._http.post(loginUrl + route, rtoken).toPromise()
      .then(response => {
        const resp = this._jwtService.parseJWT(get(response, 'data.aToken'));
        // Store the user's info for easy lookup
        if (this._jwtService.isValid(resp)) {
          // this._jwtService.destroy();
          this._jwtService.set(get(response, 'data.aToken'), get(response, 'data.rToken'));
        }
        return resp;
      }, err => {
        throw err;
      });
  }
}
