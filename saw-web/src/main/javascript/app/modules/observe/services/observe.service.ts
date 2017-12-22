import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import * as fpGet from 'lodash/fp/get';

import { JwtService } from '../../../../login/services/jwt.service';
import { Dashboard } from '../models/dashboard.interface';
import APP_CONFIG from '../../../../../../../appConfig';

@Injectable()
export class ObserveService {

  private api = fpGet('api.url', APP_CONFIG);

  constructor(private http: HttpClient, private jwt: JwtService) {}

  addHeaders(headers = new HttpHeaders({})) {
    headers = headers.append('Authorization', `Bearer ${this.jwt.getAccessToken()}`)
    return headers;
  }

  addModelStructure(model) {
    return {
      contents: {
        observe: [model]
      }
    };
  }

  /* Saves dashboard. If @model.entityId not present, uses create operation.
     Otherwise uses update operation.
  */
  saveDashboard(model: Dashboard) {
    let method = 'post', endpoint = 'create';
    if (fpGet('entityId', model)) {
      method = 'put';
      endpoint = model.entityId
      model.updatedBy = this.jwt.getUserId();
    } else {
      // Log the creator id if creating for first time
      model.createdBy = this.jwt.getUserId();
    }

    return this.http[method](`${this.api}/observe/dashboards/${endpoint}` , this.addModelStructure(model), {
      headers: this.addHeaders()
    }).map(fpGet('contents.observe.0'));
  }

  getDashboard(entityId: string): Observable<Dashboard> {
    return this.http.get(`${this.api}/observe/dashboards/${entityId}` , {
      headers: this.addHeaders()
    }).map(fpGet('contents.observe.0'));
  }
}
