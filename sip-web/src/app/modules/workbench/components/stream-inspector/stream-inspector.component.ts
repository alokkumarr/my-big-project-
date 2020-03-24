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
  private subscriptions: SubscriptionLike[] = [];
  public streamName: string;
  public topicName = '';
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
    this.streamResult$ = this._workbench.getListOfStreams();

    this.streamResult$.subscribe(result => {
      if (!isEmpty(result)) {
        let cols;
        const stream = [];
        forEach(result, item => {
          cols = union([], keys(item));
          cols.shift();
          stream.push(head(item[head(cols)]));
        });
        this.streamData$ = of(stream);
      }
    });
  }

  streamChanged() {
    this.resetTopicAndDataGrid();
    const subs = this.streamData$.subscribe(result => {
      const topic = filter(result, {
        queue: this.streamName
      });
      this.topicData$ = of(topic);
    });

    this.subscriptions.push(subs);
  }

  topicChanged() {
    this.streamTopicData$ = of([]);
    setTimeout(() => {
      this.streamTopicData$ = this._workbench.getListOfTopics(this.streamName);
    }, 200);
  }

  resetTopicAndDataGrid() {
    this.streamTopicData$ = of([]);
    this.topicData$ = of([]);
    this.topicName = '';
  }

  trackByFn(index) {
    return index;
  }
}
