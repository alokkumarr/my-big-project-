import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {tap} from 'rxjs/operators/tap';
import { ToastService } from '../../../common/services/toastMessage.service';
import { HeaderProgressService } from '../../../common/services/header-progress.service';
import AppConfig from '../../../../../../../appConfig';

type RequestOptions = {
  toast?: {successMsg: string, errorMsg?: string},
  forWhat?: 'export' | 'user' | 'role' | 'privilege' | 'category';
}

const loginUrl = AppConfig.login.url;
const apiUrl = AppConfig.api.url;

@Injectable()
export class AdminService {

  constructor(
    private http: HttpClient,
    private _toastMessage: ToastService,
    private _headerProgress: HeaderProgressService
  ) {}

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
    }
  }

  request<T>(path, params, options: RequestOptions = {}) {
    const { toast, forWhat } = options
    this._headerProgress.show();
    return this.http.post<T>(`${this.getBaseUrl(forWhat)}/${this.getIntermediaryPath(forWhat)}${path}`, params)
      .pipe(
        tap(this.showToastMessageIfNeeded(toast))
      )
      .finally(() => this._headerProgress.hide());
  }

  getIntermediaryPath(forWhat) {
    switch (forWhat) {
    case 'export':
      return '';
    default:
      return 'auth/admin/cust/manage/';
    }
  }

  getBaseUrl(forWhat) {
    switch (forWhat) {
    case 'export':
      return apiUrl;
    default:
      return loginUrl;
    }
  }
}
