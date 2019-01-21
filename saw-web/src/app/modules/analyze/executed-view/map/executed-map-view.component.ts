import { Component, Input } from '@angular/core';
import { Subject } from 'rxjs';

import { AnalysisChart } from '../../types';

@Component({
  selector: 'executed-map-view',
  templateUrl: 'executed-map-view.component.html'
  // styleUrls: ['./executed-map-view.component.scss']
})
export class ExecutedMapViewComponent {
  @Input() actionBus: Subject<Object[]>;
  @Input() analysis: AnalysisChart;
  @Input() data: any[];
}
