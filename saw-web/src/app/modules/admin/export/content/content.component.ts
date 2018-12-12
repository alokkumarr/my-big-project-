import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Dashboard } from '../../../observe/models/dashboard.interface';
import { Analysis } from '../../../analyze/models';
import { DxDataGridService } from '../../../../common/services/dxDataGrid.service';
import { map } from 'rxjs/operators';

export interface ExportItemChangeOutput {
  checked: boolean;
  item: Analysis | Dashboard;
}

@Component({
  selector: 'admin-export-content',
  templateUrl: './content.component.html',
  styleUrls: ['./content.component.scss']
})
export class AdminExportContentComponent implements OnInit {
  @Input() exportList: Observable<any[]>;
  @Input() analyses: Observable<any[]>;
  // @Input() dashboards: Observable<any[]>;
  @Output() change: EventEmitter<ExportItemChangeOutput> = new EventEmitter();

  config: any;

  constructor(private dxDataGridService: DxDataGridService) {}

  ngOnInit() {
    this.config = this.getConfig();
  }

  onItemToggled({ checked }, item) {
    this.change.emit({ checked, item });
  }

  isSelectedForExport$(item): Observable<boolean> {
    return this.exportList.pipe(
      map(list =>
        list.some(a => {
          return item.entityId
            ? a.entityId === item.entityId
            : a.id === item.id;
        })
      )
    );
  }

  getConfig() {
    const columns = [
      {
        caption: '',
        allowSorting: false,
        alignment: 'center',
        cellTemplate: 'selectionCellTemplate',
        width: '10%'
      },
      {
        caption: 'Name',
        dataField: 'name',
        allowSorting: true,
        alignment: 'left',
        width: '50%'
      },
      {
        caption: 'Type',
        dataField: 'type',
        allowSorting: true,
        alignment: 'left',
        width: '40%'
      }
    ];

    return this.dxDataGridService.mergeWithDefaultConfig({
      columns,
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
