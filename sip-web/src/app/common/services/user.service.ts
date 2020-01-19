import { Store } from '@ngxs/store';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpBackend } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';

import AppConfig from '../../../../appConfig';
import { JwtService } from './jwt.service';
import { CommonResetStateOnLogout } from '../actions/common.actions';

const loginUrl = AppConfig.login.url;
const refreshTokenEndpoint = 'getNewAccessToken';

interface TokenResponse {
  aToken: string;
  rToken: string;
}
@Injectable()
export class UserService {
  static refreshTokenEndpoint = refreshTokenEndpoint;

  loginChange$ = new BehaviorSubject(false);
  public _httpClientWithoutIntercepors: HttpClient;

  constructor(
    public _http: HttpClient,
    public _jwtService: JwtService,
    private store: Store,
    public handler: HttpBackend
  ) {
    this._httpClientWithoutIntercepors = new HttpClient(handler);
  }

  attemptAuth(formData) {
    const LoginDetails = {
      masterLoginId: formData.masterLoginId,
      password: formData.authpwd
    };

    const route = '/doAuthenticate';

    return this._http
      .post<TokenResponse>(loginUrl + route, LoginDetails)
      .toPromise()
      .then(response => this.setJwtIfValid(response))
      .then(jwt => {
        if (this._jwtService.isValid(jwt)) {
          this.loginChange$.next(true);
        }
        return jwt;
      });
  }

  isLoggedIn() {
    const token = this._jwtService.getTokenObj();
    const refreshToken = this._jwtService.refreshTokenObject;
    const isTokenvalid = this._jwtService.isValid(token);
    const isRefreshTokenValid = this._jwtService.isValid(refreshToken);
    return isTokenvalid || isRefreshTokenValid;
  }

  /**
   * Exchanges a single-sign-on token for actual login tokens
   */
  exchangeLoginToken(token): Observable<boolean> {
    const route = '/authentication';

    return this._http
      .get<TokenResponse>(loginUrl + route, {
        params: {
          jwt: token
        }
      })
      .pipe(map(response => this.setJwtIfValid(response)));
  }

  logout(path) {
    const route = '/auth/doLogout';
    const token = this._jwtService.get();
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace('-', '+').replace('_', '/');
    const resp = JSON.parse(window.atob(base64));
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      })
    };

    return this._http
      .post(loginUrl + route, resp.ticket.ticketId, httpOptions)
      .toPromise()
      .then(
        () => {
          this._jwtService.destroy();
          this.store.dispatch(new CommonResetStateOnLogout());
        },
        () => {
          this._jwtService.destroy();
          this.store.dispatch(new CommonResetStateOnLogout());
        }
      );
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
        'Content-Type': 'application/json',
        Authorization: `Bearer ${this._jwtService.get()}`
      })
    };

    return this._http
      .post(loginUrl + route, LoginDetails, httpOptions)
      .toPromise()
      .then((res: any) => {
        if (res.valid) {
          this.logout('change');
        }

        return res;
      });
  }

  preResetPwd(credentials) {
    const route = '/resetPassword';
    const productUrl = `${
      window.location.href.split('/preResetPwd')[0]
    }/resetPassword`;

    const LoginDetails = {
      masterLoginId: credentials.masterLoginId,
      productUrl
    };
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: `Bearer ${this._jwtService.get()}`
      })
    };

    return this._http
      .post(loginUrl + route, LoginDetails, httpOptions)
      .toPromise();
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
        'Content-Type': 'application/json',
        Authorization: `Bearer ${this._jwtService.get()}`
      })
    };
    return this._http
      .post(loginUrl + route, ResetPasswordDetails, httpOptions)
      .toPromise();
  }

  verify(hashCode) {
    const route = '/vfyRstPwd';
    return this._http.post(loginUrl + route, hashCode).toPromise();
  }

  redirect(baseURL) {
    const route = '/auth/redirect';
    return this._http.post(loginUrl + route, baseURL).toPromise();
  }

  refreshAccessToken(rtoken = this._jwtService.getRefreshToken()) {
    const route = `/${refreshTokenEndpoint}`;

    return this._http
      .post<TokenResponse>(loginUrl + route, rtoken)
      .toPromise()
      .then(response => this.setJwtIfValid(response))
      .then(jwt => {
        // Store the user's info for easy lookup
        if (!this._jwtService.isValid(jwt)) {
          throw new Error('Received invalid access token on refresh.');
        }
      });
  }

  authenticateWithSessionID(
    sessionID: string,
    domainName: string,
    clientId: string
  ) {
    const route = '/authenticate';
    const body = { domainName, clientId };
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: sessionID
      })
    };
    return this._httpClientWithoutIntercepors
      .post<TokenResponse>(loginUrl + route, body, httpOptions)
      .toPromise()
      .then(response => this.setJwtIfValid(response));
  }

  setJwtIfValid(response: TokenResponse) {
    const { aToken, rToken } = response;
    const jwt = this._jwtService.parseJWT(aToken);
    // Store the user's info for easy lookup
    if (this._jwtService.isValid(jwt)) {
      this._jwtService.set(aToken, rToken);
    }
    return jwt;
  }
}
