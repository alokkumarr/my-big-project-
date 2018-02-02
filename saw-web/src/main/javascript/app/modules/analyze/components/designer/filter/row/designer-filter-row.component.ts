import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as find from 'lodash/find';
import {
  ArtifactColumns,
  ArtifactColumn,
  Filter,
  FilterModel
} from '../../types';
import { TYPE_MAP } from '../../../../consts';

const template = require('./designer-filter-row.component.html');
require('./designer-filter-row.component.scss');

@Component({
  selector: 'designer-filter-row',
  template
})
export class DesignerFilterRowComponent {
  @Output() public removeRequest: EventEmitter<null> = new EventEmitter();
  @Output() public filterChange: EventEmitter<null> = new EventEmitter();
  @Input() public artifactColumns: ArtifactColumns;
  @Input() public filter: Filter;

  public TYPE_MAP = TYPE_MAP;

  onArtifactColumnSelected(columnName) {
    const target: ArtifactColumn = find(this.artifactColumns, column => column.columnName === columnName);
    this.filter.columnName = target.columnName;
    this.filter.type = target.type;
    if (this.filter.isRuntimeFilter) {
      delete this.filter.model;
    } else {
      this.filter.model = {};
    }
    this.filterChange.emit();
  }

  onFilterModelChange(filterModel: FilterModel) {
    this.filter.model = filterModel;
  }

  onRuntimeCheckboxToggle(filter: Filter, checked: boolean) {
    filter.isRuntimeFilter = checked;
    delete filter.model;
  }

  remove() {
    this.removeRequest.emit();
  }
}
