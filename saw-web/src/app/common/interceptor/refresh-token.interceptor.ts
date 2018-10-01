import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import 'rxjs/add/operator/catch';

import * as get from 'lodash/get';

import { JwtService, UserService } from '../../common/services';

@Injectable()
export class RefreshTokenInterceptor implements HttpInterceptor {
  constructor(private jwt: JwtService, public user: UserService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    let refreshRequest = null;
    // Clone the request to add the new header.
    // const authReq = req.clone({ headers: req.headers.set('Authorization', `Bearer ${this.jwt.getAccessToken()}`) });

    // send the newly created request
    return next.handle(req)
      .catch((error: HttpErrorResponse, caught) => {
        const errorMessage = get(error, 'error.message', '');
        const refreshRegexp = new RegExp(UserService.refreshTokenEndpoint);

        const tokenMessageRegex = /token has expired|invalid token/i;

        if (!(error.status === 401 && tokenMessageRegex.test(errorMessage))) {
          return throwError(error);
        }

        if (refreshRegexp.test(get(error, 'url', ''))) {
          // response.config._hideError = true;
          return throwError(error);
        }

        if (!refreshRequest) {
          refreshRequest = this.user.refreshAccessToken();
        }

        return Observable.create(observer => {
          refreshRequest.then(data => {
            refreshRequest = null;
            observer.next(data);
            observer.complete();
          }, err => {
            observer.error(err);
          });

          return observer;
        }).flatMap(() => {
          const token = this.jwt.getTokenObj();
          if (token && this.jwt.isValid(token)) {
            const bearer = `Bearer ${this.jwt.getAccessToken()}`;
            const newRequest: HttpRequest<any> = req.clone({headers: req.headers.set('Authorization', bearer)});
            return next.handle(newRequest);
          } else {
            /* If token wasn't refreshed successfully, delete the token and reload */
            this.jwt.destroy();
            window.location.reload();
            return throwError(new Error('Token can\'t be refreshed'));
          }
        });
      }) as any;
  }
}

