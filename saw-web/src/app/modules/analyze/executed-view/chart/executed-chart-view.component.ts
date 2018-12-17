import { Component, Input } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'executed-chart-view',
  templateUrl: 'executed-chart-view.component.html'
})

export class ExecutedChartViewComponent {
  @Input() updater: BehaviorSubject<Object[]>;
  @Input() analysis;
  @Input() data;
}
