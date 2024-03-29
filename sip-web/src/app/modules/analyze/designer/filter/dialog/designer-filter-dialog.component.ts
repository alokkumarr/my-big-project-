import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import * as cloneDeep from 'lodash/cloneDeep';
import * as filter from 'lodash/filter';
import * as groupBy from 'lodash/groupBy';
import * as isEmpty from 'lodash/isEmpty';
import * as forEach from 'lodash/forEach';
import * as get from 'lodash/get';
import * as isFinite from 'lodash/isFinite';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpToPairs from 'lodash/fp/toPairs';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as isUndefined from 'lodash/isUndefined';
import { ENTER } from '@angular/cdk/keycodes';

import {
  CUSTOM_DATE_PRESET_VALUE,
  DATE_TYPES,
  NUMBER_TYPES,
  SQL_QUERY_KEYWORDS,
  QUERY_RUNTIME_IDENTIFIER
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
  analysisReportType?: string;
  designerPage?: boolean;
  query?: string;
  mode?: string;
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
  queryFilters;
  groupedFilters;
  areFiltersValid = false;
  queryWithClass;
  readonly separatorKeysCodes: number[] = [ENTER];
  modelValueArray: string[][] = [];

  constructor(
    public dialogRef: MatDialogRef<DesignerFilterDialogData>,
    @Inject(MAT_DIALOG_DATA) public data: DesignerFilterDialogData
  ) {}

  ngOnInit() {
    this.queryWithClass = this.data.analysisType === 'report'
      && this.data.analysisReportType === 'query'
        ? this.loadQueryWithClasses()
        : '';

    if (this.data.analysisType === 'report' && this.data.analysisReportType === 'query') {
      this.queryFilters = get(this.data, 'filters[0].filters');
      forEach(this.queryFilters, filtr => {
        this.modelValueArray.push([]);
        if (filtr.artifactsName) {
          filtr.tableName = filtr.artifactsName;
        }

        if (this.data.analysisType === 'report' && this.data.analysisReportType === 'query') {
          filtr.model.modelValues = [];
        }
      });
    } else {
      this.filters = cloneDeep(this.data.filters);
      forEach(this.filters, filtr => {
        this.modelValueArray.push([]);
        if (filtr.artifactsName) {
          filtr.tableName = filtr.artifactsName;
        }

        if (this.data.analysisType === 'report' && this.data.analysisReportType === 'query') {
          filtr.model.modelValues = [];
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

    }

    this.onFiltersChange();
  }

  // As we show the query in filter pop up, we need to add colors
  // to the keywords present in the sql query for better understanding
  // if the query is too long
  loadQueryWithClasses() {
    // reset filter values
    forEach(this.filters, filter => {
      filter.model.modelValues = [];
    });
    let addClass = '';
    this.data.query.replace(/[\s]+/g, " ").trim().split(" ").forEach(function(val) {
      if (SQL_QUERY_KEYWORDS.indexOf(val.trim().toUpperCase()) > -1) {
        addClass += "<span class='sql-keyword'>" + val + "&nbsp;</span>";
      }
      else if (val.trim().toUpperCase() === QUERY_RUNTIME_IDENTIFIER) {
        addClass += "<span class='runtime-indicator'>" + val + "&nbsp;</span>";
      } else {
        addClass += "<span class='other'>" + val + "&nbsp;</span>";
      }
    });
    return addClass;
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
    this.areFiltersValid = this.validateFilters(this.queryFilters);
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
      filters:
        this.data.analysisType === 'report' && this.data.analysisReportType === 'query'
          ? this.queryFilters
          :  filter(this.filters, 'columnName'),
      booleanCriteria: this.data.booleanCriteria
    };
    this.dialogRef.close(result);
  }

  validateFilters(filters) {
    let areValid = true;
    if (this.data.analysisType === 'report' && this.data.analysisReportType === 'query') {
      forEach(filters, filter => {
        areValid = isUndefined(filter.model) ? false : areValid;
        if (!isUndefined(filter.model)) {
          areValid = isEmpty(filter.model.modelValues) ? false: areValid;
        }
      })
    } else {
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
    }
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

  createFilterRequest(event, i, id) {
    switch (id) {
      case 'column':
        this.queryFilters[i].displayName = event.srcElement.value;
        break;
      case 'description':
        this.queryFilters[i].description = event.srcElement.value;
        break;
    }
    this.onFiltersChange();
  }


  addOpt(event, index: number): void {
    const input = event.input;
    const value = event.value;
    if ((value || '').trim()) {
      this.modelValueArray[index].push(value.trim());
    }
    if (input) {
      input.value = '';
    }
    this.queryFilters[index].model.modelValues = this.modelValueArray[index];
    this.onFiltersChange();
  }

  removeOpt(opt: string, index: number): void {
    const optIndex = this.modelValueArray[index].indexOf(opt);
    if (optIndex >= 0) {
      this.modelValueArray[index].splice(optIndex, 1);
    }
    this.queryFilters[index].model.modelValues = this.modelValueArray[index];
    this.onFiltersChange();
  }
}
