import {
  Component,
  OnInit
} from '@angular/core';
import { DxDataGridService } from './../../../../../common/services/dxDataGrid.service';
import { Router } from '@angular/router';
// import { DxDataGridComponent } from 'devextreme-angular/ui/data-grid';

@Component({
  selector: 'appkeys-view',
  templateUrl: './appkeys-view.component.html',
  styleUrls: ['./appkeys-view.component.scss']
})
export class AppkeysViewComponent implements OnInit {
  public config: any;
  constructor(
    public _DxDataGridService: DxDataGridService,
    private router: Router
  ) {}

  ngOnInit() {
    this.config = this.getGridConfig();
  }

  navigate() {
    this.router.navigate(['workbench', 'rtis', 'registration']);
  }

  getGridConfig() {
    const columns = [
      {
        caption: 'NAME',
        dataField: 'name',
        width: '20%'
      },
      {
        caption: 'METRICS',
        dataField: 'metrics',
        width: '20%'
      },
      {
        caption: 'SCHEDULED',
        width: '12%'
      },
      {
        caption: 'TYPE',
        dataField: 'type',
        width: '8%'
      },
      {
        caption: 'LAST MODIFIED BY',
        width: '20%'
      },
      {
        caption: 'LAST MODIFIED ON',
        width: '10%'
      },
      {
        caption: '',
        cellTemplate: 'actionCellTemplate'
      }
    ];
    return this._DxDataGridService.mergeWithDefaultConfig({
      columns,
      paging: {
        pageSize: 10
      },
      pager: {
        showPageSizeSelector: true,
        showInfo: true
      },
      width: '100%',
      height: '100%'
    });
  }
}
