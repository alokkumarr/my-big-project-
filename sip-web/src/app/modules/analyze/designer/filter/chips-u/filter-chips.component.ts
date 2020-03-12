import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Filter, Artifact } from '../../types';
import { getFilterDisplayName } from './../../../../analyze/consts';
import { AnalyzeService } from '../../../services/analyze.service';

import { ArtifactDSL } from '../../../../../models/analysis-dsl.model';
import * as forEach from 'lodash/forEach';
import * as isArray from 'lodash/isArray';
@Component({
  selector: 'filter-chips-u',
  templateUrl: './filter-chips.component.html',
  styleUrls: ['./filter-chips.component.scss']
})
export class FilterChipsComponent {
  @Output() remove: EventEmitter<number> = new EventEmitter();
  @Output() removeAll: EventEmitter<null> = new EventEmitter();
  @Input('filters')
  set _filters(val) {
    this.filters = [];
    this.fetchFilters(val);
  }
  @Input('artifacts')
  set artifacts(artifacts: Artifact[] | ArtifactDSL[]) {
    if (!artifacts) {
      return;
    }
    this.nameMap = this.analyzeService.calcNameMap(artifacts);
  }
  @Input() readonly: boolean;

  public nameMap;
  public filters= [];

  constructor(private analyzeService: AnalyzeService) {}

  getDisplayName(filter: Filter) {
    return getFilterDisplayName(this.nameMap, filter);
  }

  onRemove(index) {
    this.remove.emit(index);
  }

  onRemoveAll() {
    this.removeAll.emit();
  }

  fetchFilters(filters) {
    forEach(filters, filter => {
      if (filter.filters || isArray(filter)) {
        this.fetchFilters(filter);
      }
      if (filter.columnName) {
        this.filters.push(filter);
      }
    })
  }
}
