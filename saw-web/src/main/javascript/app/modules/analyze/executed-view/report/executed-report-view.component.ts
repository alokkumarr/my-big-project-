import { Component, Input } from '@angular/core';

import { Analysis, Artifact } from '../../types';

const template = require('./executed-report-view.component.html');

@Component({
  selector: 'executed-report-view',
  template
})

export class ExecutedReportViewComponent {
  @Input('analysis') set setAnalysis(analysis: Analysis) {
    this.analysis = analysis;
    this.artifacts = analysis.edit ? null : analysis.artifacts;
  };
  @Input() dataLoader: Function;

  analysis: Analysis;
  artifacts: Artifact[];

  constructor() { }

}
