import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpInterceptor,
  HttpHandler,
  HttpRequest
} from '@angular/common/http';
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/observable/throw';
import 'rxjs/add/operator/catch';

import * as get from 'lodash/get';
import * as truncate from 'lodash/truncate';
const ERROR_TITLE_LENGTH = 30;

import { ToastService } from '../../../common/services/toastMessage.service';

@Injectable()
export class HandleErrorInterceptor implements HttpInterceptor {
  constructor(private toast: ToastService) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    //send the newly created request
    return next.handle(req).catch((error, caught) => {
      this.toast.error(this.getTitle(error));
      return Observable.throw(error);
    }) as any;
  }

  getTitle(error, defaultMessage = 'Error') {
    const title =
      get(error, 'error.error.message') ||
      get(error, 'error.message') ||
      get(error, 'error', '') ||
      get(error, 'message', '');
    /* prettier-ignore */
    return title ? truncate(title, {
      length: ERROR_TITLE_LENGTH
    }) : defaultMessage;
  }
}
