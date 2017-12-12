import * as defaults from 'lodash/defaults';

export function dxDataGridService() {

  return {
    mergeWithDefaultConfig
  };

  function mergeWithDefaultConfig(config) {
    return defaults({}, config, getDefaultConfig());
  }

  function getDefaultConfig() {
    return {
      columnAutoWidth: true,
      columnMinWidth: 50,
      columnResizingMode: 'nextColumn',
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
