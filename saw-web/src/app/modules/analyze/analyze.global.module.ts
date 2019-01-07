import { NgModule, ModuleWithProviders } from '@angular/core';
import { FilterService } from './services/filter.service';
import { ChartService } from '../../common/services/chart.service';
import { MapDataService } from '../../common/components/charts/map-data.service';

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
  MapDataService,
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
