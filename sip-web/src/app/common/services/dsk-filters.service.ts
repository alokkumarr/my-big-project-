import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import AppConfig from '../../../../appConfig';
import { first, map, tap } from 'rxjs/operators';
import * as fpGet from 'lodash/fp/get';
import * as get from 'lodash/get';
import * as values from 'lodash/values';
import * as flatten from 'lodash/flatten';
import * as isUndefined from 'lodash/isUndefined';
import { Observable, of } from 'rxjs';
import {
  DSKFilterGroup,
  DSKSecurityGroup,
  DSKFilterField,
  DSKFilterBooleanCriteria
} from './../dsk-filter.model';
import * as uniqWith from 'lodash/uniqWith';

const loginUrl = AppConfig.login.url;

export interface DskEligibleField {
  columnName: string;
  displayName: string;
}

@Injectable()
export class DskFiltersService {
  private dskEligibleFields: Array<DskEligibleField>;

  constructor(private _http: HttpClient) {}

  getFiltersFor(group: string): Observable<DSKFilterGroup> {
    return (<Observable<DSKSecurityGroup>>(
      this.getRequest(`auth/admin/v1/dsk-security-groups/${group}`)
    )).pipe(map(data => data.dskAttributes));
  }

  clearDSKEligibleFields() {
    this.dskEligibleFields = null;
  }

  getEligibleDSKFieldsFor(
    customerId,
    productId
  ): Observable<Array<DskEligibleField>> {
    if (this.dskEligibleFields) {
      return of(this.dskEligibleFields);
    }
    const path = 'auth/admin/dsk/fields';
    return this.getRequest(path).pipe(
      first(),
      map(fpGet(`dskEligibleData.${customerId}.${productId}`)),
      map((data: { [semanticId: string]: Array<DskEligibleField> }) =>
        flatten(values(data))
      ),
      /* Remove keyword */
      map(fields =>
        (fields || []).map(field => ({
          ...field,
          columnName: (field.columnName || '').replace('.keyword', '')
        }))
      ),
      /* Take only unique fields. If two fields match in both -
      display and column names, no need to take both */
      map(fields =>
        uniqWith(
          fields,
          (field1, field2) =>
            field1.columnName === field2.columnName &&
            field1.displayName === field2.displayName
        )
      ),
      tap(eligibleFields => {
        this.dskEligibleFields = eligibleFields;
      })
    );
  }

  isDSKFilterValid(filter: DSKFilterGroup, isTopLevel = false) {
    let condition;
    return true;
    condition = filter.booleanQuery.length > 0;

    return (
      filter.booleanCriteria &&
      condition &&
      filter.booleanQuery.every(child => {
        if ((<DSKFilterGroup>child).booleanCriteria) {
          return this.isDSKFilterValid(<DSKFilterGroup>child, false);
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
    const path = `auth/admin/v1/dsk-security-groups/${groupId}`;
    return this.putrequest(path, filters);
  }

  deleteDskFiltersForGroup(groupId: string): Promise<any> {
    return this.updateDskFiltersForGroup(groupId, {
      booleanCriteria: DSKFilterBooleanCriteria.AND,
      booleanQuery: []
    });
  }

  getRequest(path) {
    return this._http.get(`${loginUrl}/${path}`);
  }

  putrequest(path, requestBody) {
    return this._http.put(`${loginUrl}/${path}`, requestBody).toPromise();
  }

  deleteRequest(path) {
    return this._http.delete(`${loginUrl}/${path}`);
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

  generatePreview(filterGroup: DSKFilterGroup, mode: string): string {
    const pStart = '<strong class="parens">(</strong>';
    const pEnd = '<strong class="parens">)</strong>';
    return filterGroup.booleanQuery
      .map(query => {
        console.log(query);
        console.log(query['booleanCriteria']);
        const statement = mode === 'ANALYZE' ? get(query, 'booleanCriteria') : query['booleanCriteria'];
        if (statement) {
          return `${pStart}${this.generatePreview(
            query as DSKFilterGroup,
            mode
          )}${pEnd}`;
        }

        const field = <DSKFilterField>query;
        const values = field.model.values || get(field, 'model.modelValues');
        if (isUndefined(values)) {
          return '';
        }
        console.log(values);
        return `${field.columnName} <span class="operator">${
          field.model.operator
        }</span> [${values.join(', ')}]`;
      })
      .join(
        ` <strong class="bool-op">${filterGroup.booleanCriteria}</strong> `
      );
  }
}
