import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/observable/throw'
import 'rxjs/add/operator/catch';

import * as get from 'lodash/get';

import { JwtService } from '../../../../login/services/jwt.service';
import { UserService } from '../../../../login/services/user.service';

@Injectable()
export class RefreshTokenInterceptor implements HttpInterceptor {
  constructor(private jwt: JwtService, private user: UserService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    let refreshRequest = null;
    // Clone the request to add the new header.
    // const authReq = req.clone({ headers: req.headers.set('Authorization', `Bearer ${this.jwt.getAccessToken()}`) });

    //send the newly created request
    return next.handle(req)
      .catch((error: HttpErrorResponse, caught) => {
        const errorMessage = get(error, 'error.message', '');
        const refreshRegexp = new RegExp(this.user.refreshTokenEndpoint);

        const tokenMessageRegex = /token has expired|invalid token/i;

        if (!(error.status === 401 && tokenMessageRegex.test(errorMessage))) {
          return Observable.throw(error);
        }

        if (refreshRegexp.test(get(error, 'url', ''))) {
          // response.config._hideError = true;
          return Observable.throw(error);
        }

        if (!refreshRequest) {
          refreshRequest = this.user.refreshAccessToken();
        }

        return Observable.create(observer => {
          refreshRequest.then(data => {
            refreshRequest = null;
            observer.next(data);
            observer.complete();
          }, error => {
            observer.error(error);
          });

          return observer;
        }).flatMap(() => {
          const token = this.jwt.getTokenObj();
          if (token && this.jwt.isValid(token)) {
            const newRequest: HttpRequest<any> = req.clone({headers: req.headers.set('Authorization', `Bearer ${this.jwt.getAccessToken()}`)});
            return next.handle(newRequest);
          } else {
            /* If token wasn't refreshed successfully, delete the token and reload */
            this.jwt.destroy();
            window.location.reload();
            return Observable.throw(new Error('Token can\'t be refreshed'));
          }
        });
        // return Observable.throw(error);
      }) as any;
  }
}

