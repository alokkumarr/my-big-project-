import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import AppConfig from '../../../../../appConfig';
import { first, map } from 'rxjs/operators';
import * as isUndefined from 'lodash/isUndefined';
import * as fpGet from 'lodash/fp/get';
import * as values from 'lodash/values';
import * as flatten from 'lodash/flatten';
import { Observable } from 'rxjs';

const loginUrl = AppConfig.login.url;

export interface DskEligibleField {
  columnName: string;
  displayName: string;
}

@Injectable()
export class UserAssignmentService {
  constructor(private _http: HttpClient) {}

  getList() {
    return this.getRequest('auth/admin/user-assignments').toPromise();
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

  getEligibleDSKFieldsFor(
    customerId,
    productId
  ): Observable<Array<DskEligibleField>> {
    const path = 'auth/admin/dsk/fields';
    return this.getRequest(path).pipe(
      first(),
      map(fpGet(`dskEligibleData.${customerId}.${productId}`)),
      map((data: { [semanticId: string]: Array<DskEligibleField> }) =>
        flatten(values(data))
      )
    );
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
    return this.getRequest(
      `auth/admin/security-groups/${request.secGroupSysId}/dsk-attribute-values`
    ).toPromise();
  }

  getSecurityGroups() {
    return this.getRequest('auth/admin/security-groups').toPromise();
  }

  deleteGroupOrAttribute(path) {
    return this._http.delete(`${loginUrl}/${path}`).toPromise();
  }

  assignGroupToUser(requestBody) {
    const path = `auth/admin/users/${requestBody.userId}/security-group`;
    return this.putrequest(path, requestBody.securityGroupName);
  }

  getRequest(path) {
    return this._http.get(`${loginUrl}/${path}`);
  }

  putrequest(path, requestBody) {
    return this._http.put(`${loginUrl}/${path}`, requestBody).toPromise();
  }

  postRequest(path: string, params: Object) {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };
    return this._http
      .post(`${loginUrl}/${path}`, params, httpOptions)
      .toPromise();
  }
}
