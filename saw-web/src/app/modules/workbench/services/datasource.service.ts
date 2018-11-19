import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { JwtService } from '../../../common/services';
import APP_CONFIG from '../../../../../appConfig';
import { of, Observable } from 'rxjs';
import * as get from 'lodash/get';

const userProject = 'workbench';

@Injectable({
  providedIn: 'root'
})
export class DatasourceService {
  public api = get(APP_CONFIG, 'api.url');

  constructor(public http: HttpClient, public jwt: JwtService) {}

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
}
