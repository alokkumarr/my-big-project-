import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { UIRouter } from '@uirouter/angular';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { catchError } from 'rxjs/operators';

import { DATASETS, TREE_DATA, TREE_VIEW_Data, RAW_SAMPLE, parser_preview, ARTIFACT_SAMPLE } from '../sample-data';

import * as fpGet from 'lodash/fp/get';
import * as forEach from 'lodash/forEach';
import * as find from 'lodash/find';
import * as map from 'lodash/map';
import * as isUndefined from 'lodash/isUndefined';

import { JwtService } from '../../../../login/services/jwt.service';
import { MenuService } from '../../../common/services/menu.service';
import APP_CONFIG from '../../../../../../../appConfig';

@Injectable()
export class WorkbenchService {
  private api = fpGet('api.url', APP_CONFIG);
  private wbAPI = `${this.api}/internal/workbench/projects`;
  private apiWB = fpGet('wbAPI.url', APP_CONFIG);

  constructor(private http: HttpClient,
    private jwt: JwtService,
    private router: UIRouter,
    private menu: MenuService) { }

  /** GET datasets from the server */
  getDatasets(projectName: string): Observable<any> {
    let Params = new HttpParams();
    Params = Params.append('prj', projectName);
    return this.http.get(`${this.apiWB}/dl/sets`, { params: Params })
      .pipe(
        catchError(this.handleError('data', DATASETS)));
  }

  /** GET Staging area tree list */
  getProjects(): Observable<any> {
    return this.http.get(`${this.wbAPI}/list`)
      .pipe(
        catchError(this.handleError('data', TREE_VIEW_Data)));
  }

  /** GET Staging area tree list */
  getStagingData(projectName: string, path: string): Observable<any> {
    const endpoint = `${this.wbAPI}/${projectName}/raw/directory`;
    return this.http.post(endpoint, { path })
      .pipe(
        catchError(this.handleError('data', TREE_VIEW_Data)));
  }

  /** GET raw preview from the server */
  getRawPreviewData(projectName: string, path: string): Observable<any> {
    const endpoint = `${this.wbAPI}/${projectName}/raw/preview`;
    return this.http.post(endpoint, {path})
      .pipe(
        catchError(this.handleError('data', RAW_SAMPLE)));
  }

  /** GET parsed preview from the server */
  getParsedPreviewData(projectName: string, previewConfig): Observable<any> {
    const endpoint = `${this.wbAPI}/${projectName}/raw/directory/inspect`;
    return this.http.post(endpoint, previewConfig)
      .pipe(
        catchError(this.handleError('data', parser_preview)));
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

  uploadFile(filesToUpload: FileList, projectName: string, path: string): Observable<any> {
    // const endpoint = `${this.wbAPI}/${projectName}/directory/upload/files?cat=${path}`;

    const endpoint = `${this.wbAPI}/${projectName}/directory/upload/files`;
    const formData: FormData = new FormData();
    forEach(filesToUpload, file => {
      formData.append('file', file);
    });
    // const dirPath = {path};

    const blobOverrides = new Blob([JSON.stringify(path)], {
      type: 'application/json'
    });
    formData.append('path', blobOverrides);
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

  createFolder(projectName: string, path: string): Observable<any> {
    const endpoint = `${this.wbAPI}/${projectName}/raw/directory/create`;
    return this.http.post(endpoint, {path})
      .pipe(
        catchError(this.handleError('data', TREE_VIEW_Data)));
  }
  /**
   * Service to fetch meta data of dataset
   * 
   * @param {string} projectName 
   * @param {any} id 
   * @returns {Observable<any>} 
   * @memberof WorkbenchService
   */
  getDatasetDetails(projectName: string, id): Observable<any> {
    const endpoint = `${this.wbAPI}/${projectName}/${id}`;
    return this.http.get(`${this.wbAPI}/${projectName}/${id}`)
      .pipe(
        catchError(this.handleError('data', ARTIFACT_SAMPLE)));
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