import angular from 'angular';

import {routesConfig} from './routes';

import {AnalyzeService} from './services/analyze.service';

import {AnalyzePageComponent} from './components/analyze-page/analyze-page.component';
import {AnalyzeViewComponent} from './components/analyze-view/analyze-view.component';
import {AnalyzeCardComponent} from './components/analyze-card/analyze-card.component';
import {AnalyzeNewComponent} from './components/analyze-new/analyze-new.component';
import {AnalyzeReportComponent} from './components/analyze-report/analyze-report.component';
import {AnalyzeDialogComponent} from './components/analyze-dialog/analyze-dialog.component';
import {AnalyzeReportSortComponent} from './components/analyze-report-sort/analyze-report-sort.component';
import {AnalyzeReportDescriptionComponent} from './components/analyze-report-description/analyze-report-description.component';
import {AnalyzeReportPreviewComponent} from './components/analyze-report-preview/analyze-report-preview.component';
import {ReportGridComponent} from './components/analyze-report-grid/report-grid/report-grid.component';
import {ReportGridNodeComponent} from './components/analyze-report-grid/report-grid-node/report-grid-node.component';
import {ReportGridContainerComponent} from './components/analyze-report-grid/report-grid-container/report-grid-container.component';
import {ReportRenameDialogComponent} from './components/analyze-report-grid/report-rename-dialog/report-rename-dialog.component';
import {AnalyzeReportSaveComponent} from './components/analyze-report-save/analyze-report-save.component';

export const AnalyzeModule = 'AnalyzeModule';

angular.module(AnalyzeModule, [])
  .config(routesConfig)
  .factory('AnalyzeService', AnalyzeService)
  .component('reportGridContainer', ReportGridContainerComponent)
  .component('reportGridNode', ReportGridNodeComponent)
  .component('reportGrid', ReportGridComponent)
  .component('reportRenameDialog', ReportRenameDialogComponent)
  .component('analyzePage', AnalyzePageComponent)
  .component('analyzeView', AnalyzeViewComponent)
  .component('analyzeCard', AnalyzeCardComponent)
  .component('analyzeNew', AnalyzeNewComponent)
  .component('analyzeReport', AnalyzeReportComponent)
  .component('analyzeDialog', AnalyzeDialogComponent)
  .component('analyzeReportSort', AnalyzeReportSortComponent)
  .component('analyzeReportDescription', AnalyzeReportDescriptionComponent)
  .component('analyzeReportPreview', AnalyzeReportPreviewComponent)
  .component('analyzeReportSave', AnalyzeReportSaveComponent);
