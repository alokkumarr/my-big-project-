import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import 'rxjs/add/observable/throw'
import 'rxjs/add/operator/catch';

import { JwtService } from '../../../login/services/jwt.service';

@Injectable()
export class AddTokenInterceptor implements HttpInterceptor {
  constructor(private jwt: JwtService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    // Clone the request to add the new header.
    let authReq = req.clone({
      headers: req.headers.set('Content-Type', 'application/json')
    });
    authReq = authReq.clone({
      headers: authReq.headers.set('Authorization', `Bearer ${this.jwt.getAccessToken()}`)
    });
    //send the newly created request
    return next.handle(authReq)
      .catch((error, caught) => {
        return Observable.throw(error);
      }) as any;
  }
}
