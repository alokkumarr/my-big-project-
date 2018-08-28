import * as angular from 'angular';
import { UIRouterUpgradeModule } from '@uirouter/angular-hybrid';
import { NgModule } from '@angular/core';
import { CommonModule as AngularCommonModule } from '@angular/common';
import { MaterialModule } from '../../material.module';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DxDataGridModule, DxTemplateModule } from 'devextreme-angular';
import { TreeModule } from 'angular-tree-component';
import { AceEditorModule } from 'ng2-ace-editor';
import { AngularSplitModule } from 'angular-split';

import { routesConfig } from './routes';
import { i18nConfig } from './i18n';

import { WorkbenchService } from './services/workbench.service';
import { analyzeServiceProvider } from '../analyze/services/ajs-analyze-providers';
import {
  menuServiceProvider,
  componentHandlerProvider,
  headerProgressProvider,
  toastProvider,
  sidenavProvider,
  localSearchProvider,
  dxDataGridProvider
} from '../../common/services/ajs-common-providers';

import { jwtServiceProvider } from '../../../login/services/ajs-login-providers';

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

import { CommonModule, CommonModuleTs } from '../../common';

export const WorkbenchModule = 'WorkbenchModule';

angular
  .module(WorkbenchModule, [CommonModule])
  .config(routesConfig)
  .config(i18nConfig);

const components = [
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

@NgModule({
  imports: [
    AngularCommonModule,
    FormsModule,
    MaterialModule,
    ReactiveFormsModule,
    UIRouterUpgradeModule,
    DxDataGridModule,
    DxTemplateModule,
    FlexLayoutModule,
    TreeModule,
    AceEditorModule,
    AngularSplitModule,
    CommonModuleTs
  ],
  declarations: components,
  entryComponents: components,
  providers: [
    WorkbenchService,
    menuServiceProvider,
    analyzeServiceProvider,
    componentHandlerProvider,
    headerProgressProvider,
    toastProvider,
    sidenavProvider,
    localSearchProvider,
    dxDataGridProvider,
    jwtServiceProvider
  ]
})
export class WorkbenchUpgradeModule {}
