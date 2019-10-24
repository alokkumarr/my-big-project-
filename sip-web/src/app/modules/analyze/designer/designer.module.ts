import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AngularSplitModule } from 'angular-split';
import { NgxsModule } from '@ngxs/store';

import { CommonModuleTs } from '../../../common';
import { UChartModule } from '../../../common/components/charts';
import { MaterialModule } from '../../../material.module';
import { AceEditorModule } from 'ng2-ace-editor';
import { AnalyzeFilterModule } from '../designer/filter';
import { DesignerState } from '../designer/state/designer.state';

import { DesignerContainerComponent } from './container';
import { DesignerHeaderComponent } from './header';
import { DesignerToolbarComponent } from './toolbar';
import {
  DesignerPivotComponent,
  ArtifactColumns2PivotFieldsPipe
} from './pivot';
import { DesignerChartComponent } from './chart';
import { DesignerMapComponent } from './map';
import { DesignerMapChartComponent } from './map-chart';
import { DesignerReportComponent } from './report';
import {
  DesignerSettingsSingleTableComponent,
  DesignerSettingsMultiTableComponent,
  DesignerSettingsQueryComponent,
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
  DesignerMapChartOptionsComponent,
  DesignerMapOptionsComponent
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
import { AnalysisSubTypeChooserComponent } from './analysis-sub-type-chooser';
import {
  PerfectScrollbarModule,
  PerfectScrollbarConfigInterface,
  PERFECT_SCROLLBAR_CONFIG
} from 'ngx-perfect-scrollbar';
import { DerivedMetricComponent } from './derived-metric/derived-metric.component';
export { DesignerPageComponent, DesignerService };

const COMPONENTS = [
  AnalyzeReportQueryComponent,
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
  ToolbarActionDialogComponent,
  DesignerSortComponent,
  DesignerDescriptionComponent,
  DerivedMetricComponent,
  DesignerSaveComponent,
  DesignerPreviewDialogComponent,
  SingleTableDesignerLayoutComponent,
  MultiTableDesignerLayoutComponent,
  AnalysisSubTypeChooserComponent,
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
  DesignerMapChartOptionsComponent,
  DesignerMapOptionsComponent
];

const DEFAULT_PERFECT_SCROLLBAR_CONFIG: PerfectScrollbarConfigInterface = {
  wheelPropagation: true
};
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
    AngularSplitModule.forChild(),
    PerfectScrollbarModule,
    NgxsModule.forFeature([DesignerState])
  ],
  declarations: [...COMPONENTS, ...PIPES],
  entryComponents: COMPONENTS,
  providers: [
    ...SERVICES,
    {
      provide: PERFECT_SCROLLBAR_CONFIG,
      useValue: DEFAULT_PERFECT_SCROLLBAR_CONFIG
    }
  ],
  exports: COMPONENTS
})
export class AnalyzeDesignerModule {}
