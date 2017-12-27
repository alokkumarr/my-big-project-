import { Injectable, Injector } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse, HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/observable/throw'
import 'rxjs/add/operator/catch';

import * as get from 'lodash/get';

import { JwtService } from '../../../../login/services/jwt.service';
import { UserService } from '../../../../login/services/user.service';

@Injectable()
export class RefreshTokenInterceptor implements HttpInterceptor {
  constructor(private jwt: JwtService, private user: UserService, private injector: Injector) { }

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
        this.user.refreshAccessToken(console.log.bind(console));

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
            const newRequest: HttpRequest<any> = req.clone({headers: req.headers.set('Authorization', `Bearer ${data}`)});
            const http = this.injector.get(HttpClient);
            http.request(newRequest).subscribe(result => {
              observer.next(result);
              observer.complete();
            }, error => {
              observer.error(error);
            });
          }, error => {
            refreshRequest = null;
            this.jwt.destroy();
            observer.error(error);
          });

          return observer;
        });

        // return Observable.throw(error);
      }) as any;
  }
}

