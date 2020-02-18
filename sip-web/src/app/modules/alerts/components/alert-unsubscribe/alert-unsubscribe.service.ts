import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import AppConfig from '../../../../../../appConfig';
import { Observable } from 'rxjs';
@Injectable()
export class AlertUnsubscribeService {
  constructor(private _http: HttpClient) {}

  unsubscribeAnAlert(token): Observable<any> {
    console.log(token);
    const url = AppConfig.api.url;

    return this._http.get(`${url}/alerts/subscriber/deactivate?token=${token}`);
  }

}
