import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as filter from 'lodash/filter';
import * as map from 'lodash/map';
import * as clone from 'lodash/clone';
import * as reduce from 'lodash/reduce';
import * as find from 'lodash/find';
import * as take from 'lodash/take';
import * as has from 'lodash/has';
import * as uniqBy from 'lodash/uniqBy';
import * as isEmpty from 'lodash/isEmpty';
import * as isUndefined from 'lodash/isUndefined';
import * as takeRight from 'lodash/takeRight';
import {
  ArtifactColumns,
  ArtifactColumn,
  ArtifactColumnReport,
  Sort
} from '../types';
import { TYPE_MAP } from '../../consts';
import { ArtifactDSL } from '../../models';
import { displayNameWithoutAggregateFor } from 'src/app/common/services/tooltipFormatter';

@Component({
  selector: 'designer-sort',
  templateUrl: './designer-sort.component.html',
  styleUrls: ['./designer-sort.component.scss']
})
export class DesignerSortComponent implements OnInit {
  @Output() public sortsChange: EventEmitter<Sort[]> = new EventEmitter();
  @Input() public artifacts: ArtifactDSL[];
  @Input() public sorts: Sort[];

  public checkedFields: ArtifactColumns = [];
  public availableFields: ArtifactColumns = [];
  public nameMap: Object = {};
  public TYPE_MAP = TYPE_MAP;
  public isEmpty = isEmpty;

  public removeFromAvailableFields(artifactColumn: ArtifactColumn) {
    this.availableFields = filter(
      this.availableFields,
      ({ columnName }) => columnName !== artifactColumn.columnName
    );
  }

  public removeFromSortedFields(sort: Sort) {
    this.sorts = filter(
      this.sorts,
      ({ columnName }) => columnName !== sort.columnName
    );
  }

  public addToSortedFields(item, index) {
    const firstN = take(this.sorts, index);
    const lastN = takeRight(this.sorts, this.sorts.length - index);
    this.sorts = [
      ...firstN,
      this.isSort(item) ? item : this.transform(item),
      ...lastN
    ];
    this.sortsChange.emit(this.sorts);
  }

  displayNameFor(column) {
    return displayNameWithoutAggregateFor(column);
  }

  isSort(item) {
    return has(item, 'order');
  }

  ngOnInit() {
    this.checkedFields = fpPipe(
      fpFlatMap((artifact: ArtifactDSL) => {
        return map(artifact.fields, column => {
          const cloned = clone(column);
          cloned.artifactsName = artifact.artifactsName;
          return cloned;
        });
      })
    )(this.artifacts);
    this.availableFields = this.getAvailableFields(
      this.checkedFields,
      this.sorts
    );
    this.nameMap = reduce(
      this.checkedFields,
      (accumulator, field) => {
        accumulator[field.columnName] = this.displayNameFor(field);
        return accumulator;
      },
      {}
    );
  }

  addSort(
    artifactColumn: ArtifactColumnReport,
    index: number = this.sorts.length
  ) {
    this.removeFromAvailableFields(artifactColumn);
    this.addToSortedFields(artifactColumn, index);
  }

  removeSort(sort: Sort) {
    this.removeFromSortedFields(sort);
    this.availableFields = this.getAvailableFields(
      this.checkedFields,
      this.sorts
    );
    this.sortsChange.emit(this.sorts);
  }

  transform({
    columnName,
    aggregate,
    type,
    artifactsName
  }: ArtifactColumnReport): Sort {
    return {
      artifactsName,
      aggregate,
      columnName,
      type,
      order: 'asc'
    };
  }

  getAvailableFields(checkedFields: ArtifactColumns, sorts: Sort[]) {
    return uniqBy(
      filter(checkedFields, field => {
        return (
          !field.expression && // derived metrics are not supported for sorts
          !find(this.sorts, ({ columnName, artifactsName }) =>
            isUndefined(artifactsName)
              ? columnName === field.columnName
              : artifactsName === field.artifactsName &&
                columnName === field.columnName
          )
        );
      }),
      field => field.columnName
    );
  }

  onSortOrderChange(sort, value) {
    sort.order = value;
    this.sortsChange.emit(this.sorts);
  }

  trackByFn(_, { artifactsName, columnName }) {
    return `${artifactsName}:${columnName}`;
  }
}
