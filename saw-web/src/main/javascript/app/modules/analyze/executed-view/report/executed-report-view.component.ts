import { Component, Input } from '@angular/core';

import { Analysis } from '../../types';

const template = require('./executed-report-view.component.html');

@Component({
  selector: 'executed-report-view',
  template
})

export class ExecutedReportViewComponent {
  @Input() analysis: Analysis;
  @Input() dataLoader: Function;

  constructor() { }

}
