import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, map } from 'rxjs/operators';
import { JwtService } from '../../../common/services';
import APP_CONFIG from '../../../../../appConfig';
import { of, Observable } from 'rxjs';
import * as get from 'lodash/get';
import * as lodashMap from 'lodash/map';
import {
  Job,
  ChannelForJobs,
  RouteForJobs,
  JobLog
} from '../models/workbench.interface';

interface JobsResponse {
  jobDetails: Job[];
  numOfPages: number;
  totalRows: number;
}

interface JobLogsResponse {
  bisFileLogs: JobLog[];
  numOfPages: number;
  totalRows: number;
}

const userProject = 'workbench';

@Injectable({
  providedIn: 'root'
})
export class DatasourceService {
  public api = get(APP_CONFIG, 'api.url');
  constructor(public http: HttpClient, public jwt: JwtService) {
    this.isDuplicateChannel = this.isDuplicateChannel.bind(this);
    this.isDuplicateRoute = this.isDuplicateRoute.bind(this);
  }

  /**
   * Get list of all data sources
   *
   * @returns
   * @memberof DatasourceService
   */
  getSourceList(): Observable<any> {
    // This API supports for BE pagination. But as the channel number won't be huge, setting it to 10,000;
    // Refer to JIRA ID: SIP-4615 if more info needed about this API support.
    return this.http
      .get(`${this.api}/ingestion/batch/channels?size=10000`)
      .pipe(catchError(this.handleError('data', {})));
  }

  /**
   * Get list of all routes in a source
   *
   * @returns
   * @memberof DatasourceService
   */
  getRoutesList(channelID): Observable<any> {
    // This API supports for BE pagination.
    // Refer to JIRA ID: SIP-4615 if more info needed about this API support.
    return this.http
      .get(
        `${this.api}/ingestion/batch/channels/${channelID}/routes?size=10000`
      )
      .pipe(catchError(this.handleError('data', {})));
  }

  /**
   * Get logs of a route
   *
   * @returns
   * @memberof DatasourceService
   */
  getRoutesLogs(channelID, routeID): Observable<any> {
    // This API supports for BE pagination.
    // Refer to JIRA ID: SIP-4615 if more info needed about this API support.
    return this.http
      .get(`${this.api}/ingestion/batch/logs/${channelID}/${routeID}`)
      .pipe(catchError(this.handleError('data', {})));
  }

  isDuplicateChannel(channelName): Observable<boolean> {
    const endpoint = `${
      this.api
    }/ingestion/batch/channels/duplicate?channelName=${channelName}`;

    return this.http.get(endpoint).pipe(
      map(data => get(data, 'isDuplicate') as boolean),
      catchError(this.handleError('data', false))
    );
  }

  activateRoute(channelId, routeId) {
    return this.toggleRoute(channelId, routeId, false);
  }

  deActivateRoute(channelId, routeId) {
    return this.toggleRoute(channelId, routeId, false);
  }

  toggleRoute(channelId, routeId, activate: boolean) {
    const endpoint = `${
      this.api
    }/ingestion/batch/channels/${channelId}/routes/${routeId}/${
      activate ? 'activate' : 'deactivate'
    }`;
    const payload = {
      channelId,
      routeId
    };
    return this.http
      .put(endpoint, payload)
      .pipe(catchError(this.handleError('data', {})));
  }

  activateChannel(channelId) {
    return this.toggleChannel(channelId, true);
  }

  deActivateChannel(channelId) {
    return this.toggleChannel(channelId, false);
  }

  toggleChannel(channelId, activate: boolean) {
    const endpoint = `${this.api}/ingestion/batch/channels/${channelId}/${
      activate ? 'activate' : 'deactivate'
    }`;
    const payload = {
      channelId
    };
    return this.http
      .put(endpoint, payload)
      .pipe(catchError(this.handleError('data', {})));
  }

  isDuplicateRoute({ channelId, routeName }): Observable<boolean> {
    const endpoint = `${
      this.api
    }/ingestion/batch/channels/${channelId}/duplicate-route?routeName=${routeName}`;

    return this.http.get(endpoint).pipe(
      map(data => get(data, 'isDuplicate') as boolean),
      catchError(this.handleError('data', false))
    );
  }

  /**
   * Delete a channel by ID
   *
   * @returns
   * @memberof DatasourceService
   */
  deleteChannel(channelID): Observable<any> {
    return this.http
      .delete(`${this.api}/ingestion/batch/channels/${channelID}`)
      .pipe(catchError(this.handleError('data', {})));
  }

  /**
   * Updates a Source
   *
   * @param {*} payload
   * @returns
   * @memberof DatasourceService
   */
  updateSource(channelID, payload): Observable<any> {
    payload.modifiedBy = this.jwt.getUserName();
    const endpoint = `${this.api}/ingestion/batch/channels/${channelID}`;

    return this.http
      .put(endpoint, payload)
      .pipe(catchError(this.handleError('data', {})));
  }

  /**
   * Creates a Source entry in Batch Ingestion Service
   *
   * @param {*} payload
   * @returns
   * @memberof DatasourceService
   */
  createSource(payload): Observable<any> {
    payload.customerCode = this.jwt.customerCode;
    payload.createdBy = this.jwt.getUserName();
    payload.projectCode = userProject;
    payload.productCode = this.jwt.getProductName();

    const endpoint = `${this.api}/ingestion/batch/channels`;

    return this.http
      .post(endpoint, payload)
      .pipe(catchError(this.handleError('data', {})));
  }

  /**
   * Creates a Route entry for a Channel in Batch Ingestion Service
   *
   * @param {*} payload
   * @returns
   * @memberof DatasourceService
   */
  createRoute(channelID, payload): Observable<any> {
    payload.createdBy = this.jwt.getUserName();

    const endpoint = `${this.api}/ingestion/batch/channels/${channelID}/routes`;

    return this.http
      .post(endpoint, payload)
      .pipe(catchError(this.handleError('data', {})));
  }

  /**
   * Updates a Route
   *
   * @param {*} payload
   * @returns
   * @memberof DatasourceService
   */
  updateRoute(channelID, routeID, payload): Observable<any> {
    payload.modifiedBy = this.jwt.getUserName();

    const endpoint = `${
      this.api
    }/ingestion/batch/channels/${channelID}/routes/${routeID}`;

    return this.http
      .put(endpoint, payload)
      .pipe(catchError(this.handleError('data', {})));
  }

  /**
   * Delete a Route by ID
   *
   * @returns
   * @memberof DatasourceService
   */
  deleteRoute(channelID, routeID): Observable<any> {
    return this.http
      .delete(
        `${this.api}/ingestion/batch/channels/${channelID}/routes/${routeID}`
      )
      .pipe(catchError(this.handleError('data', {})));
  }

  /**
   * Encrypts a given password with a key
   *
   * @param {*} payload
   * @returns {Observable<any>}
   * @memberof DatasourceService
   */
  encryptPWD(payload): Observable<any> {
    const endpoint = `${this.api}/ingestion/batch/internal/encrypt`;

    return this.http
      .post(endpoint, JSON.stringify(payload))
      .pipe(catchError(this.handleError('data', {})));
  }

  /**
   * Decrypts a given password
   *
   * @param {*} payload
   * @returns {Observable<any>}
   * @memberof DatasourceService
   */
  decryptPWD(payload): Observable<any> {
    const endpoint = `${this.api}/ingestion/batch/internal/decrypt`;

    return this.http
      .post(endpoint, JSON.stringify(payload))
      .pipe(catchError(this.handleError('data', {})));
  }

  /**
   * Test connectivity for a channel
   *
   * @param {*} channelID
   * @returns {Observable<any>}
   * @memberof DatasourceService
   */
  testChannel(channelID): Observable<any> {
    return this.http
      .get(`${this.api}/ingestion/batch/channels/${channelID}/status`)
      .pipe(catchError(this.handleError('data', {})));
  }

  /**
   * Test connectivity for a route
   *
   * @param {*} routeID
   * @returns {Observable<any>}
   * @memberof DatasourceService
   */
  testRoute(routeID): Observable<any> {
    return this.http
      .get(`${this.api}/ingestion/batch/routes/${routeID}/status`)
      .pipe(catchError(this.handleError('data', {})));
  }

  /**
   * Check channel connection using config
   *
   * @param {*} payload
   * @returns {Observable<any>}
   * @memberof DatasourceService
   */
  testChannelWithBody(payload): Observable<any> {
    const endpoint = `${this.api}/ingestion/batch/channels/test`;

    return this.http
      .post(endpoint, payload)
      .pipe(catchError(this.handleError('data', {})));
  }

  /**
   * Check route connection using config
   *
   * @param {*} payload
   * @returns {Observable<any>}
   * @memberof DatasourceService
   */
  testRouteWithBody(payload): Observable<any> {
    const endpoint = `${this.api}/ingestion/batch/routes/test`;

    return this.http
      .post(endpoint, payload)
      .pipe(catchError(this.handleError('data', {})));
  }

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  public handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      return of(result as T);
    };
  }

  public getJobLogs(jobId, pagination) {
    const url = `${this.api}/ingestion/batch/logs/job/${jobId}?${pagination}`;
    return this.http.get<JobLogsResponse>(url);
  }

  public getJobs(path) {
    const url = `${this.api}/ingestion/batch/logs/jobs/${path}`;
    return this.http.get<JobsResponse>(url).toPromise();
  }

  public getJobById(id: string) {
    const url = `${this.api}/ingestion/batch/logs/jobs/${id}`;
    return this.http.get<Job>(url).toPromise();
  }

  public getJobsByChannelId(channelId) {
    const url = `${
      this.api
    }/ingestion/batch/logs/jobs/channels/${channelId}?offset=0`;
    return this.http.get<Job[]>(url);
  }

  public getJobsByRouteName(channelId) {
    const url = `${
      this.api
    }/ingestion/batch/logs/jobs/${channelId}/{routeId}?offset=0`;
    return this.http.get<Job[]>(url);
  }

  public getChannelListForJobs(): Observable<ChannelForJobs[]> {
    return this.getSourceList().pipe(
      map(channels =>
        lodashMap(channels, channel => {
          const { channelName } = JSON.parse(channel.channelMetadata);
          return {
            id: channel.bisChannelSysId,
            name: channelName
          };
        })
      )
    );
  }

  public getRouteListForJobs(channelId): Observable<RouteForJobs[]> {
    return this.getRoutesList(channelId).pipe(
      map(routes =>
        lodashMap(routes, route => {
          const { routeName } = JSON.parse(route.routeMetadata);
          return {
            id: route.bisRouteSysId,
            name: routeName
          };
        })
      )
    );
  }
}
