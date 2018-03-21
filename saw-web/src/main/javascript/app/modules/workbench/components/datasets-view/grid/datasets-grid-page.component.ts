declare function require(string): string;

import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import { DxDataGridComponent } from 'devextreme-angular';

import { HeaderProgressService } from '../../../../../common/services/header-progress.service';
import { dxDataGridService } from '../../../../../common/services/dxDataGrid.service';

const template = require('./datasets-grid-page.component.html');
require('./datasets-grid-page.component.scss');

@Component({
  selector: 'datasets-grid-page',
  template,
  styles: []
})

export class DatasetsGridPageComponent implements OnInit {
  @Input() searchTerm: string;
  @Input() updater: BehaviorSubject<any>;
  private gridConfig: Array<any>;
  private updaterSubscribtion: any;

  constructor(
    private dxDataGrid: dxDataGridService,
    private headerProgress: HeaderProgressService
  ) { }

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
    const columns = [{
      caption: 'Data Set Name',
      dataField: 'system.name',
      allowSorting: true,
      alignment: 'left',
      width: '35%',
      cellTemplate: 'nameCellTemplate',
      cssClass: 'branded-column-name'
    }, {
      caption: 'Size',
      dataField: 'system.numberOfFiles',
      dataType: 'number',
      width: '10%'
    }, {
      dataField: 'system.user',
      caption: 'Added By',
      width: '13%',
      dataType: 'string',
      cellTemplate: 'creatorCellTemplate'
    }, {
      dataField: 'system.format',
      caption: 'Type',
      width: '10%',
      dataType: 'String',
      cellTemplate: 'creatorCellTemplate'
    }, {
      dataField: 'dataPods.numberOfPods',
      caption: 'Data Pods',
      width: '8%',
      dataType: 'number'
    }, {
      dataField: 'asOfNow.finished',
      caption: 'Last Updated',
      sortOrder: 'desc',
      cellTemplate: 'timecreatedCellTemplate',
      width: '12%',
      dataType: 'date',
      alignment: 'right'
    }, {
      dataField: 'system.type',
      caption: 'Source',
      cellTemplate: 'dsTypeTemplate',
      width: '7%',
      alignment: 'center'
    }, {
      dataField: 'sets._id',
      caption: 'Actions',
      cellTemplate: 'actionsCellTemplate',
      width: '5%'
    }];

    return this.dxDataGrid.mergeWithDefaultConfig({
      columns,
      dataSource,
      height: '100%',
      width: '100%',
      headerFilter: {
        visible: false
      },
      sorting: {
        mode: 'none'
      },
      scrolling: {
        showScrollbar: 'always',
        mode: 'virtual',
        useNative: false
      },
      showRowLines: false,
      showBorders: false,
      rowAlternationEnabled: false,
      showColumnLines: false,
      selection: {
        mode: 'single'
      },
      hoverStateEnabled: true,
      onSelectionChanged: selectedItems => {
      }
    });
  }

  reloadDataGrid(data) {
    this.dataGrid.instance.option('dataSource', data);
    this.dataGrid.instance.refresh();
    this.headerProgress.hide();
  }
}
