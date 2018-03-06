declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter,
  ViewChild
} from '@angular/core';
import { DxDataGridComponent } from 'devextreme-angular';
import {
  ArtifactColumnReport,
  Artifact
} from '../../../models';
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

type ReportGridColumn = {
  caption: string;
  dataField: string;
  dataType: string,
  type: string,
  visibleIndex: number,
  allowSorting: boolean,
  alignment: string,
  width: string,
  format: string | object
}

@Component({
  selector: 'pivot-grid',
  template
})

export class ReportGridComponent {
  public columns: ReportGridColumn[];
  public data;
  @ViewChild(DxDataGridComponent) dataGrid: DxDataGridComponent;
  @Input('artifacts') set setArtifactColumns(artifacts: Artifact[]) {
    this.columns = this.artifacts2Columns(artifacts);
  };
  @Input('queryColumns') set setQueryColumns(queryColumns) {
    // todo merge with SAW - 2002 for queryColumns
    this.columns = this.queryColumns2Columns(queryColumns);
  }
  @Input('data') set setData(data: any[]) {
    this.data = data;
  };

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

  artifacts2Columns(artifacts: Artifact[]): ReportGridColumn[] {
    return [];
  }

  queryColumns2Columns(queryColumns): ReportGridColumn[]  {
    return [];
  }
}
