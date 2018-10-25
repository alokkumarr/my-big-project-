import { Injectable } from '@angular/core';
import { JwtService } from '../../../common/services/jwt.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import AppConfig from '../../../../../appConfig';

const loginUrl = AppConfig.login.url;

@Injectable()
export class UserAssignmentService {

  constructor(
    private _http: HttpClient,
    private _jwtService: JwtService
  ) {}

  getList(customerId) {
    console.log(customerId);
    return this.getRequest('auth/admin/user-assignments');
  }

  addSecurityGroup(data) {
    let requestBody = {};
    let path;
    switch (data.mode) {
    case 'create':
      requestBody = {
        description: data.description,
        securityGroupName: data.securityGroupName
      };
      path = 'auth/admin/security-groups';
      return this.postRequest(path, requestBody);
    case 'edit':
      path = `auth/admin/security-groups/${data.secGroupSysId}/name`;
      requestBody = [data.securityGroupName, data.description];
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
  }

  addAttributetoGroup(attribute, mode) {
    console.log(attribute);
    let path;
    path = `/auth/admin/security-groups/${attribute.secGroupSysId}/dsk-attribute-values`;
    switch (mode) {
    case 'create':
      return this.postRequest(path, attribute);
    case 'edit':
      const requestBody = {
        attributeName: attribute.attributeName,
        value: attribute.value
      };
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
    const request = {
      securityGroupName: requestBody.securityGroupName
    };
    const path = `auth/admin/users/${requestBody.userId}/security-group`;
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type':  'application/json',
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Headers': 'Origin, X-Requested-With, Content-Type, Accept',
        'Access-Control-Allow-Method': 'PUT'
      })
    };
    return this._http.put(`${loginUrl}/${path}`, request, httpOptions).toPromise();
  }

  getRequest(path) {
    return this._http.get(`${loginUrl}/${path}`).toPromise();
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
