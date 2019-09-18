import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as get from 'lodash/get';
import * as ceil from 'lodash/ceil';
import * as fpGet from 'lodash/fp/get';
import { map } from 'rxjs/operators';

import APP_CONFIG from '../../../../../appConfig';
import { AlertConfig } from '../alerts.interface';

export const PROJECTID = 'workbench';
interface AllAlertsResponse {
  alertRuleDetailsList: any[];
  numberOfRecords: number;
}
@Injectable({
  providedIn: 'root'
})
export class ConfigureAlertService {
  public api = get(APP_CONFIG, 'api.url');

  constructor(public http: HttpClient) {}

  /**
   * Gets list of all datapods avialble for that particular project
   * Initial version of alert only supports with single artifact
   * So filtering the list with that condition.
   *
   * @returns {Observable<any>}
   * @memberof ConfigureAlertService
   */
  getListOfDatapods$(): Observable<any> {
    return this.http
      .get(`${this.api}/internal/semantic/md?projectId=${PROJECTID}`)
      .pipe(
        map(fpGet('contents.[0].ANALYZE'))
        // disabled because it didn't work with
        // https://sip-iot-dev-us.synchronoss.net/ environment
        // map((artifacts: Array<any>) =>
        //   artifacts.filter(
        //     artifact => artifact.repository && artifact.repository.length === 1
        //   )
        // )
      );
  }

  /**
   * Gets all metrics in a datapod and
   * filters non-numeric types
   *
   * @param {string} id
   * @returns {Observable<any>}
   * @memberof ConfigureAlertService
   */
  getDatapod$(id: string): Observable<any> {
    return this.http.get(`${this.api}/internal/semantic/${PROJECTID}/${id}`);
  }

  /**
   * Fetches all the supported operators
   *
   * @returns {Observable<any>}
   * @memberof ConfigureAlertService
   */
  getOperators(): Observable<any> {
    const endpoint = `${this.api}/alerts/operators`;
    return this.http.get(endpoint);
  }

  /**
   * Fetches all the supported aggregations
   *
   * @returns {Observable<any>}
   * @memberof ConfigureAlertService
   */
  getAggregations(): Observable<any> {
    const endpoint = `${this.api}/alerts/aggregations`;
    return this.http.get(endpoint);
  }

  /**
   * Used to add a new alert
   *
   * @param {AlertConfig} alertConfig
   * @returns {Observable<any>}
   * @memberof ConfigureAlertService
   */
  createAlert(alertConfig: AlertConfig): Observable<any> {
    const endpoint = `${this.api}/alerts`;
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
    const endpoint = `${this.api}/alerts/${id}`;
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
    const endpoint = `${this.api}/alerts/${id}`;
    return this.http.delete(endpoint);
  }

  /**
   * Returns all configured alerts definitions
   *
   * @returns {Observable<any>}
   * @memberof ConfigureAlertService
   */
  getAllAlerts(options): Promise<any> {
    options.skip = options.skip || 0;
    options.take = options.take || 10;
    const pageNumber = ceil(options.skip / options.take) + 1;
    const queryParams = `?pageNumber=${pageNumber}&pageSize=${options.take}`;
    const endpoint = `${this.api}/alerts${queryParams}`;
    return this.http.get<AllAlertsResponse>(endpoint).toPromise();
  }
}
