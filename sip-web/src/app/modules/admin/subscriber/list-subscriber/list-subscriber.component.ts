import { Component, OnInit } from '@angular/core';
import { SIPSubscriber } from '../models/subscriber.model';
import { Observable } from 'rxjs';
import { SubscriberService } from '../subscriber.service';
import { map } from 'rxjs/operators';

@Component({
  selector: 'list-subscriber',
  templateUrl: './list-subscriber.component.html',
  styleUrls: ['./list-subscriber.component.scss']
})
export class ListSubscriberComponent implements OnInit {
  DEFAULT_PAGE_SIZE = 25;

  subscribers$: Observable<SIPSubscriber[]> = this.allSubscribers();
  enablePaging$ = this.subscribers$.pipe(
    map(items => items.length > this.DEFAULT_PAGE_SIZE)
  );

  constructor(private subscriberService: SubscriberService) {}

  ngOnInit() {}

  allSubscribers() {
    return this.subscriberService.getAllSubscribers();
  }

  edit(subscriber: SIPSubscriber) {
    console.log(subscriber);
  }
}
