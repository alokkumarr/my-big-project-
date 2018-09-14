import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { DxDataGridComponent } from 'devextreme-angular';

import { dxDataGridService } from '../../../../../common/services/dxDataGrid.service';
import { WorkbenchService } from '../../../services/workbench.service';

const template = require('./datasets-grid-page.component.html');
require('./datasets-grid-page.component.scss');

@Component({
  selector: 'datasets-grid-page',
  template,
  styles: []
})
export class DatasetsGridPageComponent implements OnInit {
  @Input()
  searchTerm: string;
  @Input()
  updater: BehaviorSubject<any>;
  private gridConfig: Array<any>;
  private updaterSubscribtion: any;

  constructor(
    private dxDataGrid: dxDataGridService,
    private workbench: WorkbenchService
  ) {}

  @ViewChild(DxDataGridComponent)
  dataGrid: DxDataGridComponent;

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
}
