import { Injectable } from '@angular/core';
import { JwtService } from '../../../../login/services/jwt.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import AppConfig from '../../../../../../../appConfig';

const loginUrl = AppConfig.login.url;

@Injectable()
export class UserAssignmentService {

  constructor(
    private _http : HttpClient,
    private _jwtService: JwtService
  ) {}

  getList(customerId) {
    console.log(customerId);
    return this.getRequest('auth/getAlluserAssignments');
  }

  //Add a new security group detail.
  addSecurityGroup(data) {
    let requestBody = {};
    let path;
    switch (data.mode) {
    case 'create':
      requestBody = {
        description: data.description,
        securityGroupName: data.securityGroupName
      }
      path = 'auth/addSecurityGroups';
      break;
    case 'edit':
      requestBody = [data.securityGroupName, data.description, data.groupSelected];
      path = 'auth/updateSecurityGroups';
      break;
    }
    return this.postRequest(path, requestBody);
  }

  addAttributetoGroup(attribute, mode) {
    let path;
    switch (mode) {
    case 'create':
      path = 'auth/addSecurityGroupDskAttributeValues';
      break;
    case 'edit':
      path = 'auth/updateAttributeValues';
      break;
    }
    return this.postRequest(path, attribute);
  }

  getSecurityAttributes(request) {
    return this.getRequest(`auth/fetchDskAllAttributeValues?securityGroupName=${request}`);
  }

  getSecurityGroups() {
    return this.getRequest('auth/getSecurityGroups');
  }

  deleteGroupOrAttribute(path, request) {
    return this.postRequest(path, request);
  }

  getRequest(path) {
    return this._http.get(`${loginUrl}/${path}`).toPromise();
  }

  postRequest(path: string, params: Object) {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type':  'application/json'
      })
    };
    return this._http.post(`${loginUrl}/${path}`, params, httpOptions).toPromise();
  }
}
