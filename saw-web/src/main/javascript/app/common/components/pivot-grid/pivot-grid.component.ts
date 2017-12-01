import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as forEach from 'lodash/forEach';
import {Subject} from 'rxjs/Subject';

require('./pivot-grid.component.scss');
const template = require('./pivot-grid.component.html');

@Component({
  selector: 'pivot-grid',
  template
})
export class PivotGridComponent {
  @Input() updater: Subject<any>;
  @Input() mode: string;
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
    this.allowFieldDragging = this.mode === 'designer';
    setTimeout(() => {
      // have to repaint the grid because of the animation of the modal
      // if it's not repainted it appears smaller
      this._gridInstance.repaint();
      this._subscription = this.updater.subscribe(updates => this.update(updates));
    }, 500);
  }

  ngOnDestroy() {
    this._subscription.unsubscribe();
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

  update(updates) {
    /* eslint-disable no-unused-expressions */
    updates.dataSource && this.updateDataSource(updates.dataSource);
    updates.sorts && this.updateSorts(updates.sorts);
    updates.export && this.exportToExcel();
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
    this._gridInstance.option('dataSource', dataSource);
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
