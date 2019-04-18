import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as get from 'lodash/get';
import * as fpGet from 'lodash/fp/get';
import { map } from 'rxjs/operators';

import APP_CONFIG from '../../../../../appConfig';
import { AlertConfig, DatapodMetric } from '../alerts.interface';

export const PROJECTID = 'workbench';

@Injectable({
  providedIn: 'root'
})
export class ConfigureAlertService {
  public api = get(APP_CONFIG, 'api.url');

  constructor(public http: HttpClient) {}

  /**
   * Gets list of all datapods avialble for that particular project
   *
   * @returns {Observable<any>}
   * @memberof ConfigureAlertService
   */
  getListOfDatapods$(): Observable<any> {
    return this.http
      .get(`${this.api}/internal/semantic/md?projectId=${PROJECTID}`)
      .pipe(map(fpGet('contents.[0].ANALYZE')));
  }

  /**
   * Gets all metrics in a datapod and
   * filters non-numeric types
   *
   * @param {string} id
   * @returns {Observable<any>}
   * @memberof ConfigureAlertService
   */
  getMetricsInDatapod$(id: string): Observable<any> {
    const NON_NUMERIC_TYPES = ['string', 'date'];
    return this.http
      .get(`${this.api}/internal/semantic/${PROJECTID}/${id}`)
      .pipe(
        map(fpGet('artifacts.[0].columns')),
        map((columns: Array<DatapodMetric>) =>
          columns.filter(col => !NON_NUMERIC_TYPES.includes(col.type))
        )
      );
  }

  /**
   * Used to add a new alert
   *
   * @param {AlertConfig} alertConfig
   * @returns {Observable<any>}
   * @memberof ConfigureAlertService
   */
  createAlert(alertConfig: AlertConfig): Observable<any> {
    const endpoint = `${this.api}/sip/alerts`;
    return this.http.post(endpoint, alertConfig);
  }

  /**
   * Update the corresponding alert definition
   * matching the ID
   *
   * @param {string} id
   * @param {AlertConfig} alertConfig
   * @returns {Observable<any>}
   * @memberof ConfigureAlertService
   */
  updateAlert(id: string, alertConfig: AlertConfig): Observable<any> {
    const endpoint = `${this.api}/sip/alerts/${id}`;
    return this.http.put(endpoint, alertConfig);
  }

  /**
   * Deletes the alert with given ID
   *
   * @param {string} id
   * @returns {Observable<any>}
   * @memberof ConfigureAlertService
   */
  deleteAlert(id: string): Observable<any> {
    const endpoint = `${this.api}/sip/alerts/${id}`;
    return this.http.delete(endpoint);
  }

  /**
   * Returns all configured alerts definitions
   *
   * @returns {Observable<any>}
   * @memberof ConfigureAlertService
   */
  getAllAlerts(): Observable<any> {
    const endpoint = `${this.api}/sip/alerts`;
    return this.http.get(endpoint);
  }
}
