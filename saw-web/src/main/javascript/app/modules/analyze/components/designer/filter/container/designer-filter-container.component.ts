import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as filter from 'lodash/filter';
import * as groupBy from 'lodash/groupBy';
import * as isEmpty from 'lodash/isEmpty';
import * as forEach from 'lodash/forEach';
import * as first from 'lodash/first';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpToPairs from 'lodash/fp/toPairs';
import * as fpFlatMap from 'lodash/fp/flatMap';

import {
  ArtifactColumns,
  ArtifactColumn,
  Filter,
  Artifact
} from '../../types';

const template = require('./designer-filter-container.component.html');
require('./designer-filter-container.component.scss');

@Component({
  selector: 'designer-filter-container',
  template
})
export class DesignerFilterContainerComponent {
  @Output() public filtersChange: EventEmitter<Filter[]> = new EventEmitter();
  @Input() public artifacts: Artifact[];
  @Input() public filters: Filter[];

  public groupedFilters;

  ngOnInit() {
    this.groupedFilters = groupBy(this.filters, 'tableName');
    const firstArtifactName = this.artifacts[0].artifactName;
    if (!this.groupedFilters[firstArtifactName]) {
      this.addFilter(firstArtifactName, true);
    }
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
      model: null
    }
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

  removeFilter(targetIndex, tableName) {
    this.groupedFilters[tableName] = filter(this.groupedFilters[tableName],
      (_, index) => targetIndex !== index);
    this.onFiltersChange();
  }

  onFiltersChange() {
    this.filters = fpPipe(
      fpToPairs,
      fpFlatMap(([_, filters]) => filters)
    )(this.groupedFilters);

    this.filtersChange.emit(this.filters);
  }

  artifactTrackByFn(_, artifact: Artifact) {
    return artifact.artifactName;
  }

}
