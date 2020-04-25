import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Filter, Artifact, DesignerChangeEvent } from '../../types';
import { getFilterDisplayName } from './../../../../analyze/consts';
import { AnalyzeService } from '../../../services/analyze.service';
import * as cloneDeep from 'lodash/cloneDeep';
import * as get from 'lodash/get';

import { ArtifactDSL } from '../../../../../models/analysis-dsl.model';
import { DskFiltersService } from './../../../../../common/services/dsk-filters.service';

@Component({
  selector: 'filter-chips-u',
  templateUrl: './filter-chips.component.html',
  styleUrls: ['./filter-chips.component.scss']
})
export class FilterChipsComponent {
  @Output() remove: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Output() removeAll: EventEmitter<null> = new EventEmitter();
  private filters;
  @Output() onFilterClick = new EventEmitter();
  @Input('filters') set _filters(value) {
    this.filters = value;
    this.refreshFilters();
  }
  private artifacts = [];
  @Input('artifacts')
  set _artifacts(artifacts: Artifact[] | ArtifactDSL[]) {
    if (!artifacts) {
      return;
    }
    this.artifacts = artifacts;
    this.refreshFilters();
  }
  @Input() readonly: boolean;

  public nameMap;
  public flattenedFilters = [];
  public previewString;
  public aggregatePreview;
  constructor(
    private analyzeService: AnalyzeService,
    private datasecurityService: DskFiltersService
  ) {}

  getDisplayName(filter: Filter) {
    return getFilterDisplayName(this.nameMap, filter);
  }

  onRemove(filter, index) {
    // this.analyzeService.deleteFilterFromTree(this.filters[0], filter.uuid);
    // this.flattenedFilters.splice(index, 1);
    // if (this.flattenedFilters.length === 0) {
    //   this.onRemoveAll();
    // }
    if (this.filters[0].booleanCriteria) {
      this.flattenedFilters.splice(index, 1);
      if (filter.isAggregationFilter) {
        this.filters = cloneDeep(this.filters.filter(option => {
          return option.uuid !== filter.uuid;
        }));
        this.remove.emit({subject: 'filters', data: this.filters});
      } else {
        this.analyzeService.deleteFilterFromTree(this.filters[0], filter.uuid);
        setTimeout(() => {
          this.remove.emit({subject: 'filters', data: this.filters});
        }, 650);
      }


    } else {
      this.flattenedFilters.splice(index, 1);
      this.filters.splice(index, 1);
      this.remove.emit({subject: 'filters', data: this.filters});
    }
  }

  onRemoveAll() {
    this.filters = [];
    this.flattenedFilters = [];
    this.removeAll.emit();
  }

  refreshFilters() {
    this.nameMap = this.analyzeService.calcNameMap(this.artifacts || []);
    this.flattenedFilters = cloneDeep(
      this.analyzeService.flattenAndFetchFiltersChips(this.filters, [])
    );
    this.previewString = this.datasecurityService.generatePreview(
      this.changeIndexToNames(this.filters, 'booleanQuery', 'filters'), 'ANALYZE'
    );

    const aggregatedFilters = this.filters.filter(option => {
      return option.isAggregationFilter === true;
    });

    this.aggregatePreview = aggregatedFilters.map(field => {
      if (!field.model) {
        return `<span ${field.isRuntimeFilter ? 'class="prompt-filter"' : ''}>
          <span ${field.isRuntimeFilter ? 'class="prompt-filter"' : ''}>
            ${field.aggregate}(${field.columnName.split('.keyword')[0]})
          </span>
        </span>`;
      }
      if (field.model.operator === 'BTW') {
        return `<span ${field.isRuntimeFilter ? 'class="prompt-filter"' : ''}>${field.aggregate}(${field.columnName.split('.keyword')[0]})</span> <span class="operator">${
          field.model.operator
        }</span> <span [attr.e2e]="'ffilter-model-value'">[${get(field, 'model.otherValue')} and ${get(field, 'model.value')}]</span>`;
      } else {
        return `<span ${field.isRuntimeFilter ? 'class="prompt-filter"' : ''}>${field.aggregate}(${field.columnName.split('.keyword')[0]})</span> <span class="operator">${
          field.model.operator || ''
        }</span> <span [attr.e2e]="'ffilter-model-value'">[${[get(field, 'model.value')]}]</span>`;
      }
    })
  }

  changeIndexToNames(dskObject, source, target) {
    const convertToString = JSON.stringify(dskObject);
    const replaceIndex = convertToString.replace(/"filters":/g, '"booleanQuery":');
    const convertToJson = JSON.parse(replaceIndex);
    return convertToJson[0];
  }

  openFilterPopUp() {
    this.onFilterClick.emit();
  }
}
