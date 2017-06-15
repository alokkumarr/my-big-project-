import fpGroupBy from 'lodash/fp/groupBy';
import fpPipe from 'lodash/fp/pipe';
import fpMapValues from 'lodash/fp/mapValues';
import cloneDeep from 'lodash/cloneDeep';

import template from './analyze-pivot-settings.component.html';
import style from './analyze-pivot-settings.component.scss';

export const ANALYZE_PIVOT_SETTINGS_SIDENAV_ID = 'ANALYZE_PIVOT_SETTINGS_SIDENAV_ID';

const AGGREGATE_TYPES = [{
  label: 'Sum',
  value: 'sum',
  icon: 'icon-Sum'
}, {
  label: 'Average',
  value: 'avg',
  icon: 'icon-AVG'
}, {
  label: 'Mininum',
  value: 'min',
  icon: 'icon-MIN'
}, {
  label: 'Maximum',
  value: 'max',
  icon: 'icon-MAX'
}, {
  label: 'Count',
  value: 'count',
  icon: 'icon-group-by-column'
}];

const DEFAULT_SUMMARY_TYPE = AGGREGATE_TYPES[0];
const AGGREGATE_TYPES_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(AGGREGATE_TYPES);

const AREA_TYPES = [{
  label: 'Row',
  value: 'row',
  icon: 'icon-row'
}, {
  label: 'Column',
  value: 'column',
  icon: 'icon-column'
}, {
  label: 'Data',
  value: 'data',
  icon: 'icon-data'
}];

const NUMBER_ICON = 'icon-sort';
const DATE_ICON = 'icon-calendar';
const STRING_ICON = 'icon-key';
const NUMBER_TOOLTIP = 'TOOLTIP_NUMBER_TYPE';
const STRING_TOOLTIP = 'TOOLTIP_STRING_TYPE';
const DATE_TOOLTIP = 'TOOLTIP_DATE_TYPE';
const ARTIFACT_ICON_TYPES_OBJ = {
  string: {
    tooltip: STRING_TOOLTIP,
    icon: STRING_ICON
  },
  long: {
    tooltip: NUMBER_TOOLTIP,
    icon: NUMBER_ICON
  },
  int: {
    tooltip: NUMBER_TOOLTIP,
    icon: NUMBER_ICON
  },
  integer: {
    tooltip: NUMBER_TOOLTIP,
    icon: NUMBER_ICON
  },
  double: {
    tooltip: NUMBER_TOOLTIP,
    icon: NUMBER_ICON
  },
  float: {
    tooltip: NUMBER_TOOLTIP,
    icon: NUMBER_ICON
  },
  timestamp: {
    tooltip: DATE_TOOLTIP,
    icon: DATE_ICON
  },
  date: {
    tooltip: DATE_TOOLTIP,
    icon: DATE_ICON
  }
};

const DEFAULT_AREA_TYPE = AREA_TYPES[0];
const AREA_TYPES_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(AREA_TYPES);

export const AnalyzePivotSettingsComponent = {
  template,
  styles: [style],
  bindings: {
    onApplySettings: '&',
    reciever: '<'
  },
  controller: class AnalyzePivotSettingsController {
    constructor(AnalyzeService, FilterService, $mdSidenav) {
      'ngInject';
      // TODO filter possible areas based on column type
      this.AGGREGATE_TYPES = AGGREGATE_TYPES;
      this.AGGREGATE_TYPES_OBJ = AGGREGATE_TYPES_OBJ;
      this.DEFAULT_SUMMARY_TYPE = DEFAULT_SUMMARY_TYPE;

      this.AREA_TYPES = AREA_TYPES;
      this.AREA_TYPES_OBJ = AREA_TYPES_OBJ;
      this.DEFAULT_AREA_TYPE = DEFAULT_AREA_TYPE;

      this.ARTIFACT_ICON_TYPES_OBJ = ARTIFACT_ICON_TYPES_OBJ;

      this.ANALYZE_PIVOT_SETTINGS_SIDENAV_ID = ANALYZE_PIVOT_SETTINGS_SIDENAV_ID;

      this._FilterService = FilterService;
      this._AnalyzeService = AnalyzeService;
      this._$mdSidenav = $mdSidenav;
    }

    $onInit() {
      this.subscribtion = this.reciever.subscribe(event => this.onRecieve(event));
    }

    $onDestroy() {
      this.subscribtion.unsubscribe();
    }

    onRecieve(event) {
      if (event.eventName === 'open') {
        this.onOpenSidenav(event.payload.artifactColumns);
      }
    }

    onOpenSidenav(artifactColumns) {
      this._$mdSidenav(ANALYZE_PIVOT_SETTINGS_SIDENAV_ID).open();
      this.artifactColumns = cloneDeep(artifactColumns);
    }

    openMenu($mdMenu, ev) {
      $mdMenu.open(ev);
    }

    applySettings(artifactColumns) {
      this._$mdSidenav(ANALYZE_PIVOT_SETTINGS_SIDENAV_ID).close();
      this.onApplySettings({columns: artifactColumns});
    }

    onChecked(artifactColumn) {
      if (!artifactColumn.area) {
        artifactColumn.area = DEFAULT_AREA_TYPE.value;
      }
    }

    onSelectAreaType(area, artifactColumn) {
      artifactColumn.area = area;

      if (artifactColumn.area === 'data' && !artifactColumn.aggregate) {
        artifactColumn.aggregate = DEFAULT_SUMMARY_TYPE.value;
      }
    }

    onSelectAggregateType(aggregateType, artifactColumn) {
      artifactColumn.aggregate = aggregateType.value;
    }

    inputChanged(field) {
      this.onChange({field});
    }
  }
};
