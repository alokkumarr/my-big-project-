import * as angular from 'angular';
import { downgradeInjectable } from '@angular/upgrade/static';
import { downgradeComponent } from '@angular/upgrade/static';

import { NgModule } from '@angular/core';
import { CommonModule as AngularCommonModule } from '@angular/common';
import { MaterialModule } from '../../material.module';
import { FlexLayoutModule } from '@angular/flex-layout'
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DxDataGridModule, DxTemplateModule } from 'devextreme-angular';
import { DxTreeViewModule } from 'devextreme-angular';
import { TreeModule } from 'angular-tree-component';

import { routesConfig } from './routes';
import { i18nConfig } from './i18n';

import { AnalyzeService } from '../analyze/services/analyze.service';
import { WorkbenchService } from './services/workbench.service';
import {
  menuServiceProvider,
  componentHandlerProvider,
  headerProgressProvider,
  toastProvider,
  sidenavProvider,
  localSearchProvider,
  dxDataGridProvider
} from '../../common/services/ajs-common-providers';

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

import { CommonModule } from '../../common';

export const WorkbenchModule = 'WorkbenchModule';

angular.module(WorkbenchModule, [
  CommonModule
])
  .config(routesConfig)
  .config(i18nConfig)
  .factory('WorkbenchService', downgradeInjectable(WorkbenchService) as Function)
  .service('AnalyzeService', AnalyzeService)
  .directive('datasetsPage',
  downgradeComponent({ component: DatasetsComponent }) as angular.IDirectiveFactory)
  .component('workbenchPage', WorkbenchPageComponent);

const components = [
  DatasetsComponent,
  DatasetsCardPageComponent,
  DatasetsGridPageComponent,
  CreateDatasetsComponent,
  SelectRawdataComponent,
  DatasetDetailsComponent,
  RawpreviewDialogComponent,
  ParserPreviewComponent,
  DateformatDialogComponent
];

@NgModule({
  imports: [AngularCommonModule, FormsModule, MaterialModule, ReactiveFormsModule, DxDataGridModule, DxTemplateModule, FlexLayoutModule, DxTreeViewModule, TreeModule],
  declarations: components,
  entryComponents: components,
  providers: [
    WorkbenchService,
    menuServiceProvider,
    componentHandlerProvider,
    headerProgressProvider,
    toastProvider,
    sidenavProvider,
    localSearchProvider,
    dxDataGridProvider
  ]
})
export class WorkbenchUpgradeModule { }  
