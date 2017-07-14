import forEach from 'lodash/forEach';
import assign from 'lodash/assign';
import isUndefined from 'lodash/isUndefined';

import template from './report-grid-node.component.html';
import style from './report-grid-node.component.scss';

import {LAYOUT_MODE} from '../report-grid-container/report-grid-container.component';

export const ReportGridNodeComponent = {
  template,
  style: [style],
  bindings: {
    reportGridContainer: '<',
    reportGridNode: '<',
    source: '<',
    gridIdentifier: '@'
  },
  controller: class ReportGridNodeController {
    constructor() {
      this.LAYOUT_MODE = LAYOUT_MODE;
      this.gridNodeComponents = [];
      this.gridComponent = null;

      this.settings = {};
      this.columns = [];
    }

    $onInit() {
      if (this.reportGridNode) {
        this.reportGridNode.addGridNodeComponent(this);
      } else if (this.reportGridContainer) {
        this.reportGridContainer.setGridNodeComponent(this);
      }
    }

    $onDestroy() {
      if (this.reportGridNode) {
        this.reportGridNode.removeGridNodeComponent(this);
      } else if (this.reportGridContainer) {
        this.reportGridContainer.setGridNodeComponent(null);
      }
    }

    addGridNodeComponent(gridNodeComponent) {
      if (gridNodeComponent) {
        this.gridNodeComponents.push(gridNodeComponent);

        gridNodeComponent.updateSettings({
          layoutMode: this.settings.layoutMode
        });

        gridNodeComponent.updateColumns(this.columns);
      }
    }

    removeGridNodeComponent(gridNodeComponent) {
      const idx = this.gridNodeComponents.indexOf(gridNodeComponent);

      if (idx !== -1) {
        this.gridNodeComponents.splice(idx, 1);
      }
    }

    setGridComponent(gridComponent) {
      this.gridComponent = gridComponent;

      if (this.gridComponent) {
        this.gridComponent.updateSettings({
          layoutMode: this.settings.layoutMode
        });

        this.gridComponent.updateColumns(this.columns);
        this.gridComponent.updateSorts();
      }
    }

    updateSettings(settings) {
      this.settings = assign(this.settings, settings);

      if (this.gridComponent) {
        this.gridComponent.updateSettings({
          layoutMode: this.settings.layoutMode
        });
      }

      forEach(this.gridNodeComponents, gridNode => {
        gridNode.updateSettings({
          layoutMode: this.settings.layoutMode
        });
      });
    }

    updateColumns(columns) {
      if (!isUndefined(columns)) {
        this.columns = columns;
      }

      if (this.gridComponent) {
        this.gridComponent.updateColumns(this.columns);
      }

      forEach(this.gridNodeComponents, gridNode => {
        gridNode.updateColumns(this.columns);
      });
    }

    updateSorts(sorts) {
      if (!isUndefined(sorts)) {
        this.sorts = sorts;
      }

      if (this.gridComponent) {
        this.gridComponent.updateSorts(this.sorts);
      }

      forEach(this.gridNodeComponents, gridNode => {
        gridNode.updateSorts(this.sorts);
      });
    }

    onSourceUpdate() {
      if (this.gridComponent) {
        this.gridComponent.onSourceUpdate();
      }

      forEach(this.gridNodeComponents, gridNode => {
        gridNode.onSourceUpdate();
      });
    }

    refreshGrid() {
      if (this.gridComponent) {
        this.gridComponent.refreshGrid();
      }

      forEach(this.gridNodeComponents, gridNode => {
        gridNode.refreshGrid();
      });
    }
  }
};
