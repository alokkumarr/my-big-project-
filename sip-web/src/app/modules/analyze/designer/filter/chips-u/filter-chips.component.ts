import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Filter, Artifact } from '../../types';
import { getFilterDisplayName } from './../../../../analyze/consts';
import { AnalyzeService } from '../../../services/analyze.service';

import { ArtifactDSL } from '../../../../../models/analysis-dsl.model';

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
    return getFilterDisplayName(this.nameMap, filter);
  }

  onRemove(index) {
    this.remove.emit(index);
  }

  onRemoveAll() {
    this.removeAll.emit();
  }
}
