import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import 'rxjs/add/operator/catch';

import { JwtService } from '../../common/services';
@Injectable()
export class AddTokenInterceptor implements HttpInterceptor {
  constructor(private jwt: JwtService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    // Clone the request to add the new header.
    const authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${this.jwt.getAccessToken()}`)
    });
    // send the newly created request
    return next.handle(authReq)
      .catch((error, caught) => {
        return throwError(error);
      }) as any;
  }
}
