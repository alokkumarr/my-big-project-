import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { DxDataGridService } from '../../../../common/services/dxDataGrid.service';
import { Observable } from 'rxjs/Observable';
import { ExportItemChangeOutput } from '../content/content.component';

@Component({
  selector: 'admin-export-list',
  templateUrl: 'admin-export-list.component.html',
  styleUrls: ['admin-export-list.component.scss']
})
export class AdminExportListComponent implements OnInit {
  @Input() exportList: Observable<any[]>;
  @Output() change: EventEmitter<ExportItemChangeOutput> = new EventEmitter();
  @Output() changeAll: EventEmitter<boolean> = new EventEmitter();

  config: any;

  areAllSelected = false;

  constructor(private dxDataGridService: DxDataGridService) {}

  ngOnInit() {
    this.config = this.getConfig();
  }

  onItemToggle({ checked }, item) {
    this.change.emit({ checked, item });
  }

  /**
   * Handle toggling the 'all' checkbox at top of the list
   *
   * @param {*} { checked }
   * @memberof AdminExportListComponent
   */
  onToggleAll({ checked }: any) {
    this.changeAll.emit(checked);
  }

  getConfig() {
    const columns = [
      {
        caption: '',
        allowSorting: false,
        alignment: 'center',
        headerCellTemplate: 'selectionHeaderCellTemplate',
        cellTemplate: 'selectionCellTemplate',
        width: '10%'
      },
      {
        caption: 'Name',
        dataField: 'name',
        allowSorting: true,
        alignment: 'left',
        width: '40%'
      },
      {
        caption: 'Type',
        dataField: 'type',
        allowSorting: true,
        alignment: 'left',
        width: '20%'
      },
      {
        caption: 'Metric Name',
        dataField: 'metricName',
        allowSorting: true,
        alignment: 'left',
        width: '30%'
      }
    ];
    return this.dxDataGridService.mergeWithDefaultConfig({
      columns,
      noDataText: 'No data to export. Select items from left to add them here.',
      width: '100%',
      height: '100%',
      paging: {
        pageSize: 10
      },
      pager: {
        showPageSizeSelector: true,
        showInfo: true
      }
    });
  }
}
