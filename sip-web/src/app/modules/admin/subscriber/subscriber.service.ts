import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import AppConfig from '../../../../../appConfig';

import * as isNil from 'lodash/isNil';

import { SIPSubscriber } from './models/subscriber.model';

const apiUrl = AppConfig.api.url;

@Injectable({
  providedIn: 'root'
})
export class SubscriberService {
  constructor(private http: HttpClient) {}

  getSubscriber(id: string): Observable<SIPSubscriber> {
    return this.http.get<SIPSubscriber>(apiUrl + `/subscribers/${id}`);
  }

  getAllSubscribers(): Observable<SIPSubscriber[]> {
    return this.http.get<SIPSubscriber[]>(apiUrl + `/subscribers/`);
  }

  createSubscriber(
    subscriber: Partial<SIPSubscriber>
  ): Observable<SIPSubscriber> {
    return this.http.post<SIPSubscriber>(apiUrl + `/subscribers/`, subscriber);
  }

  updateSubscriber(subscriber: SIPSubscriber): Observable<SIPSubscriber> {
    const { id, ...updateData } = subscriber;
    return this.http.put<SIPSubscriber>(
      apiUrl + `/subscribers/${id}`,
      updateData
    );
  }

  saveSubscriber(subscriber: SIPSubscriber): Observable<SIPSubscriber> {
    return isNil(subscriber.id)
      ? this.createSubscriber(subscriber)
      : this.updateSubscriber(subscriber);
  }

  deleteSubscriber(subscriberId: string): Observable<null> {
    return this.http.delete<null>(apiUrl + `/subscribers/${subscriberId}`);
  }
}
