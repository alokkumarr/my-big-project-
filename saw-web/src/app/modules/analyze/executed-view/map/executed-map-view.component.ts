import { Component, Input } from '@angular/core';
import { Subject, BehaviorSubject } from 'rxjs';

import { AnalysisChart } from '../../types';

@Component({
  selector: 'executed-map-view',
  templateUrl: 'executed-map-view.component.html',
  styles: [`:host {
    display: block;
    height: calc(100vh - 250px);
  }`]
})
export class ExecutedMapViewComponent {
  @Input() actionBus: Subject<Object[]>;
  @Input() analysis: AnalysisChart;
  @Input() data: any[];
  @Input() updater: BehaviorSubject<Object[]>;
}
