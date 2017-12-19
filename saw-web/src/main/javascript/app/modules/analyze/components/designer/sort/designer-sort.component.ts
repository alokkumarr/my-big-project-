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
  public checkedFields: ArtifactColumns = [];
  public sortableFields: ArtifactColumns = [];
  public nameMap: Object = {};
  public dndSortableContainerObj = {
    zone: 'sortableContainer'
  };
  public dndSortedContainerObj = {};

  ngOnInit() {
    this.checkedFields = filter(this.artifactColumns, 'checked');
    this.sortableFields = this.getSortableFields(this.checkedFields, this.sorts);
    this.nameMap = reduce(this.checkedFields, (accumulator, field) => {
      accumulator[field.columnName] = field.aliaName || field.displayName;
      return accumulator;
    }, {});
  }

  addSort(artifactColumn: ArtifactColumn) {
    this.sorts = [
      ...this.sorts,
      this.transform(artifactColumn)
    ];
    this.sortableFields = filter(this.sortableFields,
      ({columnName}) => columnName !== artifactColumn.columnName);
    this.sortsChange.emit(this.sorts);
  }

  removeSort(sort: Sort) {
    this.sorts = filter(this.sorts,
      ({columnName}) => columnName !== sort.columnName);
    this.sortableFields = this.getSortableFields(this.checkedFields, this.sorts);
    this.sortsChange.emit(this.sorts);
  }

  transform({columnName, type}: ArtifactColumn): Sort {
    return {
      columnName,
      type,
      order: 'asc'
    };
  }

  getSortableFields(checkedFields: ArtifactColumns, sorts: Sort[]) {
    return filter(checkedFields, field => {
      return !find(this.sorts, ({columnName}) => columnName === field.columnName);
    });
  }

  onSortOrderChange(sort, value) {
    sort.order = value;
    this.sortsChange.emit(this.sorts);
  }

  trackByFn(_, {columnName}) {
    return columnName;
  }

  onDrop(event) {
    console.log('drop', event);
  }

  onSortableDragEnd(event, fromIndex) {
    console.log('sortableDragEnd', event);
  }

  onSortedDragEnd(event, fromIndex) {
    console.log('sortedDragEnd', event);
  }
}
