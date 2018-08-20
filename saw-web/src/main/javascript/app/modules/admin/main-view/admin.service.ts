import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {tap} from 'rxjs/operators/tap';
import { ToastService } from '../../../common/services/toastMessage.service';
import { HeaderProgressService } from '../../../common/services/header-progress.service';
import AppConfig from '../../../../../../../appConfig';

type RequestOptions = {
  toast?: {successMsg: string, errorMsg?: string}
}

const loginUrl = AppConfig.login.url;

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
    const { toast } = options
    this._headerProgress.show();
    const headers = new HttpHeaders().set('Content-Type', 'application/json');
    return this.http.post<T>(`${loginUrl}/auth/admin/cust/manage/${path}`, params, {headers})
      .pipe(
        tap(this.showToastMessageIfNeeded(toast)),
        tap(() => this._headerProgress.hide())
      )
      .finally(() => this._headerProgress.hide());
  }
}
