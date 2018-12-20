import { Injectable, NgZone } from '@angular/core';
import {
  HttpEvent,
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
  HttpEventType
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap, finalize } from 'rxjs/operators';

import { HeaderProgressService } from '../../common/services';

@Injectable()
export class ProgressIndicatorInterceptor implements HttpInterceptor {
  constructor(
    private _headerProgress: HeaderProgressService,
    private zone: NgZone
  ) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    let cancelled = true;
    return next.handle(req).pipe(
      tap(
        event => {
          switch (event.type) {
            case HttpEventType.Sent:
              this.zone.run(() => {
                this._headerProgress.show();
              });
              break;
            case HttpEventType.Response:
              cancelled = false;
              this.zone.run(() => {
                this._headerProgress.hide();
              });
              break;
          }
        },
        err => {
          cancelled = false;
          this.zone.run(() => {
            this._headerProgress.hide();
          });
          return err;
        }
      ),
      finalize(() => {
        if (cancelled) {
          this.zone.run(() => {
            this._headerProgress.hide();
          });
        }
      })
    );
  }
}
