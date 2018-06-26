import { Component, Input } from '@angular/core';

import { Analysis } from '../../types';

const template = require('./executed-chart-view.component.html');

@Component({
  selector: 'executed-chart-view',
  template
})

export class ExecutedChartViewComponent {
  @Input() analysis: Analysis;
  @Input() data: any[];

  constructor() { }

}
