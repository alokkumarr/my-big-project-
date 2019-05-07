import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import * as floor from 'lodash/floor';
import * as get from 'lodash/get';
import * as fpGet from 'lodash/fp/get';
import { map } from 'rxjs/operators';

import { GridPagingOptions } from '../alerts.interface';
import AppConfig from '../../../../../appConfig';

const apiUrl = AppConfig.api.url;
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
    const pageNumber = floor(options.skip / options.take);

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
    const url = `alerts/${id}`;
    return this.getRequest(url).pipe(map(fpGet('alert')));
  }
}
