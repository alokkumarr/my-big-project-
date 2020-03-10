import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import {
  DSKFilterGroup,
  DSKFilterField,
  DSKFilterOperator,
  DSKFilterBooleanCriteria
} from '../dsk-filter.model';

import { PopperContent } from 'ngx-popper';
import { MatChipInputEvent } from '@angular/material';
import { JwtService } from 'src/app/common/services';
import { DskFiltersService, DskEligibleField } from './../services/dsk-filters.service';

import * as toString from 'lodash/toString';
import * as cloneDeep from 'lodash/cloneDeep';
import * as forEach from 'lodash/forEach';
import * as groupBy from 'lodash/groupBy';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpToPairs from 'lodash/fp/toPairs';
import * as fpFlatMap from 'lodash/fp/flatMap';

export const defaultFilters: DSKFilterGroup = {
  booleanCriteria: DSKFilterBooleanCriteria.AND,
  booleanQuery: []
};

@Component({
  selector: 'dsk-filter-group',
  templateUrl: './dsk-filter-group.component.html',
  styleUrls: ['./dsk-filter-group.component.scss']
})
export class DskFilterGroupComponent implements OnInit {
  filterGroup: DSKFilterGroup = cloneDeep(defaultFilters);
  readonly separatorKeysCodes: number[] = [ENTER, COMMA];
  dskEligibleFields: Array<DskEligibleField> = [];
  filteredColumns: [];
  groupedFilters;
  filters;
  @Input() data;
  @Input('filterGroup') set _filterGroup(filters: DSKFilterGroup) {
    this.filterGroup = filters || cloneDeep(defaultFilters);
    this.onChange.emit(this.filterGroup);
  }
  @Input() selfIndex: number; // stores the position inside parent (for removal)
  @Output() onRemoveGroup = new EventEmitter();
  @Output() onChange = new EventEmitter();

  constructor(
    jwtService: JwtService,
    dskFilterService: DskFiltersService
  ) {
    dskFilterService
      .getEligibleDSKFieldsFor(jwtService.customerId, jwtService.productId)
      .subscribe(fields => {
        this.dskEligibleFields = fields;
      });
  }

  ngOnInit() {
    console.log(this.data);
    this.filters = cloneDeep(this.data.filters);
    this.groupedFilters = groupBy(this.data.filters, 'tableName');
    this.onChange.emit(this.filterGroup);
  }

  filterAutocompleteFields(value) {
    const filterValue = toString(value).toLowerCase();
    return this.dskEligibleFields.filter(option =>
      (option.displayName || option.columnName)
        .toLowerCase()
        .includes(filterValue)
    );
  }

  toggleCriteria() {
    if (this.filterGroup.booleanCriteria === DSKFilterBooleanCriteria.AND) {
      this.filterGroup.booleanCriteria = DSKFilterBooleanCriteria.OR;
    } else {
      this.filterGroup.booleanCriteria = DSKFilterBooleanCriteria.AND;
    }
    this.onChange.emit(this.filterGroup);
  }

  addField(popper: PopperContent) {
    popper.hide();
    this.filterGroup.booleanQuery.push({
      columnName: '',
      artifactsName: '',
      model: {
        operator: DSKFilterOperator.ISIN,
        values: []
      }
    });
    console.log(this.filterGroup);
    this.onChange.emit(this.filterGroup);
  }

  /**
   * Removes field from this.filterGroup's booleanQuery.
   *
   * @param {number} fieldIndex
   * @memberof DskFilterGroupComponent
   */
  removeField(fieldIndex: number) {
    this.filterGroup.booleanQuery.splice(fieldIndex, 1);
    this.onChange.emit(this.filterGroup);
  }

  addGroup(popper: PopperContent) {
    popper.hide();
    this.filterGroup.booleanQuery.push({
      booleanCriteria: DSKFilterBooleanCriteria.AND,
      booleanQuery: []
    });
    console.log(this.filterGroup);
    this.onChange.emit(this.filterGroup);
  }

  /**
   * Removes group from this.filterGroup's booleanQuery.
   * This is a handler for an output event fired from the child group
   * component, since the child component owns the remove button.
   *
   * @param {*} childId
   * @memberof DskFilterGroupComponent
   */
  removeGroup(childId) {
    this.filterGroup.booleanQuery.splice(childId, 1);
    this.onChange.emit(this.filterGroup);
  }

  preventSpace(event: KeyboardEvent) {
    if (event.code === 'Space') {
      return false;
    }
    return true;
  }

  updateAttributeName(childId: number, value: string) {
    (<DSKFilterField>this.filterGroup.booleanQuery[childId]).columnName = value;
    this.onChange.emit(this.filterGroup);
  }

  addValue(childId: number, event: MatChipInputEvent) {
    const input = event.input;
    const value = event.value;
    if ((value || '').trim()) {
      (<DSKFilterField>(
        this.filterGroup.booleanQuery[childId]
      )).model.values.push(value);
      this.onChange.emit(this.filterGroup);
    }

    if (input) {
      input.value = '';
    }
  }

  removeValue(childId: number, valueId: number) {
    (<DSKFilterField>(
      this.filterGroup.booleanQuery[childId]
    )).model.values.splice(valueId, 1);
    this.onChange.emit(this.filterGroup);
  }

  artifactSelect(artifact, childId) {
    console.log(artifact);
    this.addFilter(artifact, true);
    (<DSKFilterField>this.filterGroup.booleanQuery[childId]).artifactsName = artifact;
    console.log(this.filterGroup);
    this.onChange.emit(this.filterGroup);
  }

  fetchColumns(childId) {
    let dropDownData = this.data.artifacts.find((data: any) => data.artifactName === this.filterGroup.booleanQuery[childId].artifactsName);
    return dropDownData.columns;
  }

  artifactTrackByFn(_, artifact: Artifact | ArtifactDSL) {
    return (
      (<Artifact>artifact).artifactName || (<ArtifactDSL>artifact).artifactsName
    );
  }

  nonAggregatedFiltersFor(childId): Filter[] {
    const allFilters = this.groupedFilters[(<DSKFilterField>this.filterGroup.booleanQuery[childId]).artifactsName];
    return allFilters
      ? allFilters.filter((f: Filter) => !f.isAggregationFilter)
      : [];
  }

  filterRowTrackBy(index, filterRow) {
    return `${index}:${filterRow.columnName}`;
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

  onFiltersChange() {
    this.filters = fpPipe(
      fpToPairs,
      fpFlatMap(([_, filters]) => filters)
    )(this.groupedFilters);
  }
}
