import { Injectable } from '@angular/core';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import * as cloneDeep from 'lodash/cloneDeep';
import * as filter from 'lodash/filter';
import * as flatMap from 'lodash/flatMap';
import * as get from 'lodash/get';
import * as fpMap from 'lodash/fp/map';
import * as fpFilter from 'lodash/fp/filter';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpOmit from 'lodash/fp/omit';
import * as fpConcat from 'lodash/fp/concat';

import { AnalyzeDialogService } from './analyze-dialog.service';
import { AnalysisDSL } from '../types';

@Injectable()
export class FilterService {
  constructor(
    public _dialog: AnalyzeDialogService,
    private router: Router,
    private locationService: Location
  ) {}

  getRuntimeFiltersFrom(filters = []) {
    return filter(f => f.isRuntimeFilter, filters);
  }

  supportsAggregatedFilters(analysis: AnalysisDSL): boolean {
    /* DL reports are not supported for aggregated filters yet */
    if (analysis.type === 'report') {
      return false;
    }
    return [/*'report', */ 'esReport'].includes(analysis.type)
      ? flatMap(
          analysis.sipQuery.artifacts,
          artifact => artifact.fields
        ).some(field => Boolean(field.aggregate))
      : true;
  }

  hasRuntimeAggregatedFilters(analysis: AnalysisDSL): boolean {
    return analysis.sipQuery.filters.some(
      f => f.isAggregationFilter && f.isRuntimeFilter
    );
  }

  openRuntimeModal(analysis: AnalysisDSL, filters = [], navigateTo: string) {
    return new Promise(resolve => {
      this._dialog
        .openFilterPromptDialog(
          filters,
          analysis,
          this.supportsAggregatedFilters(analysis) &&
            this.hasRuntimeAggregatedFilters(analysis)
        )
        .afterClosed()
        .subscribe(result => {
          if (!result) {
            return resolve();
          }
          const runtimeFiltersWithValues = result.filters;
          const allFiltersWithEmptyRuntimeFilters = analysis.sipQuery.filters;
          const allFiltersWithValues = this.mergeValuedRuntimeFiltersIntoFilters(
            runtimeFiltersWithValues,
            allFiltersWithEmptyRuntimeFilters
          );
          analysis.sipQuery.filters = allFiltersWithValues;
          resolve(analysis);
          this.navigateTo(navigateTo, analysis.category);
        });
    });
  }

  mergeValuedRuntimeFiltersIntoFilters(
    runtimeFilters,
    allFiltersWithEmptyRuntimeFilters
  ) {
    const nonRuntimeFilters = filter(
      allFiltersWithEmptyRuntimeFilters,
      f => !(f.isRuntimeFilter || f.isGlobalFilter)
    );
    return fpPipe(
      fpFilter(
        ({ isRuntimeFilter, isOptional, model }) =>
          !(isRuntimeFilter && isOptional && !model)
      ),
      fpConcat(nonRuntimeFilters)
    )(runtimeFilters);
  }

  private navigateTo(navigateTo, analysisCategory) {
    switch (navigateTo) {
      case 'home':
        this.router.navigate(['analyze', analysisCategory]);
        break;
      case 'back':
        this.locationService.back();
        break;
    }
  }

  getCleanedRuntimeFilterValues(analysis) {
    const filters = get(analysis, 'sipQuery.filters');
    // due to a bug, the backend sends runtimeFilters, with the values that were last used
    // so we have to delete model from runtime filters
    return fpPipe(
      fpFilter(f => f.isRuntimeFilter),
      fpMap(fpOmit('model'))
    )(filters);
  }

  public getRuntimeFilterValuesIfAvailable(
    analysis,
    navigateBack: string = null
  ) {
    const clone = cloneDeep(analysis);
    const cleanedRuntimeFilters = this.getCleanedRuntimeFilterValues(clone);

    if (!cleanedRuntimeFilters.length) {
      return Promise.resolve(clone);
    }
    return this.openRuntimeModal(clone, cleanedRuntimeFilters, navigateBack);
  }
}
