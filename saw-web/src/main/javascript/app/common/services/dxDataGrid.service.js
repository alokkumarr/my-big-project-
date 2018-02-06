import * as defaults from 'lodash/defaults';

export class dxDataGridService {
  constructor() {
    'ngInject';
  }

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
      wordWrapEnabled: true,
      scrolling: {
        mode: 'virtual'
      },
      sorting: {
        mode: 'multiple'
      }
    };
  }
}
