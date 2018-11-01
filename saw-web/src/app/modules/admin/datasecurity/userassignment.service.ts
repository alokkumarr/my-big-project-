import { Injectable } from '@angular/core';
import { JwtService } from '../../../common/services/jwt.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import AppConfig from '../../../../../appConfig';
import * as isUndefined from 'lodash/isUndefined';

const loginUrl = AppConfig.login.url;

@Injectable()
export class UserAssignmentService {

  constructor(
    private _http: HttpClient,
    private _jwtService: JwtService
  ) {}

  getList(customerId) {
    return this.getRequest('auth/admin/user-assignments');
  }

  addSecurityGroup(data) {
    let path;
    switch (data.mode) {
    case 'create':
      const requestCreateBody = {
        description: isUndefined(data.description) ? '' : data.description,
        securityGroupName: data.securityGroupName
      };
      path = 'auth/admin/security-groups';
      return this.postRequest(path, requestCreateBody);
    case 'edit':
      path = `auth/admin/security-groups/${data.secGroupSysId}/name`;
      const requestEditBody = [data.securityGroupName, data.description];
      return this.putrequest(path, requestEditBody);
    }
  }

  attributetoGroup(data) {
    const requestBody = {
      attributeName: data.attributeName.trim(),
      value: data.value
    };
    const path = `auth/admin/security-groups/${data.groupSelected.secGroupSysId}/dsk-attribute-values`;
    switch (data.mode) {
    case 'create':
      return this.postRequest(path, requestBody);
    case 'edit':
      return this.putrequest(path, requestBody);
    }
  }

  getSecurityAttributes(request) {
    return this.getRequest(`auth/admin/security-groups/${request.secGroupSysId}/dsk-attribute-values`);
  }

  getSecurityGroups() {
    return this.getRequest('auth/admin/security-groups');
  }

  deleteGroupOrAttribute(path) {
    return this._http.delete(`${loginUrl}/${path}`).toPromise();
  }

  assignGroupToUser(requestBody) {
    const path = `auth/admin/users/${requestBody.userId}/security-group`;
    return this.putrequest(path, requestBody.securityGroupName);
  }

  getRequest(path) {
    return this._http.get(`${loginUrl}/${path}`).toPromise();
  }

  putrequest(path, requestBody) {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type':  'application/json',
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Headers': 'Origin, X-Requested-With, Content-Type, Accept',
        'Access-Control-Allow-Method': 'PUT'
      })
    };
    return this._http.put(`${loginUrl}/${path}`, requestBody, httpOptions).toPromise();
  }

  postRequest(path: string, params: Object) {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type':  'application/json'
      })
    };
    return this._http.post(`${loginUrl}/${path}`, params, httpOptions).toPromise();
  }
}
