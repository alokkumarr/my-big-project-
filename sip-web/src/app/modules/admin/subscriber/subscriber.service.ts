import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
// import { HttpClient } from '@angular/common/http';
// import AppConfig from '../../../../../appConfig';

import {
  SIPSubscriber,
  SubscriberChannelType
} from './models/subscriber.model';

// const apiUrl = AppConfig.api.url;

@Injectable({
  providedIn: 'root'
})
export class SubscriberService {
  constructor(/* private http: HttpClient */) {}

  getSubscriber(id: string): Observable<SIPSubscriber> {
    // return this.http.get<SIPSubscriber>(apiUrl + `/subscribers/${id}`);

    return of({
      id,
      subscriberName: 'Test Subscriber ' + id,
      channelType: SubscriberChannelType.EMAIL,
      channelValue: `subscriber${id}@synchronoss.com`
    });
  }

  getAllSubscribers(): Observable<SIPSubscriber[]> {
    // return this.http.get<SIPSubscriber[]>(apiUrl + `/subscribers`);

    const subscribers = [...Array(50).keys()].map(id => ({
      id: String(id),
      subscriberName: 'Test Subscriber ' + id,
      channelType: SubscriberChannelType.EMAIL,
      channelValue: `subscriber${id}@synchronoss.com`
    }));
    return of(subscribers);
  }

  createSubscriber(
    subscriber: Partial<SIPSubscriber>
  ): Observable<SIPSubscriber> {
    // return this.http.post<SIPSubscriber>(apiUrl + `/subscribers`, subscriber);

    const id = Date.now().toString();
    return of({
      id,
      subscriberName: 'Test Subscriber ' + id,
      channelType: SubscriberChannelType.EMAIL,
      channelValue: `subscriber${id}@synchronoss.com`
    });
  }

  updateSubscriber(subscriber: SIPSubscriber): Observable<SIPSubscriber> {
    // const {id, ...updateData} = subscriber;
    // return this.http.put<SIPSubscriber>(apiUrl + `/subscribers/${id}`, updateData);

    return of(subscriber);
  }

  saveSubscriber(subscriber: SIPSubscriber): Observable<SIPSubscriber> {
    return subscriber.id === null
      ? this.createSubscriber(subscriber)
      : this.updateSubscriber(subscriber);
  }
}
