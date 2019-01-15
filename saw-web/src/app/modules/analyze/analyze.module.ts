import { CommonModule as CommonModuleAngular4 } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { LocalStorageModule } from 'angular-2-local-storage';
import { AnalyzeViewModule } from './view';
import { ExecutedViewModule } from './executed-view';
import { AnalyzeActionsModule } from './actions';

import { MaterialModule } from '../../material.module';
import { AceEditorModule } from 'ng2-ace-editor';

import { routes } from './routes';

import { DefaultAnalyzeCategoryGuard } from './guards';

import { CommonModuleTs } from '../../common';
import { UChartModule } from '../../common/components/charts';
import { AnalyzePublishDialogModule } from './publish';
import { AnalyzeModuleGlobal } from './analyze.global.module';
import {
  AnalyzeReportQueryComponent,
  DesignerPageComponent,
  DesignerContainerComponent,
  DesignerHeaderComponent,
  DesignerToolbarComponent,
  DesignerPivotComponent,
  DesignerChartComponent,
  DesignerMapComponent,
  DesignerMapChartComponent,
  DesignerReportComponent,
  DesignerSettingsSingleTableComponent,
  DesignerSettingsMultiTableComponent,
  DesignerSettingsQueryComponent,
  DesignerSettingsGroupComponent,
  DesignerSettingsAuxComponent,
  DesignerSettingsAuxChartComponent,
  DesignerSettingsAuxMapChartComponent,
  ExpandableFieldComponent,
  ExpandDetailPivotComponent,
  ExpandDetailChartComponent,
  ToolbarActionDialogComponent,
  DesignerSortComponent,
  DesignerDescriptionComponent,
  DesignerSaveComponent,
  DesignerPreviewDialogComponent,
  SingleTableDesignerLayoutComponent,
  MultiTableDesignerLayoutComponent,
  ArtifactColumns2PivotFieldsPipe
} from './designer';

import { AnalyzeFilterModule } from './designer/filter';

import { AnalyzePageComponent } from './page';

const COMPONENTS = [
  AnalyzeReportQueryComponent,
  DesignerPageComponent,
  DesignerContainerComponent,
  DesignerHeaderComponent,
  DesignerToolbarComponent,
  DesignerPivotComponent,
  DesignerChartComponent,
  DesignerMapComponent,
  DesignerMapChartComponent,
  DesignerReportComponent,
  DesignerSettingsSingleTableComponent,
  DesignerSettingsMultiTableComponent,
  DesignerSettingsQueryComponent,
  DesignerSettingsGroupComponent,
  DesignerSettingsAuxComponent,
  DesignerSettingsAuxChartComponent,
  DesignerSettingsAuxMapChartComponent,
  ExpandableFieldComponent,
  ExpandDetailPivotComponent,
  ExpandDetailChartComponent,
  ToolbarActionDialogComponent,
  DesignerSortComponent,
  DesignerDescriptionComponent,
  DesignerSaveComponent,
  DesignerPreviewDialogComponent,
  SingleTableDesignerLayoutComponent,
  MultiTableDesignerLayoutComponent,
  AnalyzePageComponent
];

const PIPES = [ArtifactColumns2PivotFieldsPipe];

const GUARDS = [DefaultAnalyzeCategoryGuard];

@NgModule({
  imports: [
    CommonModuleAngular4,
    LocalStorageModule.withConfig({
      prefix: 'symmetra',
      storageType: 'localStorage'
    }),
    AnalyzeModuleGlobal.forRoot(),
    RouterModule.forChild(routes),
    CommonModuleTs,
    MaterialModule,
    FlexLayoutModule,
    AceEditorModule,
    FormsModule,
    ReactiveFormsModule,
    UChartModule,
    AnalyzeViewModule,
    ExecutedViewModule,
    AnalyzeActionsModule,
    AnalyzeFilterModule,
    AnalyzePublishDialogModule
  ],
  declarations: [...COMPONENTS, ...PIPES],
  entryComponents: COMPONENTS,
  providers: [...GUARDS],
  exports: [AnalyzePageComponent]
})
export class AnalyzeModule {}
