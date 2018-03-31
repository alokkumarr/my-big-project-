declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter,
  ViewChild
} from '@angular/core';
import { DxDataGridComponent } from 'devextreme-angular';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as fpMap from 'lodash/fp/map';
import * as fpFilter from 'lodash/fp/filter';
import * as reduce from 'lodash/reduce';
import {
  ArtifactColumnReport,
  Artifact,
  Sort
} from './types';
import {
  DATE_TYPES,
  NUMBER_TYPES,
  FLOAT_TYPES,
  DATE_INTERVALS_OBJ
} from '../../../modules/analyze/consts';

const ARTIFACT_COLUMN_2_PIVOT_FIELD = {
  displayName: 'caption',
  columnName: 'dataField',
  aggregate: 'summaryType'
};

const template = require('./report-grid.component.html');

type ReportGridSort = {
  order: 'asc' | 'desc';
  index: number;
}

type ReportGridField = {
  caption: string;
  dataField: string;
  dataType: string,
  type: string,
  visibleIndex: number,
  allowSorting: boolean,
  alignment: string,
  width: string,
  format?: string | object,
  sortOrder?: 'asc' | 'desc',
  sortIndex?: number;
}

@Component({
  selector: 'report-grid-upgraded',
  template
})

export class ReportGridComponent {
  public columns: ReportGridField[];
  public data;
  @ViewChild(DxDataGridComponent) dataGrid: DxDataGridComponent;
  @Input() query: string;
  @Input('sorts') set setSorts(sorts: Sort[]) {
    console.log('sorts', sorts);
    this.sorts = reduce(sorts, (acc, sort, index) => {
      const reportGirdSort: ReportGridSort = {
        order: sort.order,
        index
      }
      acc[sort.columnName] = reportGirdSort;
      return acc;
    }, {});
  };
  @Input('artifacts') set setArtifactColumns(artifacts: Artifact[]) {
    this.columns = this.artifacts2Columns(artifacts);
  };
  @Input('queryColumns') set setQueryColumns(queryColumns) {
    // TODO merge with SAW - 2002 for queryColumns
    // for query mode
    this.columns = this.queryColumns2Columns(queryColumns);
  }
  @Input('data') set setData(data: any[]) {
    this.data = data;
  };

  public sorts: {};

  // grid settings
  public columnAutoWidth = true;
  public columnMinWidth = 150;
  public columnResizingMode = 'widget';
  public allowColumnReordering = true;
  public allowColumnResizing = true;
  public showColumnHeaders = true;
  public showColumnLines = false;
  public showRowLines = false;
  public showBorders = false;
  public rowAlternationEnabled = true;
  public hoverStateEnabled = true;
  public wordWrapEnabled = true;
  public scrolling = {mode: 'scrolling'};
  public sorting = {mode: 'multiple'};
  public gridHeight = '100%';
  public gridWidth = '100%';

  onContextMenuPreparing(event) {
    const { target, column } = event;
    if (target === 'header') {
      event.items = [{
        text: 'Rename',
        icon: 'grid-menu-item icon-edit',
        onItemClick: () => {
          this.renameColumn(column);
        }
      }, {
        text: `Hide ${column.caption}`,
        icon: 'grid-menu-item icon-eye-disabled',
        onItemClick: () => {
          this.hideColumn(column);
        }
      }];
      if (NUMBER_TYPES.includes(column.dataType) || DATE_TYPES.includes(column.dataType)) {
        event.items.unshift({
          text: 'Format Data',
          icon: 'grid-menu-item icon-filter',
          onItemClick: () => {
            this.formatColumn(column);
          }
        });
      }
    }
  }

  hideColumn(column) {

  }

  renameColumn(column) {

  }

  formatColumn(column) {

  }

  artifacts2Columns(artifacts: Artifact[]): ReportGridField[] {
    return fpPipe(
      fpFlatMap((artifact: Artifact) => {
        return artifact.columns;
      }),
      fpFilter('checked'),
      fpMap((column: ArtifactColumnReport) => {
        const field: ReportGridField = {
          caption: column.aliasName || column.displayName,
          dataField: column.columnName,
          dataType: NUMBER_TYPES.includes(column.type) ? 'number' : column.type,
          type: column.type,
          visibleIndex: column.visibleIndex,
          allowSorting: false,
          alignment: 'left',
          width: 'auto',
          ...this.getSortingPart(column)
        }

        return field;
      })
    )(artifacts);
  }

  queryColumns2Columns(queryColumns): ReportGridField[]  {
    return [];
  }

  getSortingPart(column: ArtifactColumnReport) {
    const sort = this.sorts[column.columnName];
    if (sort) {
      return {
        sortIndex: sort.index,
        sortOrder: sort.order
      }
    }
    return {};
  }
}
