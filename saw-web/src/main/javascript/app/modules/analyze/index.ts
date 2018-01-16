import * as angular from 'angular';
import { downgradeComponent } from '@angular/upgrade/static';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms'

import { MaterialModule } from '../../material.module';

import { routesConfig } from './routes';
import { i18nConfig } from './i18n';

import { transitions } from './transitions';

import { AnalyzeService } from './services/analyze.service';
import { FilterService } from './services/filter.service';
import { PivotService } from './services/pivot.service';
import { ChartService } from './services/chart.service';
import { SortService } from './services/sort.service';

import { $mdDialogProvider } from '../../common/services/ajs-common-providers';

import { pivotAreaTypeFilter } from './filters/pivot-area-type.filter';
import { uniqueFilter } from './filters/unique.filter';

import { AnalyzePageComponent } from './components/page/analyze-page.component';
import { AggregateChooserComponent } from './components/aggregate-chooser/aggregate-chooser.component';
import { AnalyzeActionsService } from './components/actions/analyze-actions.service';
import { AnalyzeActionsMenuComponent } from './components/actions/analyze-actions-menu.component';
import { AnalyzeViewComponent } from './components/view/analyze-view.component';
import { AnalyzeCardsViewComponent } from './components/view/card/analyze-cards-view.component';
import { AnalyzeCardComponent } from './components/view/card/analyze-card.component';
import { AnalyzeListViewComponent } from './components/view/list/analyze-list-view.component';
import { AnalyzeExecutedListComponent } from './components/executed-list/analyze-executed-list.component';
import { AnalyzeReportDetailComponent } from './components/executed-detail/report/analyze-report-detail.component';
import { AnalyzePivotDetailComponent } from './components/executed-detail/pivot/analyze-pivot-detail.component';
import { AnalyzeChartDetailComponent } from './components/executed-detail/chart/analyze-chart-detail.component';
import { AnalyzeExecutedDetailComponent } from './components/executed-detail/analyze-executed-detail.component';
import { AnalyzeNewComponent } from './components/new/analyze-new.component';
import { AnalyzePivotSettingsComponent } from './components/pivot/settings/analyze-pivot-settings.component';
import { AnalyzePivotPreviewComponent } from './components/pivot/preview/analyze-pivot-preview.component';
import { AnalyzePivotComponent } from './components/pivot/analyze-pivot.component';
import { AnalyzeReportComponent } from './components/report/analyze-report.component';
import { AnalyzeReportQueryComponent } from './components/report/query/analyze-report-query.component';
import { AnalyzeDialogComponent } from './components/dialog/analyze-dialog.component';
import { AnalyzeSortDialogComponent } from './components/sort-dialog/analyze-sort-dialog.component';
import { AnalyzeDescriptionDialogComponent } from './components/description-dialog/analyze-description-dialog.component';
import { AnalyzeReportPreviewComponent } from './components/report/preview/analyze-report-preview.component';
import { ReportGridComponent } from './components/report/grid/report-grid/report-grid.component';
import { ReportGridNodeComponent } from './components/report/grid/report-grid-node/report-grid-node.component';
import { ReportGridContainerComponent } from './components/report/grid/report-grid-container/report-grid-container.component';
import { StringFilterComponent } from './components/filter/filters/string-filter.component';
import { NumberFilterComponent } from './components/filter/filters/number-filter.component';
import { DateFilterComponent } from './components/filter/filters/date-filter.component';
import { FilterChipsComponent } from './components/filter/chips/filter-chips.component';
import { AnalyzeFilterRowComponent } from './components/filter/row/analyze-filter-row.component';
import { ReportFormatDialogComponent } from './components/report/grid/report-format-dialog/report-format-dialog.component';
import { AnalyzeFilterModalComponent } from './components/filter/modal/analyze-filter-modal.component';
import { ReportRenameDialogComponent } from './components/report/grid/report-rename-dialog/report-rename-dialog.component';
import { AnalyzeSaveDialogComponent } from './components/save-dialog/analyze-save-dialog.component';
import { AnalyzePublishDialogComponent } from './components/publish-dialog/analyze-publish-dialog.component';
import { AnalyzeChartComponent } from './components/chart/analyze-chart.component';
import { AnalyzeChartSettingsComponent } from './components/chart/settings/analyze-chart-settings.component';
import { AnalyzeChartPreviewComponent } from './components/chart/preview/analyze-chart-preview.component';
import { CommonModule } from '../../common';

export const AnalyzeModule = 'AnalyzeModule';

angular.module(AnalyzeModule, [
  CommonModule
])
  .run(transitions)
  .config(routesConfig)
  .config(i18nConfig)
  .filter('pivotAreaTypeFilter', pivotAreaTypeFilter)
  .filter('uniqueFilter', uniqueFilter)
  .service('FilterService', FilterService)
  .service('AnalyzeService', AnalyzeService)
  .factory('PivotService', PivotService)
  .service('ChartService', ChartService)
  .service('SortService', SortService)
  .factory('AnalyzeActionsService', AnalyzeActionsService)
  .component('analyzeActionsMenu', AnalyzeActionsMenuComponent)
  .component('aggregateChooser', AggregateChooserComponent)
  .component('reportGridContainer', ReportGridContainerComponent)
  .component('reportGridNode', ReportGridNodeComponent)
  .component('reportGrid', ReportGridComponent)
  .component('reportRenameDialog', ReportRenameDialogComponent)
  .component('reportFormatDialog', ReportFormatDialogComponent)
  .component('analyzePage', AnalyzePageComponent)
  .component('analyzeView', AnalyzeViewComponent)
  .component('analyzeCardsView', AnalyzeCardsViewComponent)
  .component('analyzeCard', AnalyzeCardComponent)
  .component('analyzeListView', AnalyzeListViewComponent)
  .component('analyzeExecutedList', AnalyzeExecutedListComponent)
  .component('analyzeReportDetail', AnalyzeReportDetailComponent)
  .component('analyzePivotDetail', AnalyzePivotDetailComponent)
  .component('analyzeChartDetail', AnalyzeChartDetailComponent)
  .component('analyzeExecutedDetail', AnalyzeExecutedDetailComponent)
  .component('analyzeNew', AnalyzeNewComponent)
  .component('analyzePivotSettings', AnalyzePivotSettingsComponent)
  .component('analyzePivotPreview', AnalyzePivotPreviewComponent)
  .component('analyzePivot', AnalyzePivotComponent)
  .component('analyzeReport', AnalyzeReportComponent)
  .directive(
    'analyzeReportQuery',
    downgradeComponent({component: AnalyzeReportQueryComponent}) as angular.IDirectiveFactory
  )
  .component('analyzeDialog', AnalyzeDialogComponent)
  .component('analyzeSortDialog', AnalyzeSortDialogComponent)
  .component('stringFilter', StringFilterComponent)
  .component('numberFilter', NumberFilterComponent)
  .component('dateFilter', DateFilterComponent)
  .component('filterChips', FilterChipsComponent)
  .component('analyzeFilterRow', AnalyzeFilterRowComponent)
  .component('analyzeFilterModal', AnalyzeFilterModalComponent)
  .component('analyzeDescriptionDialog', AnalyzeDescriptionDialogComponent)
  .component('analyzeReportPreview', AnalyzeReportPreviewComponent)
  .component('analyzePublishDialog', AnalyzePublishDialogComponent)
  .component('analyzeChart', AnalyzeChartComponent)
  .component('analyzeChartSettings', AnalyzeChartSettingsComponent)
  .component('analyzeChartPreview', AnalyzeChartPreviewComponent)
  .component('analyzeSaveDialog', AnalyzeSaveDialogComponent);

const components = [
  AnalyzeReportQueryComponent
];

@NgModule({
  imports: [
    MaterialModule,
    FormsModule
  ],
  exports: [],
  declarations: components,
  providers: [
    $mdDialogProvider
  ],
  entryComponents: components
})
export class AnalyzeUpgradeModule { }
