import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import * as cloneDeep from 'lodash/cloneDeep';
import * as filter from 'lodash/filter';
import * as groupBy from 'lodash/groupBy';
import * as isEmpty from 'lodash/isEmpty';
import * as forEach from 'lodash/forEach';
import * as isFinite from 'lodash/isFinite';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpToPairs from 'lodash/fp/toPairs';
import * as fpFlatMap from 'lodash/fp/flatMap';

import {
  CUSTOM_DATE_PRESET_VALUE,
  DATE_TYPES,
  NUMBER_TYPES
} from '../../../consts';
import { Artifact, FilterModel, Filter } from '../../types';
import { ArtifactDSL } from '../../../../../models';

export interface DesignerFilterDialogData {
  filters: Filter[];
  analysisType: string;
  booleanCriteria?: string;
  artifacts;
  isInRuntimeMode: boolean;
  supportsGlobalFilters?: boolean;
  supportsAggregationFilters?: boolean;
  showFilterOptions: boolean;
}
export interface DesignerFilterDialogResult {
  filters: Filter[];
  booleanCriteria?: string;
}

@Component({
  selector: 'designer-filter-dialog',
  templateUrl: './designer-filter-dialog.component.html',
  styleUrls: ['./designer-filter-dialog.component.scss']
})
export class DesignerFilterDialogComponent implements OnInit {
  artifacts: Artifact[] | ArtifactDSL[];
  filters: Filter[];
  groupedFilters;
  areFiltersValid = false;

  constructor(
    public dialogRef: MatDialogRef<DesignerFilterDialogData>,
    @Inject(MAT_DIALOG_DATA) public data: DesignerFilterDialogData
  ) {}

  ngOnInit() {
    this.filters = cloneDeep(this.data.filters);
    forEach(this.filters, filtr => {
      if (filtr.artifactsName) {
        filtr.tableName = filtr.artifactsName;
      }
    });
    this.groupedFilters = groupBy(this.filters, 'tableName');
    forEach(this.artifacts, artifact => {
      const name =
        (<Artifact>artifact).artifactName ||
        (<ArtifactDSL>artifact).artifactsName;
      if (!this.groupedFilters[name]) {
        this.addFilter(name, true);
      }
    });
    this.onFiltersChange();
  }

  aggregatedFiltersFor(artifactName: string): Filter[] {
    const allFilters = this.groupedFilters[artifactName];
    return allFilters
      ? allFilters.filter((f: Filter) => f.isAggregationFilter)
      : [];
  }

  nonAggregatedFiltersFor(artifactName: string): Filter[] {
    const allFilters = this.groupedFilters[artifactName];
    return allFilters
      ? allFilters.filter((f: Filter) => !f.isAggregationFilter)
      : [];
  }

  filterRowTrackBy(index, filterRow) {
    return `${index}:${filterRow.columnName}`;
  }

  onFilterModelChange() {
    this.onFilterChange();
  }
  onFilterChange() {
    this.onFiltersChange();
  }

  addFilter(tableName, initialAdd = false, isAggregationFilter = false) {
    const newFilter: Filter = {
      type: null,
      tableName,
      isOptional: false,
      columnName: null,
      isRuntimeFilter: false,
      isAggregationFilter,
      isGlobalFilter: false,
      model: null
    };
    if (!this.groupedFilters[tableName]) {
      this.groupedFilters[tableName] = [];
    }
    this.groupedFilters[tableName] = [
      ...this.groupedFilters[tableName],
      newFilter
    ];
    if (!initialAdd) {
      this.onFiltersChange();
    }
  }

  removeFilter(targetIndex, tableName, isAggregationFilter) {
    let aggregatedFilters = this.aggregatedFiltersFor(tableName),
      nonAggregatedFilters = this.nonAggregatedFiltersFor(tableName);

    if (isAggregationFilter) {
      aggregatedFilters = filter(
        aggregatedFilters,
        (_, index) => targetIndex !== index
      );
    } else {
      nonAggregatedFilters = filter(
        nonAggregatedFilters,
        (_, index) => targetIndex !== index
      );
    }
    this.groupedFilters[tableName] = [
      ...nonAggregatedFilters,
      ...aggregatedFilters
    ];
    this.onFiltersChange();
  }

  onFiltersChange() {
    this.filters = fpPipe(
      fpToPairs,
      fpFlatMap(([_, filters]) => filters)
    )(this.groupedFilters);
    this.areFiltersValid = this.validateFilters(this.filters);
  }

  artifactTrackByFn(_, artifact: Artifact | ArtifactDSL) {
    return (
      (<Artifact>artifact).artifactName || (<ArtifactDSL>artifact).artifactsName
    );
  }

  onBooleanCriteriaChange(booleanCriteria) {
    this.data.booleanCriteria = booleanCriteria;
  }

  ok() {
    const result: DesignerFilterDialogResult = {
      filters: filter(this.filters, 'columnName'),
      booleanCriteria: this.data.booleanCriteria
    };
    this.dialogRef.close(result);
  }

  validateFilters(filters) {
    let areValid = true;
    forEach(
      filters,
      ({
        type,
        model,
        isAggregationFilter,
        isRuntimeFilter,
        isGlobalFilter,
        isOptional
      }: Filter) => {
        if (!isRuntimeFilter && isGlobalFilter) {
          areValid = true;
        } else if (!model) {
          areValid = Boolean(
            this.data.isInRuntimeMode
              ? isOptional && isRuntimeFilter
              : isRuntimeFilter
          );
        } else if (NUMBER_TYPES.includes(type) || isAggregationFilter) {
          areValid = this.isNumberFilterValid(model);
        } else if (type === 'string') {
          areValid = this.isStringFilterValid(model);
        } else if (DATE_TYPES.includes(type)) {
          areValid = this.isDateFilterValid(model);
        }
        if (!areValid) {
          return false;
        }
      }
    );
    return areValid;
  }

  isStringFilterValid({ operator, modelValues }: FilterModel) {
    return Boolean(operator && !isEmpty(modelValues));
  }

  isNumberFilterValid({ operator, value, otherValue }: FilterModel) {
    switch (operator) {
      case 'BTW':
        return Boolean(isFinite(value) && isFinite(otherValue));
      default:
        return Boolean(isFinite(value));
    }
  }

  isDateFilterValid({ preset, lte, gte }: FilterModel) {
    switch (preset) {
      case CUSTOM_DATE_PRESET_VALUE:
        return Boolean(lte && gte);
      default:
        return Boolean(preset);
    }
  }

  cancel() {
    this.dialogRef.close();
  }
}
