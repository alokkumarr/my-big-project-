import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import AppConfig from '../../../../../../appConfig';
import { Observable } from 'rxjs';
export interface Color {
  name: string;
  hex: string;
  darkContrast: boolean;
}


@Injectable()
export class AlertUnsubscribeService {
  constructor(private _http: HttpClient) {}

  unsubscribeAnAlert(token): Observable<any> {
    const url = AppConfig.login.url;
    return this._http.get(`${url}/alerts/subscriber/deactivate?token=${token}`);
  }

}
