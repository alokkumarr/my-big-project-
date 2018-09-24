import * as defaults from 'lodash/defaults';
import * as forEach from 'lodash/forEach';
import { Injectable } from '@angular/core';

@Injectable()
export class dxDataGridService {
  mergeWithDefaultConfig(config) {
    return defaults({}, config, this.getDefaultConfig());
  }

  getDefaultConfig() {
    return {
      columnAutoWidth: true,
      columnMinWidth: 150,
      columnResizingMode: 'widget',
      allowColumnReordering: true,
      allowColumnResizing: true,
      showColumnHeaders: true,
      showColumnLines: false,
      showRowLines: false,
      showBorders: false,
      rowAlternationEnabled: true,
      hoverStateEnabled: true,
      wordWrapEnabled: false,
      scrolling: {
        mode: 'virtual'
      },
      sorting: {
        mode: 'multiple'
      },
      customizeColumns: columns => {
        forEach(columns, col => {
          col.alignment = 'left';
        });
      },
      height: 'auto',
      width: 'auto'
    };
  }
}
