declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as fpFilter from 'lodash/fp/filter';
import * as filter from 'lodash/filter';
import * as map from 'lodash/map';
import * as clone from 'lodash/clone';
import * as reduce from 'lodash/reduce';
import * as find from 'lodash/find';
import * as take from 'lodash/take';
import * as has from 'lodash/has';
import * as takeRight from 'lodash/takeRight';
import {
  ArtifactColumns,
  ArtifactColumn,
  ArtifactColumnReport,
  Artifact,
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
  @Input() public artifacts: Artifact[];
  @Input() public sorts: Sort[];

  public checkedFields: ArtifactColumns = [];
  public availableFields: ArtifactColumns = [];
  public nameMap: Object = {};
  public TYPE_MAP = TYPE_MAP;

  public removeFromAvailableFields = (artifactColumn: ArtifactColumn) => {
    this.availableFields = filter(this.availableFields,
      ({columnName}) => columnName !== artifactColumn.columnName);
  }

  public removeFromSortedFields = (sort: Sort) => {
    this.sorts = filter(this.sorts,
      ({columnName}) => columnName !== sort.columnName);
  }

  public addToSortedFields = (item, index) => {
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
    this.checkedFields = fpPipe(
      fpFlatMap((artifact: Artifact) => {
        return map(artifact.columns, column => {
          const cloned = clone(column);
          cloned.tableName = artifact.artifactName;
          return cloned;
        })
      }),
      fpFilter('checked')
    )(this.artifacts);
    this.availableFields = this.getAvailableFields(this.checkedFields, this.sorts);
    this.nameMap = reduce(this.checkedFields, (accumulator, field) => {
      accumulator[field.columnName] = field.displayName;
      return accumulator;
    }, {});
  }

  addSort(artifactColumn: ArtifactColumnReport, index: number = this.sorts.length) {
    this.removeFromAvailableFields(artifactColumn);
    this.addToSortedFields(artifactColumn, index);
  }

  removeSort(sort: Sort) {
    this.removeFromSortedFields(sort);
    this.availableFields = this.getAvailableFields(this.checkedFields, this.sorts);
    this.sortsChange.emit(this.sorts);
  }

  transform({columnName, type, tableName}: ArtifactColumnReport): Sort {
    return {
      tableName,
      columnName,
      type,
      order: 'asc'
    };
  }

  getAvailableFields(checkedFields: ArtifactColumns, sorts: Sort[]) {
    return filter(checkedFields, field => {
      return !find(this.sorts, ({columnName, tableName}) => (
        tableName === field.tableName &&
        columnName === field.columnName
      ));
    });
  }

  onSortOrderChange(sort, value) {
    sort.order = value;
    this.sortsChange.emit(this.sorts);
  }

  trackByFn(_, {tableName, columnName}) {
    return `${tableName}:${columnName}`;
  }
}
