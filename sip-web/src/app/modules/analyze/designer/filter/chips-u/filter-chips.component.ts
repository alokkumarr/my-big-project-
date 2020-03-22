import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Filter, Artifact } from '../../types';
import { getFilterDisplayName } from './../../../../analyze/consts';
import { AnalyzeService } from '../../../services/analyze.service';
import * as cloneDeep from 'lodash/cloneDeep';

import { ArtifactDSL } from '../../../../../models/analysis-dsl.model';

@Component({
  selector: 'filter-chips-u',
  templateUrl: './filter-chips.component.html',
  styleUrls: ['./filter-chips.component.scss']
})
export class FilterChipsComponent {
  @Output() remove: EventEmitter<number> = new EventEmitter();
  @Output() removeAll: EventEmitter<null> = new EventEmitter();
  @Input() filters;
  @Input('artifacts')
  set artifacts(artifacts: Artifact[] | ArtifactDSL[]) {
    if (!artifacts) {
      return;
    }
    this.nameMap = this.analyzeService.calcNameMap(artifacts);
    this.flattenedFilters = cloneDeep(this.analyzeService.flattenAndFetchFiltersChips(this.filters, []));
  }
  @Input() readonly: boolean;

  public nameMap;
  public flattenedFilters = [];

  constructor(
    private analyzeService: AnalyzeService
  ) {}

  getDisplayName(filter: Filter) {
    return getFilterDisplayName(this.nameMap, filter);
  }

  onRemove(filter, index) {
    this.analyzeService.deleteFilterFromTree(this.filters[0], filter.uuid);
    this.flattenedFilters.splice(index, 1);
    if (this.flattenedFilters.length === 0) {
      this.onRemoveAll();
    }
  }

  onRemoveAll() {
    this.filters = [];
    this.flattenedFilters = [];
    this.removeAll.emit();
  }
}
