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
    // if in query mode, don't send the artifacts, just use the column names in the data
    // TODO use the columns from the query
    const isEsReport = analysis.type === 'esReport';
    const isInQueryMode = analysis.edit;
    const dataFields = analysis.sqlBuilder.dataFields;

    if (isInQueryMode) {
      this.artifacts = null;
    } else if (isEsReport) {
      const containsArtifacts = dataFields[0].tableName;
      if (containsArtifacts) {
        this.artifacts = dataFields;
      } else {
        // for backward compatibility we have to check if we have the artifacts, or artifact columns
        this.artifacts = [{columns: dataFields}];
      }
    } else {
      // DL report
      this.artifacts = dataFields;
    }
  };
  @Input() dataLoader: Function;

  analysis: Analysis;
  artifacts: Artifact[];

  constructor() { }

}
