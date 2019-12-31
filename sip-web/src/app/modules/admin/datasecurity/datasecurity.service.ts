import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import AppConfig from '../../../../../appConfig';
import { first, map } from 'rxjs/operators';
import * as isUndefined from 'lodash/isUndefined';
import * as fpGet from 'lodash/fp/get';
import * as values from 'lodash/values';
import * as flatten from 'lodash/flatten';
import { Observable } from 'rxjs';
import {
  DSKFilterGroup,
  DSKSecurityGroup,
  DSKFilterBooleanCriteria,
  DSKFilterField
} from './dsk-filter.model';

const loginUrl = AppConfig.login.url;

export interface DskEligibleField {
  columnName: string;
  displayName: string;
}

@Injectable()
export class DataSecurityService {
  constructor(private _http: HttpClient) {}

  getList() {
    return this.getRequest('auth/admin/user-assignments').toPromise();
  }

  getFiltersFor(group: string): Observable<DSKFilterGroup> {
    return (<Observable<DSKSecurityGroup>>(
      this.getRequest(`auth/admin/dsk-security-groups/${group}`)
    )).pipe(map(data => data.dskAttributes));
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

  isDSKFilterValid(filter: DSKFilterGroup) {
    let condition;
    if (
      filter.booleanCriteria === DSKFilterBooleanCriteria.AND ||
      filter.booleanCriteria === DSKFilterBooleanCriteria.OR
    ) {
      // AND and OR need to have 2 or more operands
      condition = filter.booleanQuery && filter.booleanQuery.length >= 2;
    } else {
      // NOT needs a single operand
      condition = filter.booleanQuery && filter.booleanQuery.length === 1;
    }

    return (
      filter.booleanCriteria &&
      condition &&
      filter.booleanQuery.every(child => {
        if ((<DSKFilterGroup>child).booleanCriteria) {
          return this.isDSKFilterValid(<DSKFilterGroup>child);
        }

        const field = <DSKFilterField>child;
        return (
          field.columnName &&
          field.model &&
          field.model.values &&
          field.model.values.length > 0
        );
      })
    );
  }

  updateDskFiltersForGroup(groupId: string, filters: DSKFilterGroup) {
    const path = `auth/admin/dsk-security-groups/${groupId}`;
    return this.putrequest(path, filters);
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
