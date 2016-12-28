import angular from 'angular';

import {routesConfig} from './routes';

import {AnalyzeService} from './services/analyze.service';

import {AnalyzePageComponent} from './components/analyze-page/analyze-page.component';
import {AnalyzeViewComponent} from './components/analyze-view/analyze-view.component';
import {AnalyzeCardComponent} from './components/analyze-card/analyze-card.component';
import {AnalyzeNewComponent} from './components/analyze-new/analyze-new.component';
import {AnalyzeReportComponent} from './components/analyze-report/analyze-report.component';
import {AnalyzeDialogComponent} from './components/analyze-dialog/analyze-dialog.component';

export const AnalyzeModule = 'AnalyzeModule';

angular.module(AnalyzeModule, [])
  .config(routesConfig)
  .factory('AnalyzeService', AnalyzeService)
  .component('analyzePage', AnalyzePageComponent)
  .component('analyzeView', AnalyzeViewComponent)
  .component('analyzeCard', AnalyzeCardComponent)
  .component('analyzeNew', AnalyzeNewComponent)
  .component('analyzeReport', AnalyzeReportComponent)
  .component('analyzeDialog', AnalyzeDialogComponent);
