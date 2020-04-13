import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { DskFiltersService } from './../services/dsk-filters.service';

import * as get from 'lodash/get';
import * as debounce from 'lodash/debounce';
import * as cloneDeep from 'lodash/cloneDeep';
import * as isEmpty from 'lodash/isEmpty';
import * as filter from 'lodash/filter';
import * as concat from 'lodash/concat';

import { v4 as uuid } from 'uuid';

import { DSKFilterGroup } from '../dsk-filter.model';
import { defaultFilters } from '../dsk-filter-group/dsk-filter-group.component';

@Component({
  selector: 'add-attribute-dialog',
  templateUrl: './dsk-filter-dialog.component.html',
  styleUrls: ['./dsk-filter-dialog.component.scss']
})
export class DskFilterDialogComponent implements OnInit {
  public attribute = {};
  dskFilterObject: DSKFilterGroup;
  errorState = true;
  operation: 'Update' | 'Add' = 'Add';
  previewString = '';
  errorMessage;
  filterQuery;
  aggregatedFilters = [];
  showGlobalOption: boolean;
  debouncedValidator = debounce(this.validateFilterGroup.bind(this), 200);
  constructor(
    private _dialogRef: MatDialogRef<DskFilterDialogComponent>,
    private datasecurityService: DskFiltersService,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      groupSelected;
      filterGroup: DSKFilterGroup;
      mode;
      filters;
      booleanCriteria;
      artifacts;
      supportsAggregationFilters: boolean;
      isInRuntimeMode;
    }
  ) {
    this.showGlobalOption = true;
    this.datasecurityService.clearDSKEligibleFields();
    this.operation = this.data.filterGroup ? 'Update' : 'Add';
    this.dskFilterObject = this.fetch(this.data, this.data.mode);
  }

  ngOnInit() {

  }

  fetch(data, mode) {
    switch (mode) {
      case 'ANALYZE':
        if (isEmpty(data.filters)) {
          return cloneDeep(defaultFilters);
        } else {
          this.aggregatedFilters = [];
          this.aggregatedFilters = this.data.filters.filter(option => {
            return option.isAggregationFilter === true;
          });
          if (this.data.filters[0].filters) {
            return this.changeIndexToNames(this.data.filters, 'fiters', 'booleanQuery');
          } else {
            const oldFormatFilters = this.data.filters.filter(option => {
              return option.isAggregationFilter !== true;
            });
            this.data.filters = [];
            this.data.filters.push({
              booleanCriteria: this.data.booleanCriteria,
              booleanQuery: oldFormatFilters
            })
            return this.data.filters[0];
          }
        }
      case 'DSK':
        return data.filterGroup || cloneDeep(defaultFilters);
    }
  }

  validateFilterGroup() {
    const analyzeResult = this.datasecurityService.changeIndexToNames(this.dskFilterObject, 'booleanQuery', 'filters' );
    this.errorState = !this.datasecurityService.isDSKFilterValid(
      this.data.mode === 'DSK' ? this.dskFilterObject : concat([analyzeResult], this.aggregatedFilters),
      true,
      this.data
    );

    if (this.errorState) {
      this.previewString = '';
    } else {
      this.previewString = this.datasecurityService.generatePreview(
        this.dskFilterObject, this.data.mode
      );
    }
  }

  fetchAggregatedFilters(filters) {
    return filters.map(field => {
      if (field.model.operator === 'BTW') {
        return `<span ${field.isRuntimeFilter ? 'class="prompt-filter"' : ''}>${field.columnName.split('.keyword')[0]}</span> <span class="operator">${
          field.model.operator
        }</span> <span [attr.e2e]="'ffilter-model-value'">[${get(field, 'model.otherValue')} and ${get(field, 'model.value')}]</span>`;
      } else {
        return `<span ${field.isRuntimeFilter ? 'class="prompt-filter"' : ''}>${field.columnName.split('.keyword')[0]}</span> <span class="operator">${
          field.model.operator || ''
        }</span> <span [attr.e2e]="'ffilter-model-value'">[${[get(field, 'model.value')]}]</span>`;
      }
    })
  }

  hasWhiteSpace(field) {
    return /\s/g.test(field);
  }

  updateFilter(filter: DSKFilterGroup) {
    this.dskFilterObject = filter;
    this.debouncedValidator();
  }

  //Need to para'se the replace funtion;

  changeIndexToNames(dskObject, source, target) {
    const convertToString = JSON.stringify(dskObject);
    const replaceIndex = convertToString.replace(/"filters":/g, '"booleanQuery":');
    const convertToJson = JSON.parse(replaceIndex);
    return convertToJson[0];
  }

  addaggregateFilter() {
    this.aggregatedFilters.push({
      columnName: '',
      type:'',
      artifactsName: this.data.artifacts[0].artifactName,
      aggregate: null,
      isAggregationFilter: true,
      uuid: uuid(),
      isRuntimeFilter: false,
      isOptional: false,
      model: {}
    });
    this.validateFilterGroup();
  }

  removeAggrFilter(targetIndex) {
    this.aggregatedFilters = filter(
      this.aggregatedFilters,
      (_, index) => targetIndex !== index
    );
    this.validateFilterGroup();
  }

  onFilterChange(e) {
    this.debouncedValidator();
  }

  filterRowTrackBy(index, filterRow) {
    return `${index}:${filterRow.columnName}`;
  }

  submit() {
    if (this.data.mode === 'ANALYZE') {
      this.filterQuery = cloneDeep(this.dskFilterObject);
      const result = this.datasecurityService.changeIndexToNames(this.filterQuery, 'booleanQuery', 'filters' );
      this._dialogRef.close(concat([result], this.aggregatedFilters));
    } else {
        this.datasecurityService
        .updateDskFiltersForGroup(
          this.data.groupSelected.secGroupSysId,
          this.dskFilterObject
        )
        .then(response => {
          if (get(response, 'valid')) {
            this.errorState = false;
            this._dialogRef.close(get(response, 'valid'));
          }
        })
        .catch(err => {
          if (!get(err.error, 'valid')) {
            this.errorState = !get(err.error, 'valid');
            this.errorMessage = get(err.error, 'validityMessage');
          }
        });
    }
  }
}
