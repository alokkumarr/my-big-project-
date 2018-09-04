import { NgModule } from '@angular/core';
import { CommonModule as AngularCommonModule } from '@angular/common';
import { MaterialModule } from '../../material.module';
import { FlexLayoutModule } from '@angular/flex-layout'
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DxDataGridModule, DxTemplateModule } from 'devextreme-angular';
import { TreeModule } from 'angular-tree-component';
import { AceEditorModule } from 'ng2-ace-editor';
import { AngularSplitModule } from 'angular-split';
import { UIRouterModule } from '@uirouter/angular';

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

import { WorkbenchPageComponent } from './components/workbench-page/workbench-page.component'
import { DatasetsComponent } from './components/datasets-view/datasets-page.component';
import { DatasetsGridPageComponent } from './components/datasets-view/grid/datasets-grid-page.component';
import { DatasetsCardPageComponent } from './components/datasets-view/card/datasets-card-page.component';
import { CreateDatasetsComponent } from './components/create-datasets/create-datasets.component';
import { SelectRawdataComponent } from './components/create-datasets/select-rawdata/select-rawdata.component';
import { DatasetDetailsComponent } from './components/create-datasets/dataset-details/dataset-details.component';
import { RawpreviewDialogComponent } from './components/create-datasets/rawpreview-dialog/rawpreview-dialog.component';
import { ParserPreviewComponent } from './components/create-datasets/parser-preview/parser-preview.component';
import { DateformatDialogComponent } from './components/create-datasets/dateformat-dialog/dateformat-dialog.component';
import { CreatefolderDialogComponent } from './components/create-datasets/createFolder-dialog/createfolder-dialog.component';
import { DatasetActionsComponent } from './components/dataset-actions/dataset-actions.component';
import { SqlExecutorComponent } from './components/sql-executor/sql-executor.component';
import { SqlScriptComponent } from './components/sql-executor/query/sql-script.component';
import { SqlpreviewGridPageComponent } from './components/sql-executor/preview-grid/sqlpreview-grid-page.component';
import { DetailsDialogComponent } from './components/sql-executor/dataset-details-dialog/details-dialog.component';
import { DatasetDetailViewComponent } from './components/dataset-detailedView/dataset-detail-view.component';

export const WorkbenchModule = 'WorkbenchModule';

const components = [
  WorkbenchPageComponent,
  DatasetsComponent,
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
  DatasetDetailViewComponent
];

@NgModule({
  imports: [
    AngularCommonModule,
    FormsModule,
    MaterialModule,
    ReactiveFormsModule,
    UIRouterModule.forChild({states: routes}),
    DxDataGridModule,
    DxTemplateModule,
    FlexLayoutModule,
    TreeModule,
    AceEditorModule,
    AngularSplitModule
  ],
  declarations: components,
  entryComponents: components,
  providers: [
    WorkbenchService,
    AnalyzeService,
    MenuService,
    ComponentHandler,
    HeaderProgressService,
    ToastService,
    SideNavService,
    LocalSearchService,
    dxDataGridService
  ]
})
export class WorkbenchUpgradeModule { }
