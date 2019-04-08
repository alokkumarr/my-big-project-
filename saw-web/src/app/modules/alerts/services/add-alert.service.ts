import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as get from 'lodash/get';

import APP_CONFIG from '../../../../../appConfig';

@Injectable({
  providedIn: 'root'
})
export class AddAlertService {
  public api = get(APP_CONFIG, 'api.url');

  constructor(public http: HttpClient) {}

  /**
   *Gets list of all datapods avialble for that particular project
   *
   * @returns {Observable<any>}
   * @memberof AddAlertService
   */
  getListOfDatapods$(): Observable<any> {
    const projectId = 'workbench';

    return this.http.get(
      `${this.api}/internal/semantic/md?projectId=${projectId}`
    );
  }
}
