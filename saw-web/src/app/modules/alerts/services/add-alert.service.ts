import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as get from 'lodash/get';
import * as fpGet from 'lodash/fp/get';
import { map as mapObservable } from 'rxjs/operators';

import APP_CONFIG from '../../../../../appConfig';
import { AlertConfig } from '../alerts.interface';

export const PROJECTID = 'workbench';

@Injectable({
  providedIn: 'root'
})
export class AddAlertService {
  public api = get(APP_CONFIG, 'api.url');

  constructor(public http: HttpClient) {}

  /**
   * Gets list of all datapods avialble for that particular project
   *
   * @returns {Observable<any>}
   * @memberof AddAlertService
   */
  getListOfDatapods$(): Observable<any> {
    return this.http
      .get(`${this.api}/internal/semantic/md?projectId=${PROJECTID}`)
      .pipe(mapObservable(fpGet('contents.[0].ANALYZE')));
  }

  /**
   * Gets all metrics in a datapod
   *
   * @param {string} id
   * @returns {Observable<any>}
   * @memberof AddAlertService
   */
  getMetricsInDatapod$(id: string): Observable<any> {
    return this.http
      .get(`${this.api}/internal/semantic/${PROJECTID}/${id}`)
      .pipe(mapObservable(fpGet('artifacts.[0].columns')));
  }

  createAlert(alertConfig: AlertConfig): Observable<any> {
    const endpoint = `${this.api}/sip/alerts`;
    return this.http.post(endpoint, alertConfig);
  }
}
