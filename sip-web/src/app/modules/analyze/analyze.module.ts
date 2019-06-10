import { CommonModule as CommonModuleAngular4 } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgxsModule } from '@ngxs/store';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { LocalStorageModule } from 'angular-2-local-storage';
import { AnalyzeViewModule } from './view';
import { ExecutedViewModule } from './executed-view';
import { AnalyzeActionsModule } from './actions';

import { AnalysesResolver } from './view/analyses.resolver';
import { AnalyzeDesignerModule } from './designer/index';

import { routes } from './routes';

import { DefaultAnalyzeCategoryGuard } from './guards';

import { CommonModuleTs } from '../../common';
import { UChartModule } from '../../common/components/charts';
import { AnalyzePublishDialogModule } from './publish';
import { AnalyzeModuleGlobal } from './analyze.global.module';
import { DesignerPageComponent } from './designer';

import { AnalyzeFilterModule } from './designer/filter';

import { AnalyzePageComponent } from './page';
import { AnalyzeState } from './state/analyze.state';

const COMPONENTS = [DesignerPageComponent, AnalyzePageComponent];

const GUARDS = [DefaultAnalyzeCategoryGuard];

@NgModule({
  imports: [
    NgxsModule.forFeature([AnalyzeState]),
    CommonModuleAngular4,
    LocalStorageModule.withConfig({
      prefix: 'symmetra',
      storageType: 'localStorage'
    }),
    AnalyzeDesignerModule,
    AnalyzeModuleGlobal.forRoot(),
    RouterModule.forChild(routes),
    CommonModuleTs,
    FormsModule,
    ReactiveFormsModule,
    UChartModule,
    AnalyzeViewModule,
    ExecutedViewModule,
    AnalyzeActionsModule,
    AnalyzeFilterModule,
    AnalyzePublishDialogModule
  ],
  declarations: [...COMPONENTS],
  entryComponents: COMPONENTS,
  providers: [AnalysesResolver, ...GUARDS],
  exports: [AnalyzePageComponent]
})
export class AnalyzeModule {}
