import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as forEach from 'lodash/forEach';
import * as isArray from 'lodash/isArray';
import * as get from 'lodash/get';
import {Subject} from 'rxjs/Subject';
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
    const isNotInDesignerMode = this.mode !== 'designer';
    this.showColumnFields = isNotInDesignerMode;
    this.showRowFields = isNotInDesignerMode;
    this.showDataFields = isNotInDesignerMode;
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
    if (isArray(data) || isArray(fields)) {
      const dataSource = new PivotGridDataSource({
        store: data || [],
        fields: fields || []
      });
      this.updateDataSource(dataSource);
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
    if (this._gridInstance) {
      this._gridInstance.option('dataSource', dataSource);
    } else {
      setTimeout(() => {
        this._gridInstance.option('dataSource', dataSource);
      }, 100);
    }
  }

  updateSorts(sorts) {
    const pivotGridDataSource = this._gridInstance.getDataSource();

    // reset other sorts
    forEach(pivotGridDataSource.fields(), field => {
      if (field.sortOrder) {
        pivotGridDataSource.field(field.dataField, {
          sortOrder: null
        });
      }
    });

    forEach(sorts, sort => {
      // remove the suffix from the sort fields name, that is added by elastic search
      // there is a bug that breaks pivotgrid when the name conains a .
      const dataField = sort.field.type === 'string' ?
        sort.field.dataField.split('.')[0] :
        sort.field.dataField;
      pivotGridDataSource.field(dataField, {
        sortOrder: sort.order
      });
    });
    pivotGridDataSource.load();
  }
}
