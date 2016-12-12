import angular from 'angular';

import {AnalyzeService} from './common/analyze.service';
import {AnalyzePageComponent} from './analyze-page/analyze-page.component';
import {AnalyzeViewComponent} from './analyze-view/analyze-view.component';
import {AnalyzeCardComponent} from './analyze-card/analyze-card.component';
import {AnalyzeNewComponent} from './analyze-new/analyze-new.component';
import {AnalyzeReportComponent} from './analyze-report/analyze-report.component';
import {AnalyzeDialogComponent} from './analyze-dialog/analyze-dialog.component';

export const AnalyzeModule = 'AnalyzeModule';

angular.module(AnalyzeModule, [])
  .component('analyzePage', AnalyzePageComponent)
  .component('analyzeView', AnalyzeViewComponent)
  .component('analyzeCard', AnalyzeCardComponent)
  .component('analyzeNew', AnalyzeNewComponent)
  .component('analyzeReport', AnalyzeReportComponent)
  .component('analyzeDialog', AnalyzeDialogComponent)
  .factory('AnalyzeService', AnalyzeService);
