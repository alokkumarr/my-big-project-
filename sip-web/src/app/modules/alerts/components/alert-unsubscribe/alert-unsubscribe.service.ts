import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import AppConfig from '../../../../../../appConfig';
import { Observable } from 'rxjs';

@Injectable()
export class AlertUnsubscribeService {
  constructor(private _http: HttpClient) {}

  unsubscribeAnAlert(token): Observable<any> {
    const url = AppConfig.api.url;
    const endpoint = `${url}/alerts/subscriber/deactivate?token=${token}`;
    const headers = new HttpHeaders().set('Content-Type', 'text/plain; charset=utf-8');
    return this._http
      .post(endpoint, '',  { headers, responseType: 'text' as 'json'});
  }
}
