import { Component, OnInit, Input } from '@angular/core';
import { DSKFilterGroup, DSKFilterBooleanCriteria } from '../dsk-filter.model';

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

  @Input('filterGroup') set _filterGroup(filters: DSKFilterGroup) {
    this.filterGroup = filters || cloneDeep(defaultFilters);
  }

  constructor() {}

  ngOnInit() {}
}
