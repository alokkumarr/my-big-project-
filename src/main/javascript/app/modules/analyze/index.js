import angular from 'angular';

import {routesConfig} from './routes';
import {i18nConfig} from './i18n';

import {transitions} from './transitions';

import {AnalyzeService} from './services/analyze.service';
import {FilterService} from './services/filter.service';
import {PivotService} from './services/pivot.service';
import {ChartService} from './services/chart.service';

import {AnalyzePageComponent} from './components/analyze-page/analyze-page.component';
import {AnalyzeViewComponent} from './components/analyze-view/analyze-view.component';
import {AnalyzePublishedListComponent} from './components/analyze-published-list/analyze-published-list.component';
import {AnalyzeReportDetailComponent} from './components/analyze-published-detail/report/analyze-report-detail.component';
import {AnalyzePivotDetailComponent} from './components/analyze-published-detail/pivot/analyze-pivot-detail.component';
import {AnalyzeChartDetailComponent} from './components/analyze-published-detail/chart/analyze-chart-detail.component';
import {AnalyzePublishedDetailComponent} from './components/analyze-published-detail/analyze-published-detail.component';
import {AnalyzeCardComponent} from './components/analyze-card/analyze-card.component';
import {AnalyzeNewComponent} from './components/analyze-new/analyze-new.component';
import {AnalyzePivotSettingsComponent} from './components/analyze-pivot-settings/analyze-pivot-settings.component';
import {AnalyzePivotPreviewComponent} from './components/analyze-pivot-preview/analyze-pivot-preview.component';
import {PivotGridComponent} from './components/analyze-pivot-grid/pivot-grid.component';
import {AnalyzePivotComponent} from './components/analyze-pivot/analyze-pivot.component';
import {AnalyzeReportComponent} from './components/analyze-report/analyze-report.component';
import {AnalyzeDialogComponent} from './components/analyze-dialog/analyze-dialog.component';
import {AnalyzeReportSortComponent} from './components/analyze-report-sort/analyze-report-sort.component';
import {AnalyzeReportDescriptionComponent} from './components/analyze-report-description/analyze-report-description.component';
import {AnalyzeReportPreviewComponent} from './components/analyze-report-preview/analyze-report-preview.component';
import {ReportGridDisplayComponent} from './components/analyze-report-grid-display/grid/report-grid-display.component';
import {ReportGridDisplayNodeComponent} from './components/analyze-report-grid-display/node/report-grid-display-node.component';
import {ReportGridDisplayContainerComponent} from './components/analyze-report-grid-display/container/report-grid-display-container.component';
import {ReportGridComponent} from './components/analyze-report-grid/report-grid/report-grid.component';
import {ReportGridNodeComponent} from './components/analyze-report-grid/report-grid-node/report-grid-node.component';
import {ReportGridContainerComponent} from './components/analyze-report-grid/report-grid-container/report-grid-container.component';
import {AnalyzeFilterContainerComponent} from './components/analyze-filter-sidenav/analyze-filter-container.component';
import {AnalyzeFilterGroupComponent} from './components/analyze-filter-sidenav/analyze-filter-group.component';
import {AnalyzeFilterSidenavComponent} from './components/analyze-filter-sidenav/analyze-filter-sidenav.component';
import {StringFilterComponent} from './components/analyze-filter-sidenav/filters/string-filter.component';
import {NumberFilterComponent} from './components/analyze-filter-sidenav/filters/number-filter.component';
import {ReportRenameDialogComponent} from './components/analyze-report-grid/report-rename-dialog/report-rename-dialog.component';
import {AnalyzeReportSaveComponent} from './components/analyze-report-save/analyze-report-save.component';
import {AnalyzePublishDialogComponent} from './components/analyze-publish-dialog/analyze-publish-dialog.component';
import {AnalyzeChartComponent} from './components/analyze-chart/analyze-chart.component';
import {AnalyzeChartSettingsComponent} from './components/analyze-chart-settings/analyze-chart-settings.component';
import {AnalyzeChartPreviewComponent} from './components/analyze-chart-preview//analyze-chart-preview.component';

export const AnalyzeModule = 'AnalyzeModule';

angular.module(AnalyzeModule, [])
  .run(transitions)
  .config(routesConfig)
  .config(i18nConfig)
  .factory('FilterService', FilterService)
  .factory('AnalyzeService', AnalyzeService)
  .factory('PivotService', PivotService)
  .factory('ChartService', ChartService)
  .component('reportGridDisplay', ReportGridDisplayComponent)
  .component('reportGridDisplayNode', ReportGridDisplayNodeComponent)
  .component('reportGridDisplayContainer', ReportGridDisplayContainerComponent)
  .component('reportGridContainer', ReportGridContainerComponent)
  .component('reportGridNode', ReportGridNodeComponent)
  .component('reportGrid', ReportGridComponent)
  .component('reportRenameDialog', ReportRenameDialogComponent)
  .component('analyzePage', AnalyzePageComponent)
  .component('analyzeView', AnalyzeViewComponent)
  .component('analyzePublishedList', AnalyzePublishedListComponent)
  .component('analyzeReportDetail', AnalyzeReportDetailComponent)
  .component('analyzePivotDetail', AnalyzePivotDetailComponent)
  .component('analyzeChartDetail', AnalyzeChartDetailComponent)
  .component('analyzePublishedDetail', AnalyzePublishedDetailComponent)
  .component('analyzeCard', AnalyzeCardComponent)
  .component('analyzeNew', AnalyzeNewComponent)
  .component('analyzePivotSettings', AnalyzePivotSettingsComponent)
  .component('analyzePivotPreview', AnalyzePivotPreviewComponent)
  .component('pivotGrid', PivotGridComponent)
  .component('analyzePivot', AnalyzePivotComponent)
  .component('analyzeReport', AnalyzeReportComponent)
  .component('analyzeDialog', AnalyzeDialogComponent)
  .component('analyzeReportSort', AnalyzeReportSortComponent)
  .component('analyzeFilterContainer', AnalyzeFilterContainerComponent)
  .component('analyzeFilterGroup', AnalyzeFilterGroupComponent)
  .component('stringFilter', StringFilterComponent)
  .component('numberFilter', NumberFilterComponent)
  .component('analyzeFilterSidenav', AnalyzeFilterSidenavComponent)
  .component('analyzeReportDescription', AnalyzeReportDescriptionComponent)
  .component('analyzeReportPreview', AnalyzeReportPreviewComponent)
  .component('analyzePublishDialog', AnalyzePublishDialogComponent)
  .component('analyzeChart', AnalyzeChartComponent)
  .component('analyzeChartSettings', AnalyzeChartSettingsComponent)
  .component('analyzeChartPreview', AnalyzeChartPreviewComponent)
  .component('analyzeReportSave', AnalyzeReportSaveComponent);
