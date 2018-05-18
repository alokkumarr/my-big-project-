import { CommonModule as CommonModuleAngular4 } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { downgradeComponent, downgradeInjectable } from '@angular/upgrade/static';
import * as angular from 'angular';
import { LocalStorageModule } from 'angular-2-local-storage';
import { AnalyzeViewModule } from './view';
import { AnalyzeActionsModule } from './actions';

import { MaterialModule } from '../../material.module';
import { AceEditorModule } from 'ng2-ace-editor';

import { routesConfig } from './routes';
import { i18nConfig } from './i18n';
import { BrowserModule } from '@angular/platform-browser';
import { OwlDateTimeModule, OwlNativeDateTimeModule } from 'ng-pick-datetime';

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
import { CronJobSchedularComponent } from './components/cron-job-schedular/cron-job-schedular.component';
import { CronDatePickerComponent } from './components/cron-date-picker/cron-date-picker.component';
import { CommonModule } from '../../common';

import { CommonModuleTs } from '../../common';
import { UChartModule } from '../../common/components/charts';
import {
  DesignerDialogComponent,
  DesignerContainerComponent,
  DesignerHeaderComponent,
  DesignerToolbarComponent,
  DesignerPivotComponent,
  DesignerChartComponent,
  DesignerReportComponent,
  DesignerSettingsSingleTableComponent,
  DesignerSettingsMultiTableComponent,
  DesignerSettingsQueryComponent,
  DesignerSettingsGroupComponent,
  DesignerSettingsAuxComponent,
  DesignerSettingsAuxChartComponent,
  ExpandableFieldComponent,
  ExpandDetailPivotComponent,
  ExpandDetailChartComponent,
  ToolbarActionDialogComponent,
  DesignerSortComponent,
  DesignerDescriptionComponent,
  DesignerSaveComponent,
  DesignerFilterContainerComponent,
  DesignerFilterRowComponent,
  DesignerStringFilterComponent,
  DesignerDateFilterComponent,
  DesignerNumberFilterComponent,
  DesignerPreviewDialogComponent,
  SingleTableDesignerLayout,
  MultiTableDesignerLayout,
  DesignerService,
  ArtifactColumns2PivotFieldsPipe
} from './components/designer';

import { FilterChipsComponent as FilterChipsUpgraded } from './components/filter/chips-u';
import { analyzeServiceProvider } from './services/ajs-analyze-providers';

import { AnalyzeDialogService } from './services/analyze-dialog.service';

export const AnalyzeModule = 'AnalyzeModule';

angular
  .module(AnalyzeModule, [CommonModule])
  .run(transitions)
  .config(routesConfig)
  .config(i18nConfig)
  .filter('pivotAreaTypeFilter', pivotAreaTypeFilter)
  .filter('uniqueFilter', uniqueFilter)
  .service('FilterService', FilterService)
  .service('AnalyzeService', AnalyzeService)
  .factory('PivotService', PivotService)
  .factory('ChartService', downgradeInjectable(ChartService) as Function)
  .factory('AnalyzeDialogService', downgradeInjectable(AnalyzeDialogService) as Function)
  .service('SortService', SortService)
  .component('aggregateChooser', AggregateChooserComponent)
  .component('reportGridContainer', ReportGridContainerComponent)
  .component('reportGridNode', ReportGridNodeComponent)
  .component('reportGrid', ReportGridComponent)
  .component('reportRenameDialog', ReportRenameDialogComponent)
  .component('reportFormatDialog', ReportFormatDialogComponent)
  .component('analyzePage', AnalyzePageComponent)
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
  .directive('analyzeReportQuery', downgradeComponent({
    component: AnalyzeReportQueryComponent
  }) as angular.IDirectiveFactory)
  .directive('cronJobSchedular', downgradeComponent({
    component: CronJobSchedularComponent
  }) as angular.IDirectiveFactory)
  .component('analyzeDialog', AnalyzeDialogComponent)
  .component('analyzeSortDialog', AnalyzeSortDialogComponent)
  .component('stringFilter', StringFilterComponent)
  .component('numberFilter', NumberFilterComponent)
  .component('dateFilter', DateFilterComponent)
  .component('filterChips', FilterChipsComponent)
  .directive('filterChipsU', downgradeComponent({
    component: FilterChipsUpgraded
  }) as angular.IDirectiveFactory)
  .component('analyzeFilterRow', AnalyzeFilterRowComponent)
  .component('analyzeFilterModal', AnalyzeFilterModalComponent)
  .component('analyzeDescriptionDialog', AnalyzeDescriptionDialogComponent)
  .component('analyzeReportPreview', AnalyzeReportPreviewComponent)
  .component('analyzePublishDialog', AnalyzePublishDialogComponent)
  .component('analyzeChart', AnalyzeChartComponent)
  .component('analyzeChartSettings', AnalyzeChartSettingsComponent)
  .component('analyzeChartPreview', AnalyzeChartPreviewComponent)
  .component('analyzeSaveDialog', AnalyzeSaveDialogComponent);

@NgModule({
  imports: [
    CommonModuleAngular4,
    LocalStorageModule.withConfig({
      prefix: 'symmetra',
      storageType: 'localStorage'
    }),
    CommonModuleTs,
    MaterialModule,
    FlexLayoutModule,
    AceEditorModule,
    FormsModule,
    ReactiveFormsModule,
    UChartModule,
    BrowserModule,
    OwlDateTimeModule,
    OwlNativeDateTimeModule,
    AnalyzeViewModule,
    AnalyzeViewModule,
    AnalyzeActionsModule
  ],
  declarations: [
    AnalyzeReportQueryComponent,
    DesignerDialogComponent,
    DesignerContainerComponent,
    DesignerHeaderComponent,
    DesignerToolbarComponent,
    DesignerPivotComponent,
    DesignerChartComponent,
    DesignerReportComponent,
    DesignerSettingsSingleTableComponent,
    DesignerSettingsMultiTableComponent,
    DesignerSettingsQueryComponent,
    DesignerSettingsGroupComponent,
    DesignerSettingsAuxComponent,
    DesignerSettingsAuxChartComponent,
    ExpandableFieldComponent,
    ExpandDetailPivotComponent,
    ExpandDetailChartComponent,
    ToolbarActionDialogComponent,
    DesignerSortComponent,
    DesignerDescriptionComponent,
    DesignerSaveComponent,
    DesignerFilterContainerComponent,
    DesignerFilterRowComponent,
    DesignerStringFilterComponent,
    DesignerDateFilterComponent,
    DesignerNumberFilterComponent,
    DesignerPreviewDialogComponent,
    ArtifactColumns2PivotFieldsPipe,
    CronJobSchedularComponent,
    CronDatePickerComponent,
    SingleTableDesignerLayout,
    MultiTableDesignerLayout,
    FilterChipsUpgraded,
    CronDatePickerComponent
  ],
  entryComponents: [
    AnalyzeReportQueryComponent,
    DesignerDialogComponent,
    DesignerContainerComponent,
    DesignerHeaderComponent,
    DesignerToolbarComponent,
    DesignerPivotComponent,
    DesignerChartComponent,
    DesignerReportComponent,
    ExpandableFieldComponent,
    ExpandDetailPivotComponent,
    ExpandDetailChartComponent,
    DesignerSettingsSingleTableComponent,
    DesignerSettingsMultiTableComponent,
    DesignerSettingsQueryComponent,
    DesignerSettingsGroupComponent,
    DesignerSettingsAuxComponent,
    DesignerSettingsAuxChartComponent,
    ToolbarActionDialogComponent,
    DesignerSortComponent,
    DesignerDescriptionComponent,
    DesignerSaveComponent,
    DesignerFilterContainerComponent,
    DesignerFilterRowComponent,
    DesignerStringFilterComponent,
    DesignerDateFilterComponent,
    DesignerNumberFilterComponent,
    DesignerPreviewDialogComponent,
    CronJobSchedularComponent,
    CronDatePickerComponent,
    SingleTableDesignerLayout,
    MultiTableDesignerLayout,
    FilterChipsUpgraded,
    CronDatePickerComponent
  ],
  providers: [
    $mdDialogProvider,
    AnalyzeDialogService,
    analyzeServiceProvider,
    DesignerService,
    ChartService
  ]
})
export class AnalyzeModuleTs {}
