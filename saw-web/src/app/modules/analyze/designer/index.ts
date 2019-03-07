import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AngularSplitModule } from 'angular-split';

import { CommonModuleTs } from '../../../common';
import { UChartModule } from '../../../common/components/charts';
import { MaterialModule } from '../../../material.module';
import { AceEditorModule } from 'ng2-ace-editor';
import { AnalyzeFilterModule } from '../designer/filter';

import { DesignerContainerComponent } from './container';
import { DesignerHeaderComponent } from './header';
import { DesignerToolbarComponent } from './toolbar';
import {
  DesignerPivotComponent,
  ArtifactColumns2PivotFieldsPipe
} from './pivot';
import { DesignerChartComponent } from './chart';
import { DesignerMapChartComponent } from './map-chart';
import { DesignerReportComponent } from './report';
import {
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
  DesignerSelectedFieldsComponent,
  DesignerAnalysisOptionsComponent,
  DesignerDataOptionFieldComponent,
  DesignerDateFormatSelectorComponent,
  DesignerDataFormatSelectorComponent,
  DesignerRegionSelectorComponent,
  DesignerComboTypeSelectorComponent,
  DesignerDateIntervalSelectorComponent,
  DesignerChartOptionsComponent,
  DesignerDataLimitSelectorComponent,
  DesignerMapChartOptionsComponent
} from './settings';
import { DesignerSortComponent } from './sort';
import {
  SingleTableDesignerLayoutComponent,
  MultiTableDesignerLayoutComponent
} from './layout';
import { AnalyzeReportQueryComponent } from './query';
import { DesignerSaveComponent } from './save';
import { DesignerDescriptionComponent } from './description';
import { DesignerPreviewDialogComponent } from './preview-dialog';
import { ToolbarActionDialogComponent } from './toolbar-action-dialog';
import { DesignerService } from './designer.service';
import { DesignerPageComponent } from './page/page.component';
import { ChartTypeChooserComponent } from './chart-type-chooser';

export {
  DesignerPageComponent,
  DesignerService
};

const COMPONENTS = [
  AnalyzeReportQueryComponent,
  DesignerContainerComponent,
  DesignerHeaderComponent,
  DesignerToolbarComponent,
  DesignerPivotComponent,
  DesignerChartComponent,
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
  ChartTypeChooserComponent,
  DesignerSelectedFieldsComponent,
  DesignerAnalysisOptionsComponent,
  DesignerDataOptionFieldComponent,
  DesignerDateFormatSelectorComponent,
  DesignerDataFormatSelectorComponent,
  DesignerRegionSelectorComponent,
  DesignerComboTypeSelectorComponent,
  DesignerDateIntervalSelectorComponent,
  DesignerChartOptionsComponent,
  DesignerDataLimitSelectorComponent,
  DesignerMapChartOptionsComponent
];

const SERVICES = [DesignerService];

const PIPES = [ArtifactColumns2PivotFieldsPipe];
@NgModule({
  imports: [
    CommonModuleTs,
    MaterialModule,
    AceEditorModule,
    FormsModule,
    ReactiveFormsModule,
    UChartModule,
    AnalyzeFilterModule,
    AngularSplitModule.forChild()
  ],
  declarations: [...COMPONENTS, ...PIPES],
  entryComponents: COMPONENTS,
  providers: SERVICES,
  exports: COMPONENTS
})
export class AnalyzeDesignerModule {}

