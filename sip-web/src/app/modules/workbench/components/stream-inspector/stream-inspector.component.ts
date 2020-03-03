import { Component, OnInit, OnDestroy } from '@angular/core';
import { Location } from '@angular/common';

import { Observable, of, SubscriptionLike } from 'rxjs';
import * as filter from 'lodash/filter';

import { WorkbenchService } from '../../services/workbench.service';

import { mockedData } from './index';

@Component({
  selector: 'stream-inspector',
  templateUrl: './stream-inspector.component.html',
  styleUrls: ['./stream-inspector.component.scss']
})
export class StreamInspectorComponent implements OnInit, OnDestroy {
  public streamData$: Observable<any>;
  public topicData$: Observable<any>;
  public streamTopicData: Array<any> = [];
  private subscriptions: SubscriptionLike[] = [];
  constructor(
    private _location: Location,
    private _workbench: WorkbenchService
  ) {}

  ngOnInit() {
    this.streamData$ = of(mockedData);
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  getAllStreamAndTopic() {
    /* this.streamData$ = of([]);
    this.resetTopicAndDataGrid();
    setTimeout(() => {
      // this.streamData$ = of(mockedData);
    }, 1500); */
  }

  streamChanged(event) {
    this.resetTopicAndDataGrid();
    const subs = this.streamData$.subscribe(result => {
      const streamTopic = filter(result, {
        streamId: event.value
      });
      this.topicData$ = of(streamTopic[0].topic);
    });

    this.subscriptions.push(subs);
  }

  topicChanged(event) {
    const subs = this.topicData$.subscribe(result => {
      const topic = filter(result, {
        title: event.value
      });
      this.streamTopicData = topic[0].topicData;
    });

    this.subscriptions.push(subs);
  }

  getTopicData(id) {}

  resetTopicAndDataGrid() {
    this.streamTopicData = [];
    this.topicData$ = of([]);
  }

  goBack() {
    this._location.back();
  }

  trackByFn(index) {
    return index;
  }
}
