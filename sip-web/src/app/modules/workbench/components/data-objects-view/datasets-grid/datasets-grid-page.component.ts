import {
  Component,
  Input,
  OnInit,
  AfterViewInit,
  OnDestroy,
  ViewChild
} from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { DxDataGridComponent } from 'devextreme-angular/ui/data-grid';

import { DxDataGridService } from '../../../../../common/services/dxDataGrid.service';
import { WorkbenchService } from '../../../services/workbench.service';

@Component({
  selector: 'datasets-grid-page',
  templateUrl: './datasets-grid-page.component.html',
  styleUrls: ['./datasets-grid-page.component.scss']
})
export class DatasetsGridPageComponent
  implements OnInit, AfterViewInit, OnDestroy {
  @Input() searchTerm: string;
  @Input() updater: BehaviorSubject<any>;
  public gridConfig: Array<any>;
  public updaterSubscribtion: any;

  constructor(
    public dxDataGrid: DxDataGridService,
    public workbench: WorkbenchService
  ) {}

  @ViewChild(DxDataGridComponent) dataGrid: DxDataGridComponent;

  ngOnInit() {
    this.gridConfig = this.getGridConfig();
    this.updaterSubscribtion = this.updater.subscribe(data => {
      this.onUpdate(data);
    });
  }

  ngAfterViewInit() {
    this.dataGrid.instance.option(this.gridConfig);
  }

  ngOnDestroy() {
    this.updaterSubscribtion.unsubscribe();
  }

  onUpdate(data) {
    setTimeout(() => {
      this.reloadDataGrid(data);
    });
  }

  getGridConfig() {
    const dataSource = [];
    const columns = [
      {
        caption: 'Data Set Name',
        dataField: 'system.name',
        alignment: 'left',
        width: '20%',
        cellTemplate: 'nameCellTemplate',
        cssClass: 'branded-column-name'
      },
      {
        dataField: 'system.description',
        caption: 'Description',
        width: '25%',
        dataType: 'String',
        cellTemplate: 'creatorCellTemplate'
      },
      {
        caption: 'Size',
        dataField: 'system.numberOfFiles',
        dataType: 'number',
        width: '10%'
      },
      {
        dataField: 'system.createdBy',
        caption: 'Added By',
        width: '13%',
        dataType: 'string',
        cellTemplate: 'creatorCellTemplate'
      },
      {
        dataField: 'dataPods.numberOfPods',
        caption: 'Data Pods',
        width: '8%',
        dataType: 'number'
      },
      {
        dataField: 'system.modifiedTime',
        caption: 'Last Updated',
        cellTemplate: 'timecreatedCellTemplate',
        width: '12%',
        dataType: 'date',
        alignment: 'right'
      },
      {
        dataField: 'asOfNow.status',
        caption: 'Status',
        width: '7%',
        alignment: 'center'
      },
      {
        dataField: '_id',
        caption: 'Actions',
        cellTemplate: 'actionsCellTemplate',
        width: '5%'
      }
    ];

    return this.dxDataGrid.mergeWithDefaultConfig({
      columns,
      dataSource,
      height: '100%',
      width: '100%',
      headerFilter: {
        visible: false
      },
      sorting: {
        mode: 'multiple'
      },
      scrolling: {
        showScrollbar: 'always',
        mode: 'virtual',
        useNative: false
      },
      filterRow: {
        visible: false,
        applyFilter: 'auto'
      },
      showRowLines: false,
      showBorders: false,
      rowAlternationEnabled: true,
      showColumnLines: false,
      hoverStateEnabled: true
    });
  }

  reloadDataGrid(data) {
    this.dataGrid.instance.option('dataSource', data);
    this.dataGrid.instance.refresh();
  }

  viewDetails(metadata) {
    this.workbench.navigateToDetails(metadata);
  }

  sizeCalculator(size) {
    let a = size;


    return a + " B";
  }
}
