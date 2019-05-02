import {
  Component,
  Input,
  Output,
  OnInit,
  OnDestroy,
  AfterViewInit,
  EventEmitter
} from '@angular/core';
import { BehaviorSubject, Subscription } from 'rxjs';
import { AnalyzeService } from '../../../analyze/services/analyze.service';
import { flattenChartData } from '../../../../common/utils/dataFlattener';
import * as isUndefined from 'lodash/isUndefined';

import { EXECUTION_MODES } from '../../../analyze/services/analyze.service';

@Component({
  selector: 'observe-map-chart',
  templateUrl: './observe-map-chart.component.html',
  styleUrls: ['./observe-map-chart.component.scss']
})
export class ObserveMapChartComponent
  implements OnInit, OnDestroy, AfterViewInit {
  @Input() analysis: any;
  @Input() item: any;
  @Input() enableChartDownload: boolean;
  @Input() isZoomAnalysis: boolean;
  @Input() updater: BehaviorSubject<Array<any>>;
  @Input() ViewMode: boolean;
  @Output() onRefresh = new EventEmitter<any>();

  public chartUpdater = new BehaviorSubject([]);
  public requesterSubscription: Subscription;
  public data: Array<any>;

  constructor(public analyzeService: AnalyzeService) {}

  ngOnInit() {
    this.subscribeToRequester();
  }

  ngAfterViewInit() {
    this.initChart();
  }

  ngOnDestroy() {
    this.requesterSubscription.unsubscribe();
  }

  /* Accept changes from parent component and pass those on to chart.
     Having separate requester and chartUpdater allows transforming
     changes coming from parent before passing them on. */
  subscribeToRequester() {
    this.requesterSubscription = this.updater.subscribe(changes => {
      this.chartUpdater.next(changes);
    });
  }

  initChart() {
    if (isUndefined(this.analysis._executeTile) || this.analysis._executeTile) {
      this.onRefreshData().then(() => {
        this.item && this.onRefresh.emit(this.item);
      });
    }
  }

  onRefreshData() {
    return this.analyzeService
      .getDataBySettings(this.analysis, EXECUTION_MODES.LIVE)
      .then(
        ({ data }) => {
          const parsedData = flattenChartData(data, this.analysis.sqlBuilder);
          this.data = parsedData;
          return parsedData;
        },
        err => {
          throw err;
        }
      );
  }
}
