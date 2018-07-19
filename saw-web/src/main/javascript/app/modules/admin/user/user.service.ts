import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {tap} from 'rxjs/operators/tap';
import {map} from 'rxjs/operators/map';
import { ToastService } from '../../../common/services/toastMessage.service';
import { HeaderProgressService } from '../../../common/services/header-progress.service';
import AppConfig from '../../../../../../../appConfig';

type UserResponse = {
  users: any[],
  valid: boolean,
  validityMessage: string
};

type RolesResponse = {
  roles: any[],
  valid: boolean,
  validityMessage: string
};

const loginUrl = AppConfig.login.url;

@Injectable()
export class UserService {

  constructor(
    private http: HttpClient,
    private _toastMessage: ToastService,
    private _headerProgress: HeaderProgressService
  ) {}

  getActiveUsersList(customerId) {
    return this.request<UserResponse>('users/fetch', customerId)
      .map(resp => resp.users);
  }

  getRoles(customerId) {
    return this.request<RolesResponse>('roles/list', customerId)
      .map(resp => resp.roles);
  }

  saveUser(user) {
    return this.request<UserResponse>('users/add', user)
      .pipe(
        tap(this.showToastMessage('User is successfully added')),
        map(resp => resp.valid ? resp.users : null)
      ).toPromise();
  }

  deleteUser(user) {
    return this.request<UserResponse>('users/delete', user)
      .pipe(
        tap(this.showToastMessage('User is successfully deleted')),
        map(resp => resp.valid ? resp.users : null)
      ).toPromise();
  }

  updateUser(user) {
    return this.request<UserResponse>('users/edit', user)
    .pipe(
      tap(this.showToastMessage('User is successfully Updated')),
      map(resp => resp.valid ? resp.users : null)
    ).toPromise();
  }

  showToastMessage(successMessage) {
    return resp => {
      if (resp.valid) {
        this._toastMessage.success(successMessage);
      } else {
        this._toastMessage.error(resp.validityMessage);
      }
    }
  }

  request<T>(path, params) {
    this._headerProgress.show();
    return this.http.post<T>(`${loginUrl}/auth/admin/cust/manage/${path}`, params)
      .pipe(tap(() => this._headerProgress.hide()))
      .finally(() => this._headerProgress.hide());
  }
}
