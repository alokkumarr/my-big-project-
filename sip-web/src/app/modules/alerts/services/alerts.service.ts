import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import * as ceil from 'lodash/ceil';
import * as get from 'lodash/get';
import { map } from 'rxjs/operators';

import { GridPagingOptions, AlertFilterModel } from '../alerts.interface';
import AppConfig from '../../../../../appConfig';
import { CUSTOM_DATE_PRESET_VALUE } from '../consts';
import {
  AlertDateCount,
  AlertDateSeverity,
  AlertConfig
} from '../alerts.interface';

const apiUrl = AppConfig.api.url;

const getAlertCountPayload = (
  dateFilter: AlertFilterModel,
  groupBy: string
) => {
  const { preset, startTime, endTime } = dateFilter;
  if (dateFilter.preset === CUSTOM_DATE_PRESET_VALUE) {
    return {
      preset,
      startTime,
      endTime,
      groupBy
    };
  }
  return {
    preset,
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

  getAlertsStatesForGrid(options: GridPagingOptions = {}) {
    options.skip = options.skip || 0;
    options.take = options.take || 10;
    const pageNumber = ceil(options.skip / options.take) + 1;

    const basePath = `alerts/states`;
    const queryParams = `?pageNumber=${pageNumber}&pageSize=${options.take}`;
    const url = `${basePath}${queryParams}`;

    return this.getRequest(url)
      .toPromise()
      .then(response => {
        const data = get(response, `alertStatesList`);
        const totalCount = get(response, `numberOfRecords`) || data.length;
        return { data, totalCount };
      });
  }

  getAlertRuleDetails(id: number) {
    const url = `${apiUrl}/alerts/${id}`;
    return this._http
      .get<{ alert: AlertConfig; message: string }>(url)
      .pipe(map(({ alert }) => alert));
  }

  getAllAlertsCount(dateFilter: AlertFilterModel) {
    const url = `${apiUrl}/alerts/count`;
    const payload = getAlertCountPayload(dateFilter, 'StartTime');

    return this._http.post<AlertDateCount[]>(url, payload);
  }

  getAllAlertsSeverity(dateFilter: AlertFilterModel) {
    const url = `${apiUrl}/alerts/count`;
    const payload = getAlertCountPayload(dateFilter, 'Severity');

    return this._http.post<AlertDateSeverity[]>(url, payload);
  }

  getAlertCountById(id, dateFilter: AlertFilterModel) {
    const url = `${apiUrl}/alerts/count?alertRuleId=${id}`;
    const payload = getAlertCountPayload(dateFilter, 'StartTime');

    return this._http.post<AlertDateCount[]>(url, payload);
  }
}
