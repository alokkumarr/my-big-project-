import { NgModule, ModuleWithProviders } from '@angular/core';
import { FilterService } from './services/filter.service';
import { ChartService } from './services/chart.service';
import { AnalyzeService } from './services/analyze.service';
import { DesignerService } from './designer';
import { AnalyzeDialogService } from './services/analyze-dialog.service';
import { AnalyzeActionsService } from './actions/analyze-actions.service';

const SERVICES = [
  AnalyzeDialogService,
  AnalyzeService,
  DesignerService,
  FilterService,
  ChartService,
  AnalyzeActionsService
];

@NgModule({})
export class AnalyzeModuleGlobal {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule: AnalyzeModuleGlobal,
      providers: [...SERVICES]
    };
  }
}
