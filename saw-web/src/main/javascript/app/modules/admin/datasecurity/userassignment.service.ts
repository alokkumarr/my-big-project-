import { Injectable } from '@angular/core';
import { JwtService } from '../../../../login/services/jwt.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import AppConfig from '../../../../../../../appConfig';

const apiUrl = AppConfig.api.url;

@Injectable()
export class UserAssignmentService {

  constructor(
    private _http : HttpClient,
    private _jwtService: JwtService
  ) {}

  //Add a new security group detail.
  addSecurityGroup(securityGroup) {
    const requestBody = {
      ...securityGroup,
      createdBy: this._jwtService.getUserName(),
      userId: this._jwtService.getLoginId()
    }
    return this.postRequest(`auth/addSecurityGroups`, requestBody)
  }

  ////edit an exiting security group detail.
  editSecurityGroup(securityGroup) {
    const requestBody = {
      ...securityGroup,
      createdBy: this._jwtService.getUserName(),
      userId: this._jwtService.getUserId()
    }
    return this.postRequest(`auth/addSecurityGroups`, requestBody);
  }

  addAttributetoGroup(attribute) {
    const requestBody = {
      ...attribute,
      createdBy: this._jwtService.getUserName(),
      userId: this._jwtService.getUserId(),
      date: new Date()
    }

    return this.postRequest(`auth/addSecurityGroupDskAttributeValues`, requestBody);
  }

  getSecurityGroups() {
    return this.getRequest();
  }

  getRequest() {
    return this._http.get(`http://54.157.215.36/saw/security/auth/getSecurityGroups`).toPromise();
  }

  postRequest(path: string, params: Object) {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type':  'application/json'
      })
    };
    return this._http.post(`http://54.157.215.36/saw/security/auth/addSecurityGroups`, params, httpOptions).toPromise();
    //return this._http.post(`${apiUrl}/${path}`, params, httpOptions).toPromise();
  }
}
