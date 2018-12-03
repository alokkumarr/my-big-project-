import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpInterceptor,
  HttpHandler,
  HttpRequest
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

import * as get from 'lodash/get';
import * as truncate from 'lodash/truncate';
const ERROR_TITLE_LENGTH = 30;
import { CUSTOM_HEADERS } from '../consts';

import { ToastService } from '../services/toastMessage.service';

@Injectable()
export class HandleErrorInterceptor implements HttpInterceptor {
  constructor(private toast: ToastService) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    let showToast = true;
    let newReq = req;
    // send the newly created request
    if (req.headers.has(CUSTOM_HEADERS.SKIP_TOAST)) {
      showToast = false;
      newReq = req.clone({
        headers: req.headers.delete(CUSTOM_HEADERS.SKIP_TOAST)
      });
    }
    return next.handle(newReq).pipe(catchError((error, caught) => {
      showToast && this.toast.error(this.getTitle(error), '', { error });
      return throwError(error);
    }) as any);
  }

  getTitle(error, defaultMessage = 'Error') {
    const title =
      get(error, 'error.error.message') ||
      get(error, 'error.message') ||
      get(error, 'message', '') ||
      get(error, 'error', '');
    /* prettier-ignore */
    return title ? truncate(title, {
      length: ERROR_TITLE_LENGTH
    }) : defaultMessage;
  }
}
