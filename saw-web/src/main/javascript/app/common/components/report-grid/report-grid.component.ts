declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter,
  ViewChild,
  ElementRef
} from '@angular/core';
import { DxDataGridComponent } from 'devextreme-angular';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as fpMap from 'lodash/fp/map';
import * as fpFilter from 'lodash/fp/filter';
import * as reduce from 'lodash/reduce';
import * as isUndefined from 'lodash/isUndefined';
import * as forEach from 'lodash/forEach';
import * as split from 'lodash/split';
import {MatDialog, MatDialogConfig} from '@angular/material';
import { DateFormatDialogComponent } from '../date-format-dialog';
import { DataFormatDialogComponent } from '../data-format-dialog';
import { AliasRenameDialogComponent } from '../alias-rename-dialog';
import { getFormatter } from '../../utils/numberFormatter';
import {
  ArtifactColumnReport,
  Artifact,
  Sort,
  ReportGridChangeEvent
} from './types';
import {
  DATE_TYPES,
  NUMBER_TYPES,
  FLOAT_TYPES,
  DATE_INTERVALS_OBJ
} from '../../../modules/analyze/consts';
import { componentFactoryName } from '@angular/compiler';

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
  payload: ArtifactColumnReport;
  format?: string | object,
  sortOrder?: 'asc' | 'desc',
  sortIndex?: number;
  changeColumnProp: Function;
}

@Component({
  selector: 'report-grid-upgraded',
  template
})

export class ReportGridComponent {
  public columns: ReportGridField[];
  public data;
  @Output() change: EventEmitter<ReportGridChangeEvent> = new EventEmitter();
  @ViewChild(DxDataGridComponent) dataGrid: DxDataGridComponent;
  @Input() query: string;
  @Input('sorts') set setSorts(sorts: Sort[]) {
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
    this.artifacts = artifacts;
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
  @Input() isEditable: boolean = false;

  public sorts: {};
  public artifacts: Artifact[];

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

  constructor(
    private _dialog: MatDialog,
    private _elemRef: ElementRef
  ) {}

  onContentReady({component}) {
    const cols = component.getVisibleColumns();
    let isVisibleIndexChanged = false;
    forEach(cols, (col: ReportGridField) => {
      if (col.visibleIndex !== col.payload.visibleIndex) {
        col.changeColumnProp('visibleIndex', col.visibleIndex);
        isVisibleIndexChanged = true;
      }
      if (isVisibleIndexChanged) {
        this.change.emit({subject: 'visibleIndex'});
      }
    });
  }

  onContextMenuPreparing(event) {
    const { target, column } = event;
    if (target !== 'header') {
      return;
    }
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
    if (NUMBER_TYPES.includes(column.type) || DATE_TYPES.includes(column.type)) {
      event.items.unshift({
        text: 'Format Data',
        icon: 'grid-menu-item icon-filter',
        onItemClick: () => {
          this.formatColumn(column);
        }
      });
    }
  }

  hideColumn({payload}: ReportGridField) {
    payload.visible = false;
    this.change.emit({subject: 'visibility'});
  }

  renameColumn({payload, changeColumnProp}: ReportGridField) {
    this.getNewDataThroughDialog(
      AliasRenameDialogComponent,
      { alias: payload.aliasName || '' },
      alias => {
        changeColumnProp('aliasName', alias);
        this.change.emit({subject: 'aliasName'});
      }
    );
  }

  formatColumn({type, changeColumnProp, payload}: ReportGridField) {
    let component;
    if (NUMBER_TYPES.includes(type)) {
      component = DataFormatDialogComponent;
    } else if (DATE_TYPES.includes(type)) {
      component = DateFormatDialogComponent;
    }

    this.getNewDataThroughDialog(
      component,
      {format: payload.format},
      format => {
        changeColumnProp('format', format);
        this.change.emit({subject: 'format'});
      }
    );
  }

  getNewDataThroughDialog(component, currentData, actionFn: Function) {
    this._dialog.open(component, {
      width: 'auto',
      height: 'auto',
      data: currentData
    } as MatDialogConfig).afterClosed().subscribe(newValue => {
      if (!isUndefined(newValue)) {
        actionFn(newValue);
      }
    });
  }

  artifacts2Columns(artifacts: Artifact[]): ReportGridField[] {
    return fpPipe(
      fpFlatMap((artifact: Artifact) => artifact.columns),
      fpFilter('checked'),
      fpMap((column: ArtifactColumnReport) => {
        const isNumberType = NUMBER_TYPES.includes(column.type);
        const format = isNumberType ? {formatter: getFormatter(column.format)} : column.format;
        const field: ReportGridField = {
          caption: column.aliasName || column.displayName,
          dataField: this.getDataField(column),
          dataType: isNumberType? 'number' : column.type,
          type: column.type,
          visibleIndex: column.visibleIndex,
          allowSorting: false,
          alignment: 'left',
          width: 'auto',
          payload: column,
          format,
          changeColumnProp: (prop, value) => {
            column[prop] = value;
          },
          ...this.getSortingPart(column)
        }
        return field;
      })
    )(artifacts);
  }

  getDataField(column: ArtifactColumnReport) {
    const parts = split(column.columnName, '.');
    return parts[0];
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
