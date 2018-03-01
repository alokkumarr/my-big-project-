declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as isArray from 'lodash/isArray';
import * as get from 'lodash/get';
import * as unset from 'lodash/unset';
import * as map from 'lodash/map';
import * as isEmpty from 'lodash/isEmpty';
import * as forEach from 'lodash/forEach';
import * as clone from 'lodash/clone';
import * as split from 'lodash/split';
import * as isPlainObject from 'lodash/isPlainObject';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpPick from 'lodash/fp/pick';
import * as fpMap from 'lodash/fp/map';
import * as fpFilter from 'lodash/fp/filter';
import * as filter from 'lodash/filter';
import * as fpForEach from 'lodash/fp/forEach';
import * as fpMapKeys from 'lodash/fp/mapKeys';
import * as moment from 'moment';
import * as isUndefined from 'lodash/isUndefined';
import {Subject} from 'rxjs/Subject';
import { DEFAULT_PRECISION } from '../data-format-dialog/data-format-dialog.component';
import PivotGridDataSource from 'devextreme/ui/pivot_grid/data_source';
import {
  ArtifactColumnPivot,
  Sort,
  Format
} from '../../../modules/analyze/models';
import {
  DATE_TYPES,
  NUMBER_TYPES,
  FLOAT_TYPES,
  DATE_INTERVALS_OBJ,
  DATE_FORMATS_OBJ
} from '../../../modules/analyze/consts';
import { getFormatter } from '../../utils/numberFormatter';

const ARTIFACT_COLUMN_2_PIVOT_FIELD = {
  displayName: 'caption',
  columnName: 'dataField',
  aggregate: 'summaryType'
};

require('./pivot-grid.component.scss');
const template = require('./pivot-grid.component.html');

export interface IPivotGridUpdate {
  dataSource?: any;
  fields?: any;
  data?: any;
  sorts?: any;
  export?: boolean;
}

@Component({
  selector: 'pivot-grid',
  template
})
export class PivotGridComponent {
  @Input() updater: Subject<IPivotGridUpdate>;
  @Input() mode: string | 'designer';
  @Input('sorts') set setSorts(sorts: Sort[]) {
    if (sorts) {
      this.delayIfNeeded(() => {
        this.updateSorts(sorts, null);
      });
      this._sorts = sorts;
    }
  };
  @Input('artifactColumns') set setArtifactColumns(artifactColumns: ArtifactColumnPivot[]) {
    this.artifactColumns = fpPipe(
      fpFilter('checked'),
      this.preProcessArtifactColumns(),
      this.artifactColumn2PivotField()
    )(artifactColumns);
    this.setPivotData();
  };
  @Input('data') set setData(data: any[]) {
    this.data = this.preProcessData(data);
    this.setPivotData();
  };
  @Output() onContentReady: EventEmitter<any> = new EventEmitter();
  public fields: any[];
  public data: any[];
  public _sorts: Array<Sort> = [];
  public artifactColumns: ArtifactColumnPivot[];
  public pivotGridOptions : any;
  rowHeaderLayout = 'tree';
  allowSortingBySummary = false
  showBorders = true;
  allowSorting = false;
  allowFiltering = false;
  allowExpandAll = true;
  fieldChooser = {enabled: false};
  // field-panel
  visible = true;
  showColumnFields = true;
  showRowFields = true;
  showDataFields = true;
  showFilterFields = false;
  allowFieldDragging = false;
  private _gridInstance: any;
  private _preExportState: any;
  private _subscription: any;

  ngOnInit() {
    setTimeout(() => {
      // have to repaint the grid because of the animation of the modal
      // if it's not repainted it appears smaller
      this._gridInstance.repaint();
      if (this.updater) {
        this._subscription = this.updater.subscribe(updates => this.update(updates));
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
    this.onContentReady.emit({fields});
  }

  onExported(e){
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
    forEach(fields, ({dataField}) => dataSource.expandAll(dataField));
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

    let dataSource = source || this._gridInstance.getDataSource();
    let load = !source;

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
      const dataField = sort.type === 'string' ?
        sort.columnName.split('.')[0] :
        sort.columnName;
      dataSource.field(dataField, {
        sortOrder: sort.order
      });
    });

    load && dataSource.load();
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
        const cloned = clone(column);
        switch (column.dateInterval) {
        case 'day':
        case 'quarter':
        case 'month':
          cloned.type = 'string';
          cloned.checktype = 'date';
          break;
        case 'year':
          cloned.groupInterval = cloned.dateInterval;
          // the format usually sent by the backend: 'YYYY-MM-DD' does not work with the pivot grid
          unset(cloned, 'format');
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
    const columnsToFormat = filter(this.artifactColumns, ({checktype}) => DATE_TYPES.includes(checktype));
    if (isEmpty(columnsToFormat)) {
      return data;
    }

    const formattedData = map(data, dataPoint => {
      const clonedDataPoint = clone(dataPoint);
      const dataValue = dataPoint[name];
      forEach(columnsToFormat, ({name, dateInterval, format}) => {
        clonedDataPoint[name] = this.getFormattedDataValue(clonedDataPoint[name], dateInterval, format);
      });
      return clonedDataPoint;
    });
    return formattedData;
  }

  getFormattedDataValue(value, dateInterval, format) {
    const formatToApply = dateInterval === 'day' ?
      DATE_FORMATS_OBJ[format].momentValue :
      DATE_INTERVALS_OBJ[dateInterval].format;
    const formattedValue = moment.utc(value).format(formatToApply);
    if (dateInterval === 'quarter') {
      const parts = split(formattedValue, '-');
      return `${parts[0]}-Q${parts[1]}`;
    }
    return formattedValue;
  }

  artifactColumn2PivotField(): any {
    return fpPipe(
      fpMap((artifactColumn) => {
        const cloned = clone(artifactColumn);

        if (NUMBER_TYPES.includes(cloned.type)) {
          cloned.dataType = 'number';
          cloned.format = {
            formatter: getFormatter(artifactColumn.format || (
              FLOAT_TYPES.includes(cloned.type) ? {precision: DEFAULT_PRECISION} : {precision: 0}
            ))
          };
          /* We're aggregating values in backend. Aggregating it again using
             pivot's aggregate function will lead to bad data. Always keep this
             on sum */
          cloned.aggregate = 'sum';
        } else {
          cloned.dataType = cloned.type;
        }

        if (cloned.type === 'string') {
          cloned.columnName = split(cloned.columnName, '.')[0];
        }

        if (!isUndefined(cloned.aliasName) && cloned.aliasName != '') {
          cloned.displayName = cloned.aliasName;
        }

        return cloned;
      }),
      fpMap(fpMapKeys(key => {
        const newKey = ARTIFACT_COLUMN_2_PIVOT_FIELD[key];
        return newKey || key;
      }))
    );
  }
}
