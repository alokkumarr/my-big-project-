import angular from 'angular';
import 'angular-ui-grid';
import 'angular-ui-grid/ui-grid.css';

import {routesConfig} from './routes';

import {AnalyzeService} from './services/analyze.service';
import {ReportGridService} from './services/report-grid.service';

import {AnalyzePageComponent} from './components/analyze-page/analyze-page.component';
import {AnalyzeViewComponent} from './components/analyze-view/analyze-view.component';
import {AnalyzeCardComponent} from './components/analyze-card/analyze-card.component';
import {AnalyzeNewComponent} from './components/analyze-new/analyze-new.component';
import {AnalyzeReportComponent} from './components/analyze-report/analyze-report.component';
import {AnalyzeDialogComponent} from './components/analyze-dialog/analyze-dialog.component';
import {AnalyzeReportSortComponent} from './components/analyze-report-sort/analyze-report-sort.component';
import {ReportGridComponent} from './components/report-grid/report-grid.component';
import {ReportGridNodeComponent} from './components/report-grid/report-grid-node.component';
import {ReportGridContainerComponent} from './components/report-grid/report-grid-container.component';

export const AnalyzeModule = 'AnalyzeModule';

angular.module(AnalyzeModule, ['ui.grid'])
  .config(routesConfig)
  .factory('AnalyzeService', AnalyzeService)
  .factory('ReportGridService', ReportGridService)
  .component('reportGrid', ReportGridComponent)
  .component('reportGridNode', ReportGridNodeComponent)
  .component('reportGridContainer', ReportGridContainerComponent)
  .component('analyzePage', AnalyzePageComponent)
  .component('analyzeView', AnalyzeViewComponent)
  .component('analyzeCard', AnalyzeCardComponent)
  .component('analyzeNew', AnalyzeNewComponent)
  .component('analyzeReport', AnalyzeReportComponent)
  .component('analyzeDialog', AnalyzeDialogComponent)
  .component('analyzeReportSort', AnalyzeReportSortComponent);
