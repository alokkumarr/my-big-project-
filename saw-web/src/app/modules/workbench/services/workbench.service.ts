import * as fpGet from 'lodash/fp/get';
import * as forEach from 'lodash/forEach';
import * as isUndefined from 'lodash/isUndefined';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { JwtService } from '../../../common/services';

import { SQLEXEC_SAMPLE, ARTIFACT_SAMPLE } from '../sample-data';

import APP_CONFIG from '../../../../../appConfig';

const userProject = 'workbench';

@Injectable()
export class WorkbenchService {
  public api = fpGet('api.url', APP_CONFIG);
  public wbAPI = `${this.api}/internal/workbench/projects`;

  constructor(
    public http: HttpClient,
    public jwt: JwtService,
    public router: Router
  ) {}

  /** GET datasets from the server */
  getDatasets(): Observable<any> {
    const endpoint = `${this.wbAPI}/${userProject}/datasets`;
    return this.http
      .get(endpoint)
      .pipe(catchError(this.handleError('data', [])));
  }

  /** GET Staging area tree list */
  getStagingData(path: string): Observable<any> {
    const endpoint = `${this.wbAPI}/${userProject}/raw/directory`;
    return this.http
      .post(endpoint, { path })
      .pipe(catchError(this.handleError('data', [])));
  }

  /** GET raw preview from the server */
  getRawPreviewData(path: string): Observable<any> {
    const endpoint = `${this.wbAPI}/${userProject}/raw/directory/preview`;
    return this.http
      .post(endpoint, { path })
      .pipe(catchError(this.handleError('data', [])));
  }

  /** GET parsed preview from the server */
  getParsedPreviewData(previewConfig): Observable<any> {
    const endpoint = `${this.wbAPI}/${userProject}/raw/directory/inspect`;
    return this.http.post(endpoint, previewConfig).pipe(
      catchError((e: any) => {
        return Observable.of(e);
      })
    );
  }

  /** File mask search */
  filterFiles(mask, temmpFiles) {
    const selFiles = [];
    if (isUndefined(mask)) {
      return;
    }
    let wildcardSearch: any;
    if (this.startsWith(mask, '*')) {
      wildcardSearch = this.endsWith;
    } else if (this.endsWith(mask, '*')) {
      wildcardSearch = this.startsWith;
    } else {
      wildcardSearch = this.exactMatch;
    }

    const filemasksearch = mask.replace('*', '');
    for (let fileCounter = 0; fileCounter < temmpFiles.length; fileCounter++) {
      if (wildcardSearch(temmpFiles[fileCounter].name, filemasksearch)) {
        selFiles.push(temmpFiles[fileCounter]);
      }
    }

    return selFiles;
  }

  // string functions for filemask wild card search
  endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
  }

  startsWith(str, suffix) {
    return str.indexOf(suffix) === 0;
  }

  exactMatch(str, suffix) {
    return str === suffix;
  }

  uploadFile(filesToUpload: FileList, path: string): Observable<any> {
    const endpoint = `${this.wbAPI}/${userProject}/raw/directory/upload/files`;
    const headers = new HttpHeaders();
    headers.set('Content-Type', null);
    headers.set('Accept', 'multipart/form-data');

    const params = new HttpParams();
    const formData: FormData = new FormData();
    forEach(filesToUpload, file => {
      formData.append('files', file, file.name);
    });
    formData.append('path', path);
    return this.http.post(endpoint, formData, { params, headers });
  }

  validateMaxSize(fileList: FileList) {
    let size = 0;
    const maxSize = 26214400;
    forEach(fileList, file => {
      size += file.size;
    });
    if (size > maxSize) {
      return false;
    }
    return true;
  }

  validateFileTypes(fileList: FileList) {
    let isValid = true;
    forEach(fileList, file => {
      const fileName = file.name;
      const ext = fileName.substring(fileName.lastIndexOf('.') + 1);
      if (ext.toLowerCase() !== 'csv' && ext.toLowerCase() !== 'txt') {
        isValid = false;
      }
    });
    return isValid;
  }

  createFolder(path: string): Observable<any> {
    const endpoint = `${this.wbAPI}/${userProject}/raw/directory/create`;
    return this.http
      .post(endpoint, { path })
      .pipe(catchError(this.handleError('data', [])));
  }
  /**
   * Service to fetch meta data of a dataset
   *
   * @param {string} projectName
   * @param {any} id
   * @returns {Observable<any>}
   * @memberof WorkbenchService
   */
  getDatasetDetails(id): Observable<any> {
    const endpoint = `${this.wbAPI}/${userProject}/${id}`;
    return this.http
      .get(endpoint)
      .pipe(catchError(this.handleError('data', ARTIFACT_SAMPLE)));
  }

  triggerParser(payload) {
    const endpoint = `${this.wbAPI}/${userProject}/datasets`;
    return this.http
      .post(endpoint, payload)
      .pipe(catchError(this.handleError('data', {})));
  }
  /**
   * Following 3 functions
   * To store, retrive and remove data from localstorage
   *
   * @param {any} metadata
   * @memberof WorkbenchService
   */
  setDataToLS(key, value) {
    localStorage.setItem(key, JSON.stringify(value));
  }

  getDataFromLS(key) {
    const dsMetada = JSON.parse(localStorage.getItem(key));
    return dsMetada;
  }

  removeDataFromLS(key) {
    localStorage.removeItem(key);
  }

  /**
   * Calls the sql executor component and fetches the output as data for preview grid
   *
   * @param {string} query
   * @returns {Observable<any>}
   * @memberof WorkbenchService
   */
  executeSqlQuery(query: string): Observable<any> {
    const endpoint = `${this.wbAPI}/execute`;
    return this.http
      .post(endpoint, { query })
      .pipe(catchError(this.handleError('data', SQLEXEC_SAMPLE)));
  }

  navigateToDetails(metadata) {
    this.setDataToLS('dsMetadata', metadata);
    this.router.navigate(['workbench', 'dataset', 'details']);
  }

  triggerDatasetPreview(name: string): Observable<any> {
    const endpoint = `${this.wbAPI}/${userProject}/previews`;
    return this.http
      .post(endpoint, { name })
      .pipe(catchError(this.handleError('data', {})));
  }

  getDatasetPreviewData(id): Observable<any> {
    const endpoint = `${this.wbAPI}/${userProject}/previews/${id}`;
    return this.http.get(endpoint).pipe(
      catchError((e: any) => {
        return Observable.of(e);
      })
    );
  }

  createSemantic(payload) {
    const endpoint = `${this.api}/internal/semantic/${userProject}/create`;
    payload.customerCode = this.jwt.customerCode;
    payload.username = this.jwt.getUserName();
    payload.projectCode = userProject;
    return this.http
      .post(endpoint, payload)
      .pipe(catchError(this.handleError('data', {})));
  }

  /**
   * Gets list of all datapods avialble in semantic store for that particular project
   *
   * @returns
   * @memberof WorkbenchService
   */
  getListOfSemantic() {
    return this.http
      .get(`${this.api}/internal/semantic/md?projectId=${userProject}`)
      .pipe(catchError(this.handleError('data', {})));
  }

  /**
   * Gets the detailed definition of particular semantic ID
   *
   * @param {*} id
   * @returns
   * @memberof WorkbenchService
   */
  getSemanticDetails(id) {
    return this.http
      .get(`${this.api}/internal/semantic/${userProject}/${id}`)
      .pipe(catchError(this.handleError('data', {})));
  }

  /**
   * Updates the definition of particular semantic ID
   *
   * @param {*} payload
   * @returns
   * @memberof WorkbenchService
   */
  updateSemanticDetails(payload) {
    const semID = payload.id;
    const endpoint = `${this.api}/internal/semantic/${userProject}/${semID}`;
    payload.updatedBy = this.jwt.getUserName();

    return this.http
      .put(endpoint, payload)
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
