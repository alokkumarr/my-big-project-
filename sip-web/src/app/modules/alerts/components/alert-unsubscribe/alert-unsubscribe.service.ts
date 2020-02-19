import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import AppConfig from '../../../../../../appConfig';
// import { Observable } from 'rxjs';
@Injectable()
export class AlertUnsubscribeService {
  constructor(private _http: HttpClient) {}

  unsubscribeAnAlert(token) {
    console.log(token);
    const url = AppConfig.api.url;
    const endpoint = `${url}/alerts/subscriber/deactivate?token=${token}`;
    return this._http
      .post(endpoint, '')
      .toPromise()
      .then(response => {
        console.log(response);
        return response;
      });
    // return this._http.post(endpoint, httpOptions);
  }

}
