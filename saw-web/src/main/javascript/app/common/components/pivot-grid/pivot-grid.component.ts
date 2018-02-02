import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as isArray from 'lodash/isArray';
import * as get from 'lodash/get';
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
import * as fpForEach from 'lodash/fp/forEach';
import * as fpMapKeys from 'lodash/fp/mapKeys';
import * as moment from 'moment';
import {Subject} from 'rxjs/Subject';
import {Sort} from '../../../modules/analyze/models/sort.model'
import PivotGridDataSource from 'devextreme/ui/pivot_grid/data_source';
import {
  ArtifactColumnPivot
} from '../../../modules/analyze/models/artifact-column.model';
import {
  DATE_TYPES,
  NUMBER_TYPES,
  DATE_INTERVALS_OBJ
} from '../../../modules/analyze/consts';

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
  @Input('data') set setData(data: any[]) {
    this.data = this.preProcessData(data);
    this.setPivotData();
  };
  @Input('artifactColumns') set setArtifactColumns(artifactColumns: ArtifactColumnPivot[]) {
    this.artifactColumns = fpPipe(
      fpFilter('checked'),
      this.preProcessArtifactColumns(),
      this.artifactColumn2PivotField()
    )(artifactColumns);
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
        if (['day', 'quarter', 'month'].includes(column.dateInterval)) {
          cloned.type = 'string';
        } else {
          cloned.groupInterval = cloned.dateInterval;
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

    const formattedData = map(data, dataPoint => {

      const clonedDataPoint = clone(dataPoint);
      fpPipe(
        fpFilter(({type}) => DATE_TYPES.includes(type)),
        fpForEach(({columnName, dateInterval}) => {
          const format = DATE_INTERVALS_OBJ[dateInterval].format;
          clonedDataPoint[columnName] = moment.utc(dataPoint[columnName]).format(format);
          if (dateInterval === 'quarter') {
            const parts = split(clonedDataPoint[columnName], '-');
            clonedDataPoint[columnName] = `${parts[0]}-Q${parts[1]}`;
          }
        })
      )(this.artifactColumns);
      return clonedDataPoint;
    });
    return formattedData;
  }

  artifactColumn2PivotField(): any {
    return fpPipe(
      fpMap((artifactColumn) => {
        const cloned = clone(artifactColumn);

        if (NUMBER_TYPES.includes(cloned.type)) {
          cloned.dataType = 'number';
          cloned.format = {
            type: 'fixedPoint',
            precision: 2
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

        return cloned;
      }),
      fpMap(fpMapKeys(key => {
        const newKey = ARTIFACT_COLUMN_2_PIVOT_FIELD[key];
        return newKey || key;
      }))
    );
  }
}
