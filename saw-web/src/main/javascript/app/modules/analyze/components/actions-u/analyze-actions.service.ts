import { Inject, Injectable } from '@angular/core';
import * as defaultsDeep from 'lodash/defaultsDeep';
import * as clone from 'lodash/clone';
import * as deepClone from 'lodash/cloneDeep';

import {AnalyseTypes, Events} from '../../consts';
import { AnalyzeService } from '../../services/analyze.service';
import { FilterService } from '../../services/filter.service';
import { AnalyzeDialogService } from '../../services/analyze-dialog.service';

@Injectable()
export class AnalyzeActionsService {

  constructor(
    private _filterService: FilterService,
    private _analyzeService: AnalyzeService,
    private _analyzeDialogService: AnalyzeDialogService,
    @Inject('$mdDialog') private _$mdDialog: any
  ) {}

  execute(analysis) {
    return this._filterService.getRuntimeFilterValues(analysis).then(model => {
      this._analyzeService.executeAnalysis(model);
      return model;
    });
  }

  fork(analysis) {
    const model = clone(analysis);
    model.name += ' Copy';
    return this.openEditModal(model, 'fork');
  }

  edit(analysis) {
    return this.openEditModal(clone(analysis), 'edit').then(status => {
      if (!status) {
        return status;
      }

      // TODO refresh the analysis page with delete

      return status;
    });
  }

  publish() {

  }

  export() {

  }

  openEditModal(analysis, mode: 'edit' | 'fork') {
    const openModal = template => this.showDialog({
      template,
      controller: scope => {
        scope.model = deepClone(analysis);
      },
      multiple: true
    });

    switch (analysis.type) {
    case AnalyseTypes.ESReport:
    case AnalyseTypes.Report:
      return openModal(`<analyze-report model="model" mode="${mode}"></analyze-report>`);
    case AnalyseTypes.Chart:
      return openModal(`<analyze-chart model="model" mode="${mode}"></analyze-chart>`);
    case AnalyseTypes.Pivot:
      return this._analyzeDialogService.openEditAdnalysisDialog(analysis, mode)
        .afterClosed().first().toPromise();
    default:
    }
  }

  showDialog(config) {
    config = defaultsDeep(config, {
      controllerAs: '$ctrl',
      multiple: false,
      autoWrap: false,
      focusOnOpen: false,
      clickOutsideToClose: true,
      fullscreen: false
    });

    return this._$mdDialog.show(config);
  }
}
