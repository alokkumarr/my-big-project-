import {
  Component,
  OnInit,
  Input,
  Output,
  EventEmitter,
  OnChanges
} from '@angular/core';
import * as forEach from 'lodash/forEach';
import * as some from 'lodash/some';
import * as every from 'lodash/every';
import * as isEmpty from 'lodash/isEmpty';
import { DxDataGridService } from '../../../../common/services/dxDataGrid.service';

@Component({
  selector: 'admin-import-list',
  templateUrl: 'admin-import-list.component.html'
})
export class AdminImportListComponent implements OnInit, OnChanges {
  @Input() analyses: any[];
  @Input() categories: any[];
  @Output()
  validityChange: EventEmitter<boolean> = new EventEmitter();

  @Output()
  categorySelected: EventEmitter<any> = new EventEmitter();

  config: any;
  areAllSelected = false;
  isEmpty = isEmpty;

  constructor(private _DxDataGridService: DxDataGridService) {}

  ngOnInit() {
    this.config = this.getConfig();
  }

  ngOnChanges() {
    this.areAllSelected = false;
  }

  overWrite(row) {
    row.selection = true;
    this.validityChange.emit(true);
  }

  getCategoryId(analysis: any) {
    return analysis.hasOwnProperty('categoryId')
      ? analysis.categoryId
      : analysis.category;
  }

  onChecked(row) {
    row.selection = !row.selection;
    const isValid = some(
      this.analyses,
      // tslint:disable-next-line:no-shadowed-variable
      row => !row.noMetricInd && !row.legacyInd && row.selection
    );
    if (row.selection) {
      this.areAllSelected = every(
        this.analyses,
        // tslint:disable-next-line:no-shadowed-variable
        row => !row.noMetricInd && !row.legacyInd && row.selection
      );
    } else {
      this.areAllSelected = false;
    }
    this.validityChange.emit(isValid);
  }

  selectAll() {
    this.areAllSelected = !this.areAllSelected;
    forEach(this.analyses, row => {
      if (!row.noMetricInd && !row.legacyInd) {
        row.selection = this.areAllSelected;
      }
    });
    this.validityChange.emit(this.areAllSelected);
  }

  onSelectCategory(event, { analysis }) {
    this.categorySelected.emit({
      categoryId: event,
      analysisId: analysis.id
    });
  }

  getConfig() {
    const columns = [
      {
        caption: 'All',
        dataField: 'selection',
        allowSorting: false,
        alignment: 'left',
        width: '10%',
        headerCellTemplate: 'selectionHeaderCellTemplate',
        cellTemplate: 'selectionCellTemplate'
      },
      {
        caption: 'Analysis Name',
        dataField: 'analysis.name',
        allowSorting: true,
        alignment: 'left',
        width: '25%'
      },
      {
        caption: 'Analysis Type',
        dataField: 'analysis.type',
        allowSorting: true,
        alignment: 'left',
        width: '10%'
      },
      {
        caption: 'Metric Name',
        dataField: 'analysis.metricName',
        allowSorting: true,
        alignment: 'left',
        width: '20%'
      },
      {
        caption: 'Category',
        allowSorting: false,
        cellTemplate: 'categoryCellTemplate',
        alignment: 'left',
        width: '15%'
      },
      {
        caption: 'Logs',
        dataField: 'log',
        allowSorting: false,
        alignment: 'left',
        width: '20%',
        cellTemplate: 'logCellTemplate'
      }
    ];
    return this._DxDataGridService.mergeWithDefaultConfig({
      columns,
      scrolling: {
        mode: 'standard'
      },
      paging: {
        enabled: true,
        pageSize: 10,
        pageIndex: 0
      }
    });
  }
}
