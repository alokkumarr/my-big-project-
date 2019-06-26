import { Component, Input } from '@angular/core';
import { Analysis, Artifact, isDSLAnalysis } from '../../types';
import * as get from 'lodash/get';
@Component({
  selector: 'executed-report-view',
  templateUrl: 'executed-report-view.component.html'
})
export class ExecutedReportViewComponent {
  public analysisSorts: any;

  @Input('analysis')
  set setAnalysis(analysis: Analysis) {
    this.analysis = analysis;
    // if in query mode, don't send the artifacts, just use the column names in the data
    // TODO use the columns from the query
    const isEsReport = analysis.type === 'esReport';
    const isInQueryMode = analysis.edit;
    const dataFields = isDSLAnalysis(analysis)
      ? get(analysis, 'sipQuery.artifacts')
      : get(analysis, 'sqlBuilder.dataFields');

    if (isInQueryMode) {
      this.artifacts = null;
    } else if (isEsReport) {
      let containsArtifacts: String;
      isDSLAnalysis(analysis)
        ? (containsArtifacts = <String>(
            analysis.sipQuery.artifacts[0].artifactsName
          ))
        : (containsArtifacts = <String>dataFields[0].tableName);

      //const containsArtifacts = <any>dataFields[0].tableName;
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
    this.analysisSorts = isDSLAnalysis(analysis)
      ? analysis.sipQuery.sorts
      : analysis.sqlBuilder.sorts || analysis.sqlBuilder.orderByColumns;
  }
  @Input()
  dataLoader: Function;

  analysis: Analysis;
  artifacts: Artifact[];

  constructor() {}
}
