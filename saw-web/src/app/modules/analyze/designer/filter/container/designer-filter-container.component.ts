import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import * as filter from 'lodash/filter';
import * as groupBy from 'lodash/groupBy';
import * as forEach from 'lodash/forEach';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpToPairs from 'lodash/fp/toPairs';
import * as fpFlatMap from 'lodash/fp/flatMap';

import { Filter, Artifact } from '../../types';
import { isValid as isNumberFilterValid } from '../number/designer-number-filter.component';
import { isValid as isStringFilterValid } from '../string/designer-string-filter.component';
import { isValid as isDateFilterValid } from '../date/designer-date-filter.component';

@Component({
  selector: 'designer-filter-container',
  templateUrl: './designer-filter-container.component.html',
  styleUrls: ['./designer-filter-container.component.scss']
})
export class DesignerFilterContainerComponent implements OnInit {
  @Output()
  public filtersChange: EventEmitter<{
    filters: Filter[];
    valid: boolean;
  }> = new EventEmitter();
  @Input() public artifacts: Artifact[];
  @Input() public filters: Filter[];
  @Input() public supportsGlobalFilters: boolean;

  public groupedFilters;

  ngOnInit() {
    this.groupedFilters = groupBy(this.filters, 'tableName');
    forEach(this.artifacts, artifact => {
      const name = artifact.artifactName;
      if (!this.groupedFilters[name]) {
        this.addFilter(name, true);
      }
    });
  }

  onFilterChange() {
    this.onFiltersChange();
  }

  addFilter(tableName, initialAdd = false) {
    const newFilter: Filter = {
      type: null,
      tableName,
      columnName: null,
      isRuntimeFilter: false,
      isOptional: true,
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

  getValidity(filters: Array<Filter>): boolean {
    let validity = true;
    forEach(filters, f => {
      if (f.isGlobalFilter || f.isRuntimeFilter) {
        return;
      }

      /* prettier-ignore */
      switch (f.type) {
      case 'string':
        validity = validity && isStringFilterValid(f.model);
        break;
      case 'int':
      case 'double':
      case 'float':
      case 'long':
      case 'integer':
        validity = validity && isNumberFilterValid(f.model);
        break;
      case 'date':
        validity = validity && isDateFilterValid(f.model);
      }
    });
    return validity;
  }

  removeFilter(targetIndex, tableName) {
    this.groupedFilters[tableName] = filter(
      this.groupedFilters[tableName],
      (_, index) => targetIndex !== index
    );
    this.onFiltersChange();
  }

  onFiltersChange() {
    this.filters = fpPipe(fpToPairs, fpFlatMap(([_, filters]) => filters))(
      this.groupedFilters
    );

    this.filtersChange.emit({
      filters: this.filters,
      valid: this.getValidity(this.filters)
    });
  }

  artifactTrackByFn(_, artifact: Artifact) {
    return artifact.artifactName;
  }
}
