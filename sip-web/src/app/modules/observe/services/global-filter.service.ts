import { Injectable } from '@angular/core';
import * as forEach from 'lodash/forEach';
import * as filter from 'lodash/filter';
import * as find from 'lodash/find';
import * as isEmpty from 'lodash/isEmpty';
import * as cloneDeep from 'lodash/cloneDeep';
import * as isEqual from 'lodash/isEqual';
import * as get from 'lodash/get';
import * as findIndex from 'lodash/findIndex';
import * as groupBy from 'lodash/groupBy';
import * as has from 'lodash/has';

import { Subject, BehaviorSubject } from 'rxjs';

import {
  CUSTOM_DATE_PRESET_VALUE,
  NUMBER_TYPES,
  DATE_TYPES
} from '../../analyze/consts';

interface KPIFilter {
  preset: string;
  gte?: string;
  lte?: string;
}

export const isValidDateFilter: (Filter) => boolean = filt => {
  return (
    filt.model.preset !== CUSTOM_DATE_PRESET_VALUE ||
    (filt.model.lte && filt.model.gte)
  );
};

export const isValidNumberFilter: (Filter) => boolean = filt =>
  has(filt, 'model.value') &&
  has(filt, 'model.otherValue') &&
  has(filt, 'model.operator');

export const isValidStringFilter: (Filter) => boolean = filt =>
  has(filt, 'model.modelValues') && filt.model.modelValues.length > 0;

@Injectable()
export class GlobalFilterService {
  public rawFilters = [];
  public updatedFilters = [];
  public onFilterChange = new Subject();
  public onSidenavStateChange = new Subject();
  public onApplyKPIFilter = new BehaviorSubject(null);
  public onApplyFilter = new Subject();
  public onClearAllFilters = new Subject();
  private _lastKPIFilter: KPIFilter = null;
  private _lastAnalysisFilters = {};

  constructor() {}

  initialise() {
    this.rawFilters = [];
    this.updatedFilters = [];
    this.onFilterChange.next(null);
  }

  get rawGlobalFilters() {
    return this.rawFilters;
  }

  get globalFilters() {
    return groupBy(this.updatedFilters, 'semanticId');
  }

  hasKPIFilterChanged(kpiFilter: KPIFilter) {
    const lastPreset = get(this._lastKPIFilter, 'preset');
    const currentPreset = get(kpiFilter, 'preset');
    if (isEmpty(lastPreset) && isEmpty(currentPreset)) {
      return false;
    }

    if (lastPreset !== currentPreset) {
      return true;
    }

    if (lastPreset === CUSTOM_DATE_PRESET_VALUE) {
      return (
        this._lastKPIFilter.gte !== kpiFilter.gte ||
        this._lastKPIFilter.lte !== kpiFilter.lte
      );
    } else {
      return false;
    }
  }

  resetLastKPIFilterApplied() {
    this._lastKPIFilter = null;
  }

  get lastKPIFilter() {
    return this._lastKPIFilter;
  }

  set lastKPIFilter(filt: KPIFilter) {
    this._lastKPIFilter = cloneDeep(filt);
  }

  haveAnalysisFiltersChanged(filts) {
    return !isEqual(this._lastAnalysisFilters, filts);
  }

  resetLastAnalysisFiltersApplied() {
    this._lastAnalysisFilters = {};
  }

  get lastAnalysisFilters() {
    return this._lastAnalysisFilters;
  }

  set lastAnalysisFilters(filts) {
    this._lastAnalysisFilters = cloneDeep(filts);
  }

  /**
   * Returns display name for a column from a list of columns
   *
   * @param {Array<any>} artifacts
   * @param {string} columnName
   * @param {string} tableName
   * @returns
   * @memberof GlobalFilterService
   */
  getDisplayNameFor(
    artifacts: Array<any>,
    columnName: string,
    tableName: string
  ) {
    const col = find(
      artifacts,
      column =>
        column.columnName === columnName &&
        (column.table || column.tableName || column.artifactsName) === tableName
    );
    return col ? col.displayName || col.columnName : columnName;
  }

  areFiltersEqual(f1, f2) {
    return (
      f1.semanticId === f2.semanticId &&
      (f1.tableName || f1.artifactsName) ===
        (f2.tableName || f2.artifactsName) &&
      f1.columnName === f2.columnName
    );
  }
  /**
   * Merges the existing raw filters with input array of filters.
   * Only adds filters which aren't already present.
   *
   * @param {Array<any>} filt
   * @memberof GlobalFilterService
   */
  addFilter(filt: Array<any>) {
    const validFilter = [];
    forEach(filt, f => {
      const exists = findIndex(this.rawFilters, rf =>
        this.areFiltersEqual(rf, f)
      );

      if (!(exists >= 0)) {
        this.rawFilters.push(f);
        if (this.isValid(f)) {
          this.updatedFilters.push(f);
        }
        validFilter.push(f);
      }
    });
    if (validFilter.length) {
      this.onFilterChange.next(validFilter);
    }
  }

  isValid(filt): boolean {
    if (NUMBER_TYPES.includes(filt.type)) {
      return isValidNumberFilter(filt);
    } else if (DATE_TYPES.includes(filt)) {
      return isValidDateFilter(filt);
    } else if (filt.type === 'string') {
      return isValidStringFilter(filt);
    }
    return false;
  }

  removeInvalidFilters(filt: Array<any>) {
    this.rawFilters = filter(this.rawFilters, rf =>
      find(filt, f => this.areFiltersEqual(f, rf))
    );
    this.updatedFilters = filter(this.updatedFilters, uf =>
      find(filt, f => this.areFiltersEqual(f, uf))
    );

    this.onFilterChange.next(null);
  }

  /**
   * Store updated filter values in a new array
   *
   * @param {any} {data, valid}
   * @memberof GlobalFilterService
   */
  updateFilter({ data, valid }) {
    const id = findIndex(
      this.updatedFilters,
      f =>
        f.semanticId === data.semanticId &&
        f.columnName === data.columnName &&
        (f.tableName || f.artifactsName) ===
          (data.tableName || data.artifactsName)
    );

    /* Push or replace existing filter */
    if (id >= 0 && !valid) {
      this.updatedFilters.splice(id, 1);
    } else if (id >= 0) {
      this.updatedFilters.splice(id, 1, data);
    } else {
      valid && this.updatedFilters.push(data);
    }
  }
}
