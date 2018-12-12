import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnInit,
  OnDestroy
} from '@angular/core';
import * as isArray from 'lodash/isArray';
import * as unset from 'lodash/unset';
import * as map from 'lodash/map';
import * as get from 'lodash/get';
import * as isEmpty from 'lodash/isEmpty';
import * as forEach from 'lodash/forEach';
import * as clone from 'lodash/clone';
import * as split from 'lodash/split';
import * as isPlainObject from 'lodash/isPlainObject';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpMap from 'lodash/fp/map';
import * as fpFilter from 'lodash/fp/filter';
import * as filter from 'lodash/filter';
import * as fpMapKeys from 'lodash/fp/mapKeys';
import * as moment from 'moment';
import * as isUndefined from 'lodash/isUndefined';
import { Subject } from 'rxjs';
import { DEFAULT_PRECISION } from '../data-format-dialog/data-format-dialog.component';
// import {PivotGridDataSourceField} from 'devextreme/ui/pivot_grid';
import PivotGridDataSource from 'devextreme/ui/pivot_grid/data_source';
import { ArtifactColumnPivot, Sort } from '../../../models';
import {
  DATE_TYPES,
  NUMBER_TYPES,
  FLOAT_TYPES,
  DATE_INTERVALS_OBJ,
  DATE_FORMATS_OBJ,
  DEFAULT_DATE_FORMAT
} from '../../../modules/analyze/consts';
import { getFormatter } from '../../utils/numberFormatter';

const ARTIFACT_COLUMN_2_PIVOT_FIELD = {
  displayName: 'caption',
  columnName: 'dataField',
  aggregate: 'summaryType'
};

export interface IPivotGridUpdate {
  dataSource?: any;
  fields?: any;
  data?: any;
  sorts?: any;
  export?: boolean;
}

@Component({
  selector: 'pivot-grid',
  templateUrl: './pivot-grid.component.html',
  styleUrls: ['./pivot-grid.component.scss']
})
export class PivotGridComponent implements OnInit, OnDestroy {
  @Input() updater: Subject<IPivotGridUpdate>;
  @Input() mode: string | 'designer';
  @Input() showFieldDetails;
  @Input('sorts')
  set setSorts(sorts: Sort[]) {
    if (sorts) {
      this.delayIfNeeded(() => {
        this.updateSorts(sorts, null);
      });
      this._sorts = sorts;
    }
  }
  @Input('artifactColumns')
  set setArtifactColumns(artifactColumns: ArtifactColumnPivot[]) {
    this.artifactColumns = fpPipe(
      fpFilter('checked'),
      this.preProcessArtifactColumns(),
      this.artifactColumn2PivotField()
    )(artifactColumns);
    this.setPivotData();
  }
  @Input('data')
  set setData(data: any[]) {
    setTimeout(() => {
      this.data = this.preProcessData(data);
      console.log(this.data);
      this.setPivotData();
    }, 500);
  }
  @Output() onContentReady: EventEmitter<any> = new EventEmitter();
  public fields: any[];
  public data: any[];
  public _sorts: Array<Sort> = [];
  public artifactColumns;
  public pivotGridOptions: any;
  public dataFields: any[];
  public columnFields: any[];
  public rowFields: any[];
  rowHeaderLayout = 'tree';
  height = '100%';
  allowSortingBySummary = false;
  showBorders = true;
  allowSorting = false;
  allowFiltering = false;
  allowExpandAll = true;
  fieldChooser = { enabled: false };
  // field-panel
  visible = true;
  showColumnFields = false;
  showRowFields = false;
  showDataFields = false;
  showFilterFields = false;
  allowFieldDragging = false;
  public _gridInstance: any;
  public _preExportState: any;
  public _subscription: any;

  ngOnInit() {
    setTimeout(() => {
      // have to repaint the grid because of the animation of the modal
      // if it's not repainted it appears smaller
      this._gridInstance.repaint();
      if (this.updater) {
        this._subscription = this.updater.subscribe(updates => {
          this._gridInstance.repaint();
          return this.update(updates);
        });
      }
    }, 500);
  }

  ngOnDestroy() {
    if (this._subscription) {
      this._subscription.unsubscribe();
    }
  }
  // pivot grid events
  onInitialized(e) {
    this._gridInstance = e.component;
  }

  onPivotContentReady() {
    const fields = this._gridInstance.getDataSource().fields();
    this.onContentReady.emit({ fields });
  }

  onExported(e) {
    e.component.getDataSource().state(this._preExportState);
    this._preExportState = null;
  }

  setPivotData() {
    if (isArray(this.data) && isArray(this.artifactColumns)) {
      const dataSource = new PivotGridDataSource({
        store: this.data || [],
        fields: this.artifactColumns || []
      });

      /* Try to apply existing sorts (if any) to the new data source */
      this.updateSorts(this._sorts, dataSource);
      this.updateDataSource(dataSource);
    }
  }

  update(update: IPivotGridUpdate) {
    /* eslint-disable no-unused-expressions */
    update.dataSource && this.updateDataSource(update.dataSource);
    update.sorts && this.updateSorts(update.sorts, null);
    update.export && this.exportToExcel();
    /* eslint-disable no-unused-expressions */
  }

  exportToExcel() {
    const dataSource = this._gridInstance.getDataSource();
    const fields = dataSource.fields();
    this._preExportState = dataSource.state();
    forEach(fields, ({ dataField }) => dataSource.expandAll(dataField));
    this._gridInstance.exportToExcel();
  }

  updateDataSource(dataSource) {
    this.delayIfNeeded(() => {
      this._gridInstance.option('dataSource', dataSource);
    });
  }

  updateSorts(sorts: Sort[], source) {
    if (isEmpty(sorts)) {
      return;
    }

    const dataSource = source || this._gridInstance.getDataSource();
    const load = !source;

    // reset other sorts
    forEach(dataSource.fields(), field => {
      if (field.sortOrder) {
        dataSource.field(field.dataField, {
          sortOrder: null
        });
      }
    });

    forEach(sorts, (sort: Sort) => {
      // remove the suffix from the sort fields name, that is added by elastic search
      // there is a bug that breaks pivotgrid when the name conains a .
      const dataField =
        sort.type === 'string'
          ? sort.columnName.split('.')[0]
          : sort.columnName;
      dataSource.field(dataField, {
        sortOrder: sort.order
      });
    });

    if (load) {
      dataSource.load();
    }
  }

  delayIfNeeded(fn) {
    if (this._gridInstance) {
      fn();
    } else {
      setTimeout(() => fn(), 100);
    }
  }

  preProcessArtifactColumns() {
    return fpMap((column: ArtifactColumnPivot) => {
      // manually format dates for day quarter and month dateIntervals
      if (DATE_TYPES.includes(column.type)) {
        let momentFormat;
        const cloned = clone(column);
        /* prettier-ignore */
        switch (column.dateInterval) {
        case 'day':
          cloned.groupInterval = 1;
          momentFormat = this.getMomentFormat(cloned.format);
          cloned.manualFormat = cloned.format;
          cloned.format = {
            formatter: this.getFormatter(momentFormat)
          };
          break;
        case 'month':
          cloned.groupInterval = 1;
          momentFormat = DATE_INTERVALS_OBJ[cloned.dateInterval].momentFormat;
          cloned.format = {
            formatter: this.getFormatter(momentFormat)
          };
          break;
        case 'quarter':
          unset(cloned, 'format');
          cloned.groupInterval = 1;
          break;
        case 'year':
          cloned.groupInterval = cloned.dateInterval;
          // the format usually sent by the backend: 'YYYY-MM-DD' does not work with the pivot grid
          unset(cloned, 'format');
          break;
        case 'all':
        default:
          // do nothing
          break;
        }
        return cloned;
      }
      return column;
    });
  }

  getFormatter(format) {
    // Pivot grid auto converts given moment to local dates. It's important to
    // re-convert it to the zone we used to provide dates to normalise it.
    return value => moment.utc(value).format(format);
  }

  preProcessData(data) {
    if (isPlainObject(data)) {
      data = [data];
    }
    const processedData = this.formatDates(data, this.artifactColumns);
    return processedData;
  }

  formatDates(data, fields: ArtifactColumnPivot[]) {
    if (isEmpty(this.artifactColumns)) {
      return data;
    }
    const columnsToFormat = filter(this.artifactColumns, ({ type }) =>
      DATE_TYPES.includes(type)
    );
    if (isEmpty(columnsToFormat)) {
      return data;
    }

    const formattedData = map(data, dataPoint => {
      const clonedDataPoint = clone(dataPoint);
      forEach(columnsToFormat, ({ name, dateInterval, manualFormat }) => {
        clonedDataPoint[name] = this.getFormattedDataValue(
          clonedDataPoint[name],
          dateInterval,
          manualFormat
        );
      });
      return clonedDataPoint;
    });
    return formattedData;
  }

  getFormattedDataValue(value, dateInterval, format) {
    let formatToApply;
    /* prettier-ignore */
    switch (dateInterval) {
    case 'day':
      formatToApply = this.getMomentFormat(format);
      return moment.utc(value);
    case 'quarter':
      formatToApply = DATE_INTERVALS_OBJ[dateInterval].momentFormat;
      const formattedValue = moment.utc(value).format(formatToApply);
      const parts = split(formattedValue, '-');
      return `${parts[0]}-Q${parts[1]}`;
    case 'month':
      formatToApply = DATE_INTERVALS_OBJ[dateInterval].momentFormat;
      return moment.utc(value).format(formatToApply);
    case 'year':
    default:
      return value;
    }
  }

  getMomentFormat(format) {
    const formatObj = DATE_FORMATS_OBJ[format];
    return formatObj ? formatObj.momentValue : DEFAULT_DATE_FORMAT.momentValue;
  }

  artifactColumn2PivotField(): any {
    return fpPipe(
      fpMap(artifactColumn => {
        const cloned = clone(artifactColumn);

        if (NUMBER_TYPES.includes(cloned.type)) {
          cloned.dataType = 'number';
          const percent = cloned.aggregate === 'percentage' ? true : false;

          if (!isUndefined(artifactColumn.format)) {
            artifactColumn.format.percentage = percent;
          }

          const conditionalPrecision =
            ['percentage', 'avg'].includes(artifactColumn.aggregate) &&
            !isFinite(get(artifactColumn, 'format.precision'))
              ? DEFAULT_PRECISION
              : 0;

          cloned.format = {
            formatter: getFormatter(
              artifactColumn.format ||
                (FLOAT_TYPES.includes(cloned.type)
                  ? { precision: DEFAULT_PRECISION, percentage: percent }
                  : { precision: conditionalPrecision, percentage: percent })
            )
          };
          /* We're aggregating values in backend. Aggregating it again using
             pivot's aggregate function will lead to bad data. Always keep this
             on sum */
          cloned.aggregate = 'sum';
        } else {
          cloned.dataType = cloned.type;
        }

        if (cloned.type === 'string') {
          // trim the .keyword suffix from the column name if it is there
          cloned.columnName = split(cloned.columnName, '.')[0];
        }

        if (DATE_TYPES.includes(cloned.type)) {
          // disable sorting for the fields that have a type string because of manual formatting
          // so it doesn't sort the fields accordinbg to the display string
          cloned.sortBy = 'value';
        }

        if (!isUndefined(cloned.aliasName) && cloned.aliasName !== '') {
          cloned.displayName = cloned.aliasName;
        }

        return cloned;
      }),
      fpMap(
        fpMapKeys(key => {
          const newKey = ARTIFACT_COLUMN_2_PIVOT_FIELD[key];
          return newKey || key;
        })
      )
    );
  }
}
