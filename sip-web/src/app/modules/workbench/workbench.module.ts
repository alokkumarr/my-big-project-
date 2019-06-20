import { NgModule } from '@angular/core';
import { CommonModule as AngularCommonModule } from '@angular/common';
import { MaterialModule } from '../../material.module';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DxTemplateModule } from 'devextreme-angular/core/template';
import { DxDataGridModule } from 'devextreme-angular/ui/data-grid';
import { TreeModule } from 'angular-tree-component';
import { AceEditorModule } from 'ng2-ace-editor';
import { RouterModule } from '@angular/router';
import { AngularSplitModule } from 'angular-split';
import { NgxsModule } from '@ngxs/store';

import { routes } from './routes';
import { WorkbenchPageComponent } from './components/workbench-page/workbench-page.component';
import { CreateDatasetsComponent } from './components/create-datasets/create-datasets.component';
import { SelectRawdataComponent } from './components/create-datasets/select-rawdata/select-rawdata.component';
import { DatasetDetailsComponent } from './components/create-datasets/dataset-details/dataset-details.component';
import { RawpreviewDialogComponent } from './components/create-datasets/rawpreview-dialog/rawpreview-dialog.component';
import { ParserPreviewComponent } from './components/create-datasets/parser-preview/parser-preview.component';
import { DateformatDialogComponent } from './components/create-datasets/dateformat-dialog/dateformat-dialog.component';
import { SqlExecutorComponent } from './components/sql-executor/sql-executor.component';
import { SqlScriptComponent } from './components/sql-executor/query/sql-script.component';
import { SqlpreviewGridPageComponent } from './components/sql-executor/preview-grid/sqlpreview-grid-page.component';
import { DetailsDialogComponent } from './components/sql-executor/dataset-details-dialog/details-dialog.component';
import { DatasetDetailViewComponent } from './components/dataset-detailedView/dataset-detail-view.component';
import {
  CreateSemanticComponent,
  ValidateSemanticComponent,
  SemanticDetailsDialogComponent,
  UpdateSemanticComponent
} from './components/semantic-management/index';
import {
  DataobjectsComponent,
  DatapodsCardPageComponent,
  DatapodsGridPageComponent,
  DatasetsCardPageComponent,
  DatasetsGridPageComponent,
  DatasetActionsComponent,
  DatapodActionsComponent
} from './components/data-objects-view/index';
import {
  DatasourceComponent,
  CreateSourceDialogComponent,
  TestConnectivityComponent,
  CreateRouteDialogComponent,
  ConfirmActionDialogComponent,
  LogsDialogComponent,
  SourceFolderDialogComponent
} from './components/datasource-management/index';

import {
  JobsPageComponent,
  JobLogsPageComponent,
  JobFiltersComponent
} from './components/jobs';

import { DefaultWorkbenchPageGuard } from './guards';
import { IsAdminGuard } from '../admin/guards';

import { CommonModuleTs } from '../../common';
import { WorkbenchState } from './state/workbench.state';

const COMPONENTS = [
  WorkbenchPageComponent,
  DataobjectsComponent,
  DatasetsCardPageComponent,
  DatasetsGridPageComponent,
  CreateDatasetsComponent,
  SelectRawdataComponent,
  DatasetDetailsComponent,
  RawpreviewDialogComponent,
  ParserPreviewComponent,
  DateformatDialogComponent,
  DatasetActionsComponent,
  SqlExecutorComponent,
  SqlScriptComponent,
  SqlpreviewGridPageComponent,
  DetailsDialogComponent,
  DatasetDetailViewComponent,
  CreateSemanticComponent,
  ValidateSemanticComponent,
  SemanticDetailsDialogComponent,
  UpdateSemanticComponent,
  DatapodsCardPageComponent,
  DatapodsGridPageComponent,
  DatapodActionsComponent,
  DatasourceComponent,
  LogsDialogComponent,
  CreateSourceDialogComponent,
  TestConnectivityComponent,
  CreateRouteDialogComponent,
  ConfirmActionDialogComponent,
  SourceFolderDialogComponent,
  JobsPageComponent,
  JobLogsPageComponent,
  JobFiltersComponent
];

const GUARDS = [DefaultWorkbenchPageGuard, IsAdminGuard];
@NgModule({
  imports: [
    AngularCommonModule,
    FormsModule,
    MaterialModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
    DxDataGridModule,
    DxTemplateModule,
    FlexLayoutModule,
    TreeModule,
    AceEditorModule,
    CommonModuleTs,
    AngularSplitModule.forChild(),
    NgxsModule.forFeature([WorkbenchState])
  ],
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  providers: [GUARDS]
})
export class WorkbenchModule {}
