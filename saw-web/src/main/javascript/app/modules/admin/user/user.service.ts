import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import * as fpGet from 'lodash/fp/get';
import AppConfig from '../../../../../../../appConfig';

const loginUrl = AppConfig.login.url;

@Injectable()
export class UserService {

  constructor(
    private http: HttpClient
  ) {}

  getActiveUsersList(customerId) {
    return this.http.post<{users: any[]}>(`${loginUrl}/auth/admin/cust/manage/users/fetch`, customerId).map(resp => resp.users);
  }

  getRoles(customerId) {
    return this.http.post(`${loginUrl}/auth/admin/cust/manage/roles/list`, customerId).map(fpGet('data'));
  }

  saveUser(user) {
    return this.http.post(`${loginUrl}/auth/admin/cust/manage/users/add`, user).map(fpGet('data'));
  }

  deleteUser(user) {
    return this.http.post(`${loginUrl}/auth/admin/cust/manage/users/delete`, user).map(fpGet('data'));
  }

  updateUser(user) {
    return this.http.post(`${loginUrl}/auth/admin/cust/manage/users/edit`, user).map(fpGet('data'));
  }
}
