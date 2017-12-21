import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { JwtService } from '../../../../login/services/jwt.service';

import * as fpGet from 'lodash/fp/get';

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
  saveDashboard(model) {
    let method = 'post', endpoint = 'create';
    if (fpGet('entityId', model)) {
      method = 'put';
      endpoint = model.entityId
    }
    return this.http[method](`${this.api}/observe/dashboards/${endpoint}` , this.addModelStructure(model), {
      headers: this.addHeaders()
    }).map(fpGet('contents.observe.0'));
  }
}
