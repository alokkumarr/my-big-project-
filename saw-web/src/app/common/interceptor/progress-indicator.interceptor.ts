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
    return next.handle(req).pipe(
      tap(event => {
        if (event.type === HttpEventType.Sent) {
          this.zone.run(() => {
            this._headerProgress.show();
          });
        }
      }),
      finalize(() => {
        this.zone.run(() => {
          this._headerProgress.hide();
        });
      })
    );
  }
}
