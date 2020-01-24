import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import * as cloneDeep from 'lodash/cloneDeep';
import * as get from 'lodash/get';
import * as flatMap from 'lodash/flatMap';
import * as filter from 'lodash/fp/filter';
import * as fpPipe from 'lodash/fp/pipe';
import { Location } from '@angular/common';

import { AnalyzeDialogService } from './analyze-dialog.service';
import { AnalysisDSL } from '../types';

export const BOOLEAN_CRITERIA = [
  {
    label: 'ALL',
    value: 'AND'
  },
  {
    label: 'ANY',
    value: 'OR'
  }
];

export const NUMBER_TYPES = ['int', 'integer', 'double', 'long', 'float'];

export const DEFAULT_BOOLEAN_CRITERIA = BOOLEAN_CRITERIA[0];

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

  openRuntimeModal(analysis: AnalysisDSL, filters = [], navigateBack: string) {
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
          const nonRuntimeFilters = filter(
            f => !(f.isRuntimeFilter || f.isGlobalFilter),
            analysis.sipQuery.filters
          );
          const allFilters = fpPipe(
            // block optional runtime filters that have no model
            filter(
              ({ isRuntimeFilter, isOptional, model }) =>
                !(isRuntimeFilter && isOptional && !model)
            ),
            runtimeFilters => [...runtimeFilters, ...nonRuntimeFilters]
          )(result.filters);
          analysis.sipQuery.filters = allFilters;
          // analysis.sqlBuilder.filters = result.filters.concat(
          //   filter(f => !(f.isRuntimeFilter || f.isGlobalFilter), analysis.sqlBuilder.filters)
          // );

          resolve(analysis);
          if (navigateBack === 'home') {
            this.router.navigate(['analyze', analysis.category]);
          } else if (navigateBack === 'back') {
            this.locationService.back();
          }
        });
    });
  }

  getRuntimeFilterValues(analysis, navigateBack: string = null) {
    const clone = cloneDeep(analysis);
    const runtimeFilters = this.getRuntimeFiltersFrom(
      get(clone, 'sipQuery.filters', get(clone, 'sqlBuilder.filters', []))
    );

    if (!runtimeFilters.length) {
      return Promise.resolve(clone);
    }
    return this.openRuntimeModal(clone, runtimeFilters, navigateBack);
  }
}
