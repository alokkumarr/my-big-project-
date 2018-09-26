import { CommonModule as CommonModuleAngular4 } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule }  from '@angular/router';
import { LocalStorageModule } from 'angular-2-local-storage';
import { AnalyzeViewModule } from './view';
import { ExecutedViewModule } from './executed-view';
import { AnalyzeActionsModule } from './actions';

import { MaterialModule } from '../../material.module';
import { AceEditorModule } from 'ng2-ace-editor';

import { routes } from './routes';
import { BrowserModule } from '@angular/platform-browser';

import { FilterService } from './services/filter.service';
import { ChartService } from './services/chart.service';

import { AnalyzeActionsService } from './actions/analyze-actions.service';
import { DefaultAnalyzeCategoryGuard } from './guards';

import { CommonModuleTs } from '../../common';
import { UChartModule } from '../../common/components/charts';
import { AnalyzePublishDialogModule } from './publish';
import {
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
  DesignerPreviewDialogComponent,
  SingleTableDesignerLayout,
  MultiTableDesignerLayout,
  DesignerService,
  ArtifactColumns2PivotFieldsPipe
} from './designer';

import { AnalyzeFilterModule } from './designer/filter';

import { AnalyzeService } from './services/analyze.service';

import { AnalyzeDialogService } from './services/analyze-dialog.service';

import { AnalyzePageComponent } from './page';

const COMPONENTS = [
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
  DesignerPreviewDialogComponent,
  SingleTableDesignerLayout,
  MultiTableDesignerLayout,
  AnalyzePageComponent
];

const PIPES = [ArtifactColumns2PivotFieldsPipe];

const SERVICES = [
  AnalyzeDialogService,
  AnalyzeService,
  DesignerService,
  FilterService,
  ChartService,
  AnalyzeActionsService
];

const GUARDS = [DefaultAnalyzeCategoryGuard];
@NgModule({
  imports: [
    CommonModuleAngular4,
    LocalStorageModule.withConfig({
      prefix: 'symmetra',
      storageType: 'localStorage'
    }),
    RouterModule.forChild(routes),
    CommonModuleTs,
    MaterialModule,
    FlexLayoutModule,
    AceEditorModule,
    FormsModule,
    ReactiveFormsModule,
    UChartModule,
    BrowserModule,
    AnalyzeViewModule,
    ExecutedViewModule,
    AnalyzeActionsModule,
    AnalyzeFilterModule,
    AnalyzePublishDialogModule
  ],
  declarations: [
    ...COMPONENTS,
    ...PIPES
  ],
  entryComponents: COMPONENTS,
  providers: [
    ...SERVICES,
    ...GUARDS
  ]
})
export class AnalyzeModuleTs {}
