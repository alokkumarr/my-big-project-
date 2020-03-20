import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { DxDataGridComponent } from 'devextreme-angular';

import * as keys from 'lodash/keys';
import * as forEach from 'lodash/forEach';
import * as union from 'lodash/union';
import * as isArray from 'lodash/isArray';

interface StreamGridColumns {
  caption: string;
  dataField: string;
  dataType?: string;
  visibleIndex: number;
  visible: boolean;
  type?: string;
  allowSorting?: boolean;
  alignment?: 'center' | 'left' | 'right';
  format?: string | object;
  sortOrder?: 'asc' | 'desc';
  sortIndex?: number;
  changeColumnProp?: Function;
  headerCellTemplate?: string;
}
const DEFAULT_PAGE_SIZE = 10;
@Component({
  selector: 'stream-reader-grid',
  templateUrl: './stream-reader-grid.component.html',
  styleUrls: ['./stream-reader-grid.component.scss']
})
export class StreamReaderGridComponent implements OnInit {
  public gridData;
  public gridColumns: Array<StreamGridColumns> = [];
  public DEFAULT_PAGE_SIZE;
  public enaplePaging = false;
  public topicName: string;
  @ViewChild(DxDataGridComponent, { static: true })
  dataGrid: DxDataGridComponent;

  @Input('gridData') set setGridData(data) {
    if (data) {
      this.gridData = data;
      this.setGridColumns();
    }
  }
  constructor() {}

  ngOnInit() {
    this.DEFAULT_PAGE_SIZE = DEFAULT_PAGE_SIZE;
  }

  setGridColumns() {
    let cols = [];

    // Sorting is not cleared when grid data is changed so here clearing sort manually.
    if (this.dataGrid.instance) {
      this.dataGrid.instance.clearSorting();
    }

    forEach(this.gridData, data => {
      if (!isArray(data)) {
        cols = union(cols, keys(data));
      }
    });

    this.enaplePaging =
      this.gridData.length > this.DEFAULT_PAGE_SIZE ? true : false;
    const gridColumn: StreamGridColumns[] = [];
    forEach(cols, (col, index) => {
      gridColumn.push({
        caption: col,
        dataField: col,
        visibleIndex: index,
        visible: true,
        allowSorting: true
      });
    });
    this.gridColumns = gridColumn;
  }

  customizeColumn(columns: StreamGridColumns) {
    forEach(columns, col => {
      col.alignment = 'left';
    });
  }
}
