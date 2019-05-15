import { Component, Input } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';

import { AnalysisChart } from '../../types';

@Component({
  selector: 'executed-map-chart-view',
  templateUrl: 'executed-map-chart-view.component.html',
  styleUrls: ['./executed-map-chart-view.component.scss']
})
export class ExecutedMapChartViewComponent {
  @Input() updater: BehaviorSubject<Object[]>;
  @Input() actionBus: Subject<Object[]>;
  @Input() analysis: AnalysisChart;
  @Input() data: any[];
}
