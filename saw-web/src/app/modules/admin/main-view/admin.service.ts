import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { ToastService } from '../../../common/services/toastMessage.service';
import AppConfig from '../../../../../appConfig';

interface RequestOptions {
  toast?: { successMsg: string; errorMsg?: string };
  forWhat?:
    | 'export'
    | 'import'
    | 'user'
    | 'role'
    | 'privilege'
    | 'category'
    | 'newScheme';
}

const loginUrl = AppConfig.login.url;
const apiUrl = AppConfig.api.url;

@Injectable()
export class AdminService {
  constructor(public http: HttpClient, public _toastMessage: ToastService) {}

  showToastMessageIfNeeded(toast) {
    return resp => {
      if (!toast) {
        return;
      }
      if (resp.valid) {
        this._toastMessage.success(toast.successMsg);
      } else {
        this._toastMessage.error(toast.errorMsg || resp.validityMessage);
      }
    };
  }

  getRequest<T>(path, options: RequestOptions = {}) {
    const { toast, forWhat } = options;
    return this.http
      .get<T>(
        `${this.getBaseUrl(forWhat)}/${this.getIntermediaryPath(
          forWhat
        )}${path}`
      )
      .pipe(tap(this.showToastMessageIfNeeded(toast)));
  }

  request<T>(path, params, options: RequestOptions = {}) {
    const { toast, forWhat } = options;
    return this.http
      .post<T>(
        `${this.getBaseUrl(forWhat)}/${this.getIntermediaryPath(
          forWhat
        )}${path}`,
        params
      )
      .pipe(tap(this.showToastMessageIfNeeded(toast)));
  }

  getIntermediaryPath(forWhat) {
    switch (forWhat) {
      case 'export':
      case 'import':
        return '';
      case 'newScheme':
        return 'auth/admin/';
      default:
        return 'auth/admin/cust/manage/';
    }
  }

  getBaseUrl(forWhat) {
    switch (forWhat) {
      case 'export':
      case 'import':
        return apiUrl;
      default:
        return loginUrl;
    }
  }
}
