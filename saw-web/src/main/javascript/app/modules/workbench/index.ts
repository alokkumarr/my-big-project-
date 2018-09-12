import { NgModule } from '@angular/core';
import { CommonModule as AngularCommonModule } from '@angular/common';
import { MaterialModule } from '../../material.module';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DxDataGridModule, DxTemplateModule } from 'devextreme-angular';
import { TreeModule } from 'angular-tree-component';
import { AceEditorModule } from 'ng2-ace-editor';
import { AngularSplitModule } from 'angular-split';
import { RouterModule }  from '@angular/router';

import { routes } from './routes';

import { WorkbenchService } from './services/workbench.service';
import { AnalyzeService } from '../analyze/services/analyze.service';
import {
  MenuService,
  ComponentHandler,
  HeaderProgressService,
  ToastService,
  SideNavService,
  LocalSearchService,
  dxDataGridService
} from '../../common/services';

import { JwtService } from '../../../login/services/jwt.service';

import { WorkbenchPageComponent } from './components/workbench-page/workbench-page.component';
import { CreateDatasetsComponent } from './components/create-datasets/create-datasets.component';
import { SelectRawdataComponent } from './components/create-datasets/select-rawdata/select-rawdata.component';
import { DatasetDetailsComponent } from './components/create-datasets/dataset-details/dataset-details.component';
import { RawpreviewDialogComponent } from './components/create-datasets/rawpreview-dialog/rawpreview-dialog.component';
import { ParserPreviewComponent } from './components/create-datasets/parser-preview/parser-preview.component';
import { DateformatDialogComponent } from './components/create-datasets/dateformat-dialog/dateformat-dialog.component';
import { CreatefolderDialogComponent } from './components/create-datasets/createFolder-dialog/createfolder-dialog.component';
import { SqlExecutorComponent } from './components/sql-executor/sql-executor.component';
import { SqlScriptComponent } from './components/sql-executor/query/sql-script.component';
import { SqlpreviewGridPageComponent } from './components/sql-executor/preview-grid/sqlpreview-grid-page.component';
import { DetailsDialogComponent } from './components/sql-executor/dataset-details-dialog/details-dialog.component';
import { DatasetDetailViewComponent } from './components/dataset-detailedView/dataset-detail-view.component';
import { CreateSemanticComponent } from './components/semantic-management/create/create-semantic.component';
import { ValidateSemanticComponent } from './components/semantic-management/validate/validate-semantic.component';
import { SemanticDetailsDialogComponent } from './components/semantic-management/semantic-details-dialog/semantic-details-dialog.component';
import { UpdateSemanticComponent } from './components/semantic-management/update/update-semantic.component';
import {
  DataobjectsComponent,
  DatapodsCardPageComponent,
  DatapodsGridPageComponent,
  DatasetsCardPageComponent,
  DatasetsGridPageComponent,
  DatasetActionsComponent,
  DatapodActionsComponent
} from './components/data-objects-view/index';

import { DefaultWorkbenchPageGuard } from './guards';

import { CommonModuleTs } from '../../common';

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
  CreatefolderDialogComponent,
  DatasetDetailViewComponent,
  CreateSemanticComponent,
  ValidateSemanticComponent,
  SemanticDetailsDialogComponent,
  UpdateSemanticComponent,
  DatapodsCardPageComponent,
  DatapodsGridPageComponent,
  DatapodActionsComponent
];

const GUARDS = [DefaultWorkbenchPageGuard];

const SERVICES = [
  JwtService,
  WorkbenchService,
  AnalyzeService,
  MenuService,
  ComponentHandler,
  HeaderProgressService,
  ToastService,
  SideNavService,
  LocalSearchService,
  dxDataGridService
];
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
    AngularSplitModule,
    CommonModuleTs
  ],
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  providers: [
    ...SERVICES,
    ...GUARDS
  ]
})
export class WorkbenchUpgradeModule {}
