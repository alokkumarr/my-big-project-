import * as get from 'lodash/get';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { JwtService } from '../../../common/services';
import { of, Observable } from 'rxjs';


import APP_CONFIG from '../../../../../appConfig';

@Injectable({
  providedIn: 'root'
})
export class RtisService {
  public api = get(APP_CONFIG, 'api.url');
  public wbAPI = `${this.api}/internal/workbench/projects`;

  constructor(
    public http: HttpClient,
    public jwt: JwtService,
    public router: Router
  ) {}


  createRegistration(payload) {
    const endpoint = `${this.api}/internal/rtisconfig`;
    return this.postRequest(endpoint, payload).toPromise();
  }

  public handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      return of(result as T);
    };
  }

  getAppKeys() {
    const path = `${this.api}/internal/rtisconfig/appKeys`;
    return this.getRequest(path).toPromise();
  }

  deleteAppKey(appKey) {
    return this.deleteRequest(appKey).toPromise();
  }

  getRequest(path): Observable<any> {
    return this.http.get(path);
  }

  postRequest(
    path: string,
    params: Object,
    customHeaders = {}
  ): Observable<any> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        ...customHeaders
      })
    };
    return this.http.post(path, params, httpOptions);
  }


  deleteRequest(key): Observable<any> {
    const path = `${this.api}/internal/rtisconfig/${key}`;
    return (
      this.http.delete(path)
    );
  }

}
