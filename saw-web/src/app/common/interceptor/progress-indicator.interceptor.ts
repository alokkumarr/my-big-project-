import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
  HttpEventType
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

import { HeaderProgressService } from '../../common/services';

@Injectable()
export class ProgressIndicatorInterceptor implements HttpInterceptor {
  constructor(private _headerProgress: HeaderProgressService) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      tap(
        event => {
          switch (event.type) {
            case HttpEventType.Sent:
              this._headerProgress.show();
              break;
            case HttpEventType.Response:
              this._headerProgress.hide();
              break;
          }
        },
        err => {
          this._headerProgress.hide();
          return err;
        }
      )
    );
  }
}
