import { Component, OnInit, Input } from '@angular/core';
import * as keys from 'lodash/keys';
import * as forEach from 'lodash/forEach';
import * as union from 'lodash/union';

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
  public gridolumns: Array<StreamGridColumns> = [];
  public DEFAULT_PAGE_SIZE;
  public enaplePaging = false;

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
    forEach(this.gridData, data => {
      cols = union(cols, keys(data));
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
    this.gridolumns = gridColumn;
  }

  customizeColumn(columns: StreamGridColumns) {
    forEach(columns, col => {
      col.alignment = 'left';
    });
  }
}
