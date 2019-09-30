import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import * as ceil from 'lodash/ceil';
import * as get from 'lodash/get';
import * as filter from 'lodash/filter';
import { map } from 'rxjs/operators';

import { GridPagingOptions, AlertFilterModel } from '../alerts.interface';
import AppConfig from '../../../../../appConfig';
import {
  AlertDateCount,
  AlertDateSeverity,
  AlertConfig
} from '../alerts.interface';

const apiUrl = AppConfig.api.url;

const getFiltersForBackend = (filters: AlertFilterModel[]) => {
  return filter(filters, ({ type, modelValues }) => {
    switch (type) {
      case 'string':
        const [value] = modelValues;
        return value;
    }
    return true;
  });
};

const getAlertCountPayload = (filters: AlertFilterModel[], groupBy: string) => {
  return {
    filters: getFiltersForBackend(filters),
    groupBy: groupBy
  };
};

@Injectable({
  providedIn: 'root'
})
export class AlertsService {
  constructor(public _http: HttpClient) {}

  getRequest(path) {
    return this._http.get(`${apiUrl}/${path}`);
  }

  getAlertsStatesForGrid(
    options: GridPagingOptions = {},
    dateFilters: AlertFilterModel[]
  ) {
    const [sort] = options.sort || [null];
    options.skip = options.skip || 0;
    options.take = options.take || 10;
    const pageNumber = ceil(options.skip / options.take) + 1;
    const basePath = `alerts/states`;
    const queryParams = `?pageNumber=${pageNumber}&pageSize=${options.take}`;
    const payload = { filters: getFiltersForBackend(dateFilters), sorts: [] };
    if (sort) {
      const { selector, desc } = sort;
      payload.sorts = [{ fieldName: selector, order: desc ? 'DESC' : 'ASC' }];
    }
    const url = `${apiUrl}/${basePath}${queryParams}`;
    return this._http
      .post(url, payload)
      .toPromise()
      .then(response => {
        const data = get(response, `alertStatesList`);
        const totalCount = get(response, `numberOfRecords`) || data.length;
        return { data, totalCount };
      });
  }

  getAllAttributeValues() {
    const url = `${apiUrl}/alerts/attributevalues`;
    return this._http.get<string[]>(url);
  }

  getAlertRuleDetails(id: number) {
    const url = `${apiUrl}/alerts/${id}`;
    return this._http
      .get<{ alert: AlertConfig; message: string }>(url)
      .pipe(map(({ alert }) => alert));
  }

  getAllAlertsCount(dateFilters: AlertFilterModel[]) {
    const url = `${apiUrl}/alerts/count`;
    const payload = getAlertCountPayload(dateFilters, 'date');

    return this._http.post<AlertDateCount[]>(url, payload);
  }

  getAllAlertsSeverity(dateFilters: AlertFilterModel[]) {
    const url = `${apiUrl}/alerts/count`;
    const payload = getAlertCountPayload(dateFilters, 'Severity');

    return this._http.post<AlertDateSeverity[]>(url, payload);
  }

  getAlertCountById(id, dateFilters: AlertFilterModel[]) {
    const url = `${apiUrl}/alerts/count?alertRuleId=${id}`;
    const payload = getAlertCountPayload(dateFilters, 'date');

    return this._http.post<AlertDateCount[]>(url, payload);
  }
}
