import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import AppConfig from '../../../../appConfig';
import { first, map, tap } from 'rxjs/operators';
import * as fpGet from 'lodash/fp/get';
import * as get from 'lodash/get';
import * as values from 'lodash/values';
import * as flatten from 'lodash/flatten';
import * as isUndefined from 'lodash/isUndefined';
import * as forEach from 'lodash/forEach';
import * as isEmpty from 'lodash/isEmpty';
import { Observable, of } from 'rxjs';
import {
  DSKFilterGroup,
  DSKSecurityGroup,
  DSKFilterField,
  DSKFilterBooleanCriteria
} from './../dsk-filter.model';
import * as uniqWith from 'lodash/uniqWith';

import { AnalyzeService } from './../../modules/analyze/services/analyze.service';
import {
  CUSTOM_DATE_PRESET_VALUE,
  DATE_TYPES,
  NUMBER_TYPES
} from './../../modules/analyze/consts';

const loginUrl = AppConfig.login.url;

export interface DskEligibleField {
  columnName: string;
  displayName: string;
}

@Injectable()
export class DskFiltersService {
  private dskEligibleFields: Array<DskEligibleField>;

  constructor(
    private _http: HttpClient,
    private analyzeService: AnalyzeService
  ) {}

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

  isDSKFilterValid(filter: DSKFilterGroup, isTopLevel = false, mode) {
    switch (mode) {
      case 'DSK':
        let condition;
        condition = filter.booleanQuery.length > 0;

        return (
          filter.booleanCriteria &&
          condition &&
          filter.booleanQuery.every(child => {
            if ((<DSKFilterGroup>child).booleanCriteria) {
              return this.isDSKFilterValid(<DSKFilterGroup>child, false, 'DSK');
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

      case 'ANALYZE':
        let areValid = true;
        const flattenedFilters = this.analyzeService.flattenAndFetchFilters(filter, []);
        forEach(
          flattenedFilters,
          ({
            type,
            model,
            isAggregationFilter,
            isRuntimeFilter,
            isGlobalFilter,
            isOptional
          }) => {
            if (!isRuntimeFilter && isGlobalFilter) {
              areValid = true;
            } else if (!model) {
              areValid = Boolean(
                false
                  ? isOptional && isRuntimeFilter
                  : isRuntimeFilter
              );
            } else if (NUMBER_TYPES.includes(type) || isAggregationFilter) {
              areValid = this.isNumberFilterValid(model);
            } else if (type === 'string') {
              areValid = this.isStringFilterValid(model);
            } else if (DATE_TYPES.includes(type)) {
              areValid = this.isDateFilterValid(model);
            }
            if (!areValid) {
              return false;
            }
          }
        );
        return areValid;
    }
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
        const statement = get(query, 'booleanCriteria');
        if (statement) {
          return `${pStart}${this.generatePreview(
            query as DSKFilterGroup,
            mode
          )}${pEnd}`;
        }

        const field = <DSKFilterField>query;
        const values = mode === 'DSK' ? field.model.values : get(field, 'model.modelValues') || get(field, 'model.value');
        if (isUndefined(values)) {
          return '';
        }
        return `${field.columnName} <span class="operator">${
          field.model.operator
        }</span> [${mode === 'DSK' ? values.join(', ') : values}]`;
      })
      .join(
        ` <strong class="bool-op">${filterGroup.booleanCriteria}</strong> `
      );
  }

  isStringFilterValid({ operator, modelValues }) {
    return Boolean(operator && !isEmpty(modelValues));
  }

  isNumberFilterValid({ operator, value, otherValue }) {
    switch (operator) {
      case 'BTW':
        return Boolean(isFinite(value) && isFinite(otherValue));
      default:
        return Boolean(isFinite(value));
    }
  }

  isDateFilterValid({ preset, lte, gte }) {
    switch (preset) {
      case CUSTOM_DATE_PRESET_VALUE:
        return Boolean(lte && gte);
      default:
        return Boolean(preset);
    }
  }

  changeIndexToNames(dskObject, source, target) {
    const convertToString = JSON.stringify(dskObject);
    const replaceIndex = convertToString.replace(/"booleanQuery":/g, `"${target}":`);
    const convertToJson = JSON.parse(replaceIndex);
    return convertToJson;
  }
}
