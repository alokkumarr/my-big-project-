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
      model: {
        operator: DSKFilterOperator.ISIN,
        values: []
      }
    });
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
}
