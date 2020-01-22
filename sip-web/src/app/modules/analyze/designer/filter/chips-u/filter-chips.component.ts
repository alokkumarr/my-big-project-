import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Filter, Artifact } from '../../types';
import { getFilterValue } from './../../../../analyze/consts';
import { AnalyzeService } from '../../../services/analyze.service';

import { ArtifactDSL } from '../../../../../models/analysis-dsl.model';
import { AGGREGATE_TYPES_OBJ } from 'src/app/common/consts';

@Component({
  selector: 'filter-chips-u',
  templateUrl: './filter-chips.component.html',
  styleUrls: ['./filter-chips.component.scss']
})
export class FilterChipsComponent {
  @Output() remove: EventEmitter<number> = new EventEmitter();
  @Output() removeAll: EventEmitter<null> = new EventEmitter();
  @Input() filters: Filter[];
  @Input('artifacts')
  set artifacts(artifacts: Artifact[] | ArtifactDSL[]) {
    if (!artifacts) {
      return;
    }
    this.nameMap = this.analyzeService.calcNameMap(artifacts);
  }
  @Input() readonly: boolean;

  public nameMap;

  constructor(private analyzeService: AnalyzeService) {}

  getDisplayName(filter: Filter) {
    const columnName = this.nameMap[filter.tableName || filter.artifactsName][
      filter.columnName
    ];
    const filterName =
      filter.isAggregationFilter && filter.aggregate
        ? `${
            AGGREGATE_TYPES_OBJ[filter.aggregate].designerLabel
          }(${columnName})`
        : columnName;
    return filterName + this.getFilterValue(filter);
  }

  onRemove(index) {
    this.remove.emit(index);
  }

  getFilterValue(filter: Filter) {
    return getFilterValue(filter);
  }

  onRemoveAll() {
    this.removeAll.emit();
  }
}
