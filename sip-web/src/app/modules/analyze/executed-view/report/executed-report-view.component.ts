import { Component, Input } from '@angular/core';
import { Analysis, Artifact } from '../../types';
import * as get from 'lodash/get';
@Component({
  selector: 'executed-report-view',
  templateUrl: 'executed-report-view.component.html'
})
export class ExecutedReportViewComponent {
  @Input('analysis')
  set setAnalysis(analysis: Analysis) {
    this.analysis = analysis;
    // if in query mode, don't send the artifacts, just use the column names in the data
    // TODO use the columns from the query
    const isEsReport = analysis.type === 'esReport';
    const isInQueryMode = analysis.edit;
    const dataFields = get(analysis, 'sqlBuilder.dataFields');

    if (isInQueryMode) {
      this.artifacts = null;
    } else if (isEsReport) {
      const containsArtifacts = <any>dataFields[0].tableName;
      if (containsArtifacts) {
        this.artifacts = dataFields;
      } else {
        // for backward compatibility we have to check if we have the artifacts, or artifact columns
        this.artifacts = <Artifact[]>[
          { columns: dataFields, artifactName: '' }
        ];
      }
    } else {
      // DL report
      this.artifacts = dataFields;
    }
  }
  @Input()
  dataLoader: Function;

  analysis: Analysis;
  artifacts: Artifact[];

  constructor() {}
}
