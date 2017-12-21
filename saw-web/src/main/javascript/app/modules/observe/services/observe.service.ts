import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class ObserveService {

  constructor(private http: HttpClient) {}

  saveDashboard(model) {
    // console.log(model);
    return this.http.post('', model);
  }
}
