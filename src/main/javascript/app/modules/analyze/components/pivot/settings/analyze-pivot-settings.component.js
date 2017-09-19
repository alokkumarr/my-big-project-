import fpGroupBy from 'lodash/fp/groupBy';
import fpPipe from 'lodash/fp/pipe';
import fpMapValues from 'lodash/fp/mapValues';
import forEach from 'lodash/forEach';
import filter from 'lodash/filter';
import map from 'lodash/map';

import template from './analyze-pivot-settings.component.html';
import style from './analyze-pivot-settings.component.scss';
import {DATE_TYPES, MAX_POSSIBLE_FIELDS_OF_SAME_AREA,
  AGGREGATE_TYPES, DEFAULT_AGGREGATE_TYPE, AGGREGATE_TYPES_OBJ} from '../../../consts';

export const ANALYZE_PIVOT_SETTINGS_SIDENAV_ID = 'ANALYZE_PIVOT_SETTINGS_SIDENAV_ID';

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

const DEFAULT_AREA_TYPE = AREA_TYPES[0];
const AREA_TYPES_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(AREA_TYPES);

const NUMBER_ICON = 'icon-number-type';
const DATE_ICON = 'icon-calendar';
const STRING_ICON = 'icon-string-type';
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

const GROUP_INTERVALS = [{
  label: 'ALL',
  value: undefined
}, {
  label: 'YEAR',
  value: 'year'
}, {
  label: 'QUARTER',
  value: 'quarter'
}, {
  label: 'MONTH',
  value: 'month'
}, {
  label: 'DAY',
  value: 'day'
}, {
  label: 'DAY_OF_WEEK',
  value: 'dayOfWeek'
}];

export const DEFAULT_GROUP_INTERVAL = GROUP_INTERVALS[3];

export const AnalyzePivotSettingsComponent = {
  template,
  styles: [style],
  bindings: {
    onApplySettings: '&',
    artifactColumns: '<'
  },
  controller: class AnalyzePivotSettingsController {
    constructor(AnalyzeService, FilterService, $mdSidenav, $translate) {
      'ngInject';
      // TODO filter possible areas based on column type
      this.AGGREGATE_TYPES = AGGREGATE_TYPES;
      this.AGGREGATE_TYPES_OBJ = AGGREGATE_TYPES_OBJ;
      this.DEFAULT_AGGREGATE_TYPE = DEFAULT_AGGREGATE_TYPE;

      this.AREA_TYPES = AREA_TYPES;
      this.AREA_TYPES_OBJ = AREA_TYPES_OBJ;
      this.DEFAULT_AREA_TYPE = DEFAULT_AREA_TYPE;

      this.GROUP_INTERVALS = GROUP_INTERVALS;

      this.ARTIFACT_ICON_TYPES_OBJ = ARTIFACT_ICON_TYPES_OBJ;

      this.ANALYZE_PIVOT_SETTINGS_SIDENAV_ID = ANALYZE_PIVOT_SETTINGS_SIDENAV_ID;

      this.DATE_TYPES = DATE_TYPES;

      this._FilterService = FilterService;
      this._AnalyzeService = AnalyzeService;
      this._$mdSidenav = $mdSidenav;
      this._$translate = $translate;
    }

    $onInit() {
      this._$translate(map(GROUP_INTERVALS, 'label')).then(translations => {
        forEach(GROUP_INTERVALS, groupInterval => {
          groupInterval.label = translations[groupInterval.label];
        });
      });
    }

    openMenu($mdMenu, ev) {
      $mdMenu.open(ev);
    }

    onChecked(artifactColumn) {
      if (!artifactColumn.area) {
        artifactColumn.area = DEFAULT_AREA_TYPE.value;
      }
      if (!this.canBeChecked(artifactColumn)) {
        artifactColumn.checked = false;
        artifactColumn.area = null;
        return;
      }
      this.onApplySettings({columns: this.artifactColumns});
    }

    canBeChecked(artifactColumn) {
      // only 5 fields of the same type can be selected at a time
      const columnsWithSameArea = filter(this.artifactColumns,
        ({area, checked}) => checked && (artifactColumn.area === area));
      return columnsWithSameArea.length <= MAX_POSSIBLE_FIELDS_OF_SAME_AREA;
    }

    onSelectAreaType(area, artifactColumn) {
      artifactColumn.area = area;
      this.onApplySettings({columns: this.artifactColumns});
    }

    onSelectAggregateType(aggregateType, artifactColumn) {
      artifactColumn.aggregate = aggregateType.value;
      this.onApplySettings({columns: this.artifactColumns});
    }

    onSelectGroupInterval(groupInterval, artifactColumn) {
      artifactColumn.groupInterval = groupInterval.value;
      this.onApplySettings({columns: this.artifactColumns});
    }
  }
};
