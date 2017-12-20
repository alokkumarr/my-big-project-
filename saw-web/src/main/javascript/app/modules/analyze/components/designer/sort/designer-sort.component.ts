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
import * as take from 'lodash/take';
import * as has from 'lodash/has';
import * as takeRight from 'lodash/takeRight';
import {
  ArtifactColumns,
  ArtifactColumn,
  Sort
} from '../types';

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

  public checkedFields: ArtifactColumns = [];
  public availableFields: ArtifactColumns = [];
  public nameMap: Object = {};

  public removeFromAvailableFields = (artifactColumn: ArtifactColumn) => {
    this.availableFields = filter(this.availableFields,
      ({columnName}) => columnName !== artifactColumn.columnName);
  }

  public removeFromSortedFields = (sort: Sort) => {
    this.sorts = filter(this.sorts,
      ({columnName}) => columnName !== sort.columnName);
  }

  public addToSortedFields = (item: Sort | ArtifactColumn, index) => {
    const firstN = take(this.sorts, index);
    const lastN = takeRight(this.sorts, this.sorts.length - index);
    this.sorts = [
      ...firstN,
      this.isSort(item) ? item : this.transform(item),
      ...lastN
    ];
    this.sortsChange.emit(this.sorts);
  }

  isSort(item) {
    return has(item, 'order');
  }

  ngOnInit() {
    this.checkedFields = filter(this.artifactColumns, 'checked');
    this.availableFields = this.getavailableFields(this.checkedFields, this.sorts);
    this.nameMap = reduce(this.checkedFields, (accumulator, field) => {
      accumulator[field.columnName] = field.aliaName || field.displayName;
      return accumulator;
    }, {});
  }

  addSort(artifactColumn: ArtifactColumn, index: number = this.sorts.length) {
    this.removeFromAvailableFields(artifactColumn);
    this.addToSortedFields(artifactColumn, index);
  }

  removeSort(sort: Sort) {
    this.removeFromSortedFields(sort);
    this.availableFields = this.getavailableFields(this.checkedFields, this.sorts);
    this.sortsChange.emit(this.sorts);
  }

  transform({columnName, type}): Sort {
    return {
      columnName,
      type,
      order: 'asc'
    };
  }

  getavailableFields(checkedFields: ArtifactColumns, sorts: Sort[]) {
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
}
