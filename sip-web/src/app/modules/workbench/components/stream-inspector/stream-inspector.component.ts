import { Component, OnInit, OnDestroy } from '@angular/core';

import { Observable, of, SubscriptionLike } from 'rxjs';

import * as union from 'lodash/union';
import * as keys from 'lodash/keys';
import * as forEach from 'lodash/forEach';
import * as isEmpty from 'lodash/isEmpty';
import * as head from 'lodash/head';
import * as filter from 'lodash/filter';

import { WorkbenchService } from '../../services/workbench.service';

@Component({
  selector: 'stream-inspector',
  templateUrl: './stream-inspector.component.html',
  styleUrls: ['./stream-inspector.component.scss']
})
export class StreamInspectorComponent implements OnInit, OnDestroy {
  public streamData$: Observable<any>;
  public topicData$: Observable<any>;
  private streamResult$: Observable<any>;
  public streamTopicData$: Observable<any>;
  private eventTypeResult$: Observable<any>;
  public eventTypeData$: Observable<any>;
  private subscriptions: SubscriptionLike[] = [];
  public streamName: string;
  public topicName = '';
  public eventType: string;
  constructor(private _workbench: WorkbenchService) {}

  ngOnInit() {
    this.getAllStreamAndTopic();
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  getAllStreamAndTopic() {
    this.streamData$ = of([]);
    this.topicData$ = of([]);
    this.streamTopicData$ = of([]);
    this.streamName = '';
    this.topicName = '';
    this.eventTypeData$ = of([]);
    this.eventType = '';
    this.streamResult$ = this._workbench.getListOfStreams();

    this.streamResult$.subscribe(result => {
      if (!isEmpty(result)) {
        let cols;
        const stream = [];
        const eventType = [];
        forEach(result, item => {
          if (!isEmpty(item)) {
            cols = union([], keys(item));
            cols.shift();
            stream.push(head(item[head(cols)]));
            eventType.push({
              stream: head(item[head(cols)]).queue,
              data: item.eventTypes
            });
          }
        });
        this.streamData$ = of(stream);
        this.eventTypeResult$ = of(eventType);
      }
    });
  }

  streamChanged() {
    this.resetTopicAndDataGrid();
    this.eventTypeResult$.subscribe(result => {
      const matched = filter(result, {
        stream: this.streamName
      })[0];
      const eventData = [];
      forEach(matched.data, data => {
        eventData.push({
          displayValue: data,
          value: data
        });
      });
      this.eventTypeData$ = of(eventData);
    });

    const subs = this.streamData$.subscribe(res => {
      const topic = filter(res, {
        queue: this.streamName
      });
      this.topicData$ = of(topic);
    });

    this.subscriptions.push(subs);
  }

  eventTypeChanged() {
    this.streamTopicData$ = of([]);
    setTimeout(() => {
      this.streamTopicData$ = this._workbench.getListOfTopics(
        this.streamName,
        this.eventType
      );
    }, 200);
  }

  resetTopicAndDataGrid() {
    this.streamTopicData$ = of([]);
    this.topicData$ = of([]);
    this.eventTypeData$ = of([]);
    this.eventType = '';
    this.topicName = '';
  }

  trackByFn(index) {
    return index;
  }
}
