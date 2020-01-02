import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, flatMap } from 'rxjs/operators';
import { Router } from '@angular/router';

import * as get from 'lodash/get';

import { JwtService, UserService } from '../../common/services';

@Injectable()
export class RefreshTokenInterceptor implements HttpInterceptor {
  constructor(
    private jwt: JwtService,
    public user: UserService,
    public _router: Router
  ) {}

  logout() {
    this.jwt.destroy();
    this._router.navigate(['/#/login']);
    return throwError(new Error(`Token can't be refreshed`));
  }

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    let refreshRequest = null;
    // Clone the request to add the new header.
    // const authReq = req.clone({ headers: req.headers.set('Authorization', `Bearer ${this.jwt.getAccessToken()}`) });

    // send the newly created request
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse, caught) => {
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
        return new Observable(observer => {
          refreshRequest
            .then(() => {
              refreshRequest = null;
              return this.jwt.validateToken();
            })
            .then(
              () => {
                observer.next(true);
                observer.complete();
              },
              err => {
                /* If token wasn't refreshed successfully, delete the token and navigate to login */
                observer.error(new Error('Session expired.'));
                this.logout();
              }
            );

          return observer;
        }).pipe(
          flatMap(() => {
            const token = this.jwt.getTokenObj();
            if (token && this.jwt.isValid(token)) {
              const bearer = `Bearer ${this.jwt.getAccessToken()}`;
              const newRequest: HttpRequest<any> = req.clone({
                headers: req.headers.set('Authorization', bearer)
              });
              return next.handle(newRequest);
            } else {
              /* If token wasn't refreshed successfully, delete the token and navigate to login */
              return this.logout();
            }
          })
        );
      }) as any
    );
  }
}
