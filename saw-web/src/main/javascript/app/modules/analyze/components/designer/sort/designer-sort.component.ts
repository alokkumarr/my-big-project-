import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as filter from 'lodash/filter';
import * as map from 'lodash/map';
import * as reduce from 'lodash/reduce';
import * as find from 'lodash/find';
import {
  ArtifactColumns,
  ArtifactColumn,
  Sort
} from '../types';
import { TYPE_MAP } from '../../../consts';

const template = require('./designer-sort.component.html');
require('./designer-sort.component.scss');

@Component({
  selector: 'designer-sort',
  template
})
export class DesignerSortComponent {
  @Output() public sortsChange: EventEmitter<Sort[]> = new EventEmitter();
  @Input() public artifactColumns: ArtifactColumns;
  @Input() public sorts: Sort[];

  public TYPE_MAP = TYPE_MAP;
  public sortableFields: ArtifactColumns = [];
  public nameMap: Object = {};

  ngOnInit() {
    this.sortableFields = this.getSortableFields(this.artifactColumns, this.sorts);
    this.nameMap = reduce(this.sortableFields, (accumulator, field) => {
      accumulator[field.columnName] = field.aliaName || field.displayName;
      return accumulator;
    }, {});
  }

  addSort(artifactColumn: ArtifactColumn) {
    this.sortableFields = filter(this.sortableFields,
      ({columnName}) => columnName !== artifactColumn.columnName);

    this.sorts = [
      ...this.sorts,
      this.transform(artifactColumn)
    ];
    this.sortsChange.emit(this.sorts);
  }

  transform({columnName, type}: ArtifactColumn): Sort {
    return {
      columnName,
      type,
      order: 'asc'
    };
  }

  getSortableFields(artifactcolumns: ArtifactColumns, sorts: Sort[]) {
    return filter(this.artifactColumns, artifactColumn => {
      return artifactColumn.checked &&
        !find(this.sorts, ({columnName}) => columnName === artifactColumn.columnName);
    });
  }

  onSortOrderChange(sort, value) {
    sort.order = value;
    this.sortsChange.emit(this.sorts);
  }

  trackByFn(index, {columnName}) {
    return columnName;
  }
}
