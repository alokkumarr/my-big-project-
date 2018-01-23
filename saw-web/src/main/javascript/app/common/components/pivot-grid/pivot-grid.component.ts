import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as forEach from 'lodash/forEach';
import * as isArray from 'lodash/isArray';
import * as get from 'lodash/get';
import * as isEmpty from 'lodash/isEmpty';
import {Subject} from 'rxjs/Subject';
import {Sort} from '../../../modules/analyze/models/sort.model'
import PivotGridDataSource from 'devextreme/ui/pivot_grid/data_source';

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
  @Input() fields: any[];
  @Input() data: any[];
  @Input() sorts: Sort[];
  @Output() onContentReady: EventEmitter<any> = new EventEmitter();
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

  ngOnChanges(changes) {
    const data = get(changes, 'data.currentValue');
    const fields = get(changes, 'fields.currentValue');
    const sorts = get(changes, 'sorts.currentValue');
    if (isArray(data) || isArray(fields)) {
      const dataSource = new PivotGridDataSource({
        store: data || [],
        fields: fields || []
      });
      this.updateDataSource(dataSource);
    }

    if (sorts) {
      this.delayIfNeeded(() => {
        this.updateSorts(sorts);
      });
    }
  }

  update(update: IPivotGridUpdate) {
    /* eslint-disable no-unused-expressions */
    update.dataSource && this.updateDataSource(update.dataSource);
    update.sorts && this.updateSorts(update.sorts);
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

  updateSorts(sorts: Sort[]) {
    if (isEmpty(sorts)) {
      return;
    }
    const pivotGridDataSource = this._gridInstance.getDataSource();

    // reset other sorts
    forEach(pivotGridDataSource.fields(), field => {
      if (field.sortOrder) {
        pivotGridDataSource.field(field.dataField, {
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
      pivotGridDataSource.field(dataField, {
        sortOrder: sort.order
      });
    });
    pivotGridDataSource.load();
  }

  delayIfNeeded(fn) {
    if (this._gridInstance) {
      fn();
    } else {
      setTimeout(() => fn(), 100);
    }
  }
}
