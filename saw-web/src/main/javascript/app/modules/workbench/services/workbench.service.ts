import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { UIRouter } from '@uirouter/angular';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { catchError } from 'rxjs/operators';

import { DATASETS, SQLEXEC_SAMPLE, TREE_VIEW_Data, RAW_SAMPLE, parser_preview, ARTIFACT_SAMPLE } from '../sample-data';

import * as fpGet from 'lodash/fp/get';
import * as forEach from 'lodash/forEach';
import * as find from 'lodash/find';
import * as map from 'lodash/map';
import * as isUndefined from 'lodash/isUndefined';

// import { JwtService } from '../../../../login/services/jwt.service';
// import { MenuService } from '../../../common/services/menu.service';

import APP_CONFIG from '../../../../../../../appConfig';

const userProject: string = 'workbench';

@Injectable()
export class WorkbenchService {
  private api = fpGet('api.url', APP_CONFIG);
  private wbAPI = `${this.api}/internal/workbench/projects`;

  constructor(private http: HttpClient,
    private router: UIRouter) { }

  /** GET datasets from the server */
  getDatasets(): Observable<any> {
    const endpoint = `${this.wbAPI}/${userProject}/datasets`;
    return this.http.get(endpoint)
      .pipe(
        catchError(this.handleError('data', DATASETS)));
  }

  /** GET Staging area tree list */
  getStagingData(path: string): Observable<any> {
    const endpoint = `${this.wbAPI}/${userProject}/raw/directory`;
    return this.http.post(endpoint, { path })
      .pipe(
        catchError(this.handleError('data', [])));
  }

  /** GET raw preview from the server */
  getRawPreviewData(path: string): Observable<any> {
    const endpoint = `${this.wbAPI}/${userProject}/raw/directory/preview`;
    return this.http.post(endpoint, { path })
      .pipe(
        catchError(this.handleError('data', [])));
  }

  /** GET parsed preview from the server */
  getParsedPreviewData(previewConfig): Observable<any> {
    const endpoint = `${this.wbAPI}/${userProject}/raw/directory/inspect`;
    return this.http.post(endpoint, previewConfig)
      .pipe(
        catchError(this.handleError('data', [])));
  }

  /** File mask search */
  filterFiles(mask, temmpFiles) {
    let selFiles = [];
    if (isUndefined(mask)) {
      return;
    }
    let wildcardSearch: any;
    if (this.startsWith(mask, '*')) {
      wildcardSearch = this.endsWith;
    }
    if (this.endsWith(mask, '*')) {
      wildcardSearch = this.startsWith;
    }
    if (!mask.includes('*')) {
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
    const endpoint = `${this.wbAPI}/${userProject}/raw/directory/upload/files?path=${path}`;

    // const endpoint = `${this.wbAPI}/${projectName}/directory/upload/files`;
    const formData: FormData = new FormData();
    forEach(filesToUpload, file => {
      formData.append('files', file);
    });
    return this.http.post(endpoint, formData);
  }

  validateMaxSize(fileList: FileList) {
    let size = 0;
    let maxSize = 26214400;
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
    return this.http.post(endpoint, { path })
      .pipe(
        catchError(this.handleError('data', [])));
  }
  /**
   * Service to fetch meta data of all datasets
   * 
   * @param {string} projectName 
   * @param {any} id 
   * @returns {Observable<any>} 
   * @memberof WorkbenchService
   */
  getDatasetDetails(id): Observable<any> {
    const endpoint = `${this.wbAPI}/${userProject}/${id}`;
    return this.http.get(`${this.wbAPI}/${userProject}/${id}`)
      .pipe(
        catchError(this.handleError('data', ARTIFACT_SAMPLE)));
  }

  triggerParser(payload) {
    const endpoint = `${this.api}/internal/workbench/datasets`;
    return this.http.post(endpoint, payload)
      .pipe(catchError(this.handleError('data', {})));
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
    return this.http.post(endpoint, { query })
      .pipe(catchError(this.handleError('data', SQLEXEC_SAMPLE)));
  }


  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      return of(result as T);
    };
  }
}