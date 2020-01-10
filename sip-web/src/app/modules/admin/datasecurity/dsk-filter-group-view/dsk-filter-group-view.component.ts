import { Component, OnInit, Input } from '@angular/core';
import { DSKFilterGroup, DSKFilterBooleanCriteria } from '../dsk-filter.model';

import { JwtService } from 'src/app/common/services';
import { DataSecurityService, DskEligibleField } from '../datasecurity.service';

import * as toString from 'lodash/toString';
import * as cloneDeep from 'lodash/cloneDeep';

const defaultFilters: DSKFilterGroup = {
  booleanCriteria: DSKFilterBooleanCriteria.AND,
  booleanQuery: []
};
@Component({
  selector: 'dsk-filter-group-view',
  templateUrl: './dsk-filter-group-view.component.html',
  styleUrls: ['./dsk-filter-group-view.component.scss']
})
export class DskFilterGroupViewComponent implements OnInit {
  filterGroup: DSKFilterGroup = cloneDeep(defaultFilters);
  dskEligibleFields: Array<DskEligibleField> = [];

  @Input('filterGroup') set _filterGroup(filters: DSKFilterGroup) {
    this.filterGroup = filters || cloneDeep(defaultFilters);
  }

  constructor(
    jwtService: JwtService,
    datasecurityService: DataSecurityService
  ) {
    datasecurityService
      .getEligibleDSKFieldsFor(jwtService.customerId, jwtService.productId)
      .subscribe(fields => {
        this.dskEligibleFields = fields;
      });
  }

  ngOnInit() {}

  filterAutocompleteFields(value) {
    const filterValue = toString(value).toLowerCase();
    return this.dskEligibleFields.filter(option =>
      (option.displayName || option.columnName)
        .toLowerCase()
        .includes(filterValue)
    );
  }
}
