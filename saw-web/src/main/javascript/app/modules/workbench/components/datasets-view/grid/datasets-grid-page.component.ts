declare function require(string): string;

import { Component, Input, OnInit, Inject, ViewChild, AfterViewInit } from '@angular/core';
import { Subject } from 'rxjs/Subject';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import { MatDialog } from '@angular/material';
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
  private gridListInstance: any;
  private gridConfig: Array<any>;
  private gridData: Array<any>;
  private updaterSubscribtion: any;
  
  constructor(
    private dxDataGrid: dxDataGridService,
    private headerProgress: HeaderProgressService
  ) {  }

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
      dataField: 'set',
      allowSorting: true,
      alignment: 'left',
      width: '40%',
      cellTemplate: 'nameCellTemplate',
      cssClass: 'branded-column-name'
    }, {
      caption: 'Size',
      dataField: 'meta.size',
      dataType: 'number',
      width: '8%'
    }, {
      dataField: 'meta.addedBy',
      caption: 'Added By',
      width: '10%',
      dataType: 'string',
      cellTemplate: 'creatorCellTemplate'
    }, {
      dataField: 'meta.updatedBy',
      caption: 'Updated By',
      width: '10%',
      dataType: 'String',
      cellTemplate: 'creatorCellTemplate'
    }, {
      dataField: 'meta.numFiles',
      caption: 'Data Pods',
      width: '5%',
      dataType: 'number'
    }, {
      dataField: 'meta.lastUpdated',
      caption: 'Last Updated',
      sortOrder: 'desc',
      cellTemplate: 'timecreatedCellTemplate',
      width: '15%',
      dataType: 'date',
      alignment: 'right'
    }, {
      dataField: 'src',
      caption: 'Data Source',
      cellTemplate: 'dsTypeTemplate',
      width: '7%'
    }, {
      dataField: 'src',
      caption: 'Actions',
      cellTemplate: 'actionsCellTemplate',
      width: '5%'
    }];

    return this.dxDataGrid.mergeWithDefaultConfig ({
      columns,
      dataSource,
      scrolling: {
        mode: 'standard'
      },
      paging: {
        pageSize: 25
      },
      pager: {
        showPageSizeSelector: true,
        allowedPageSizes: [10, 20, 50, 100],
        showInfo: true
      },
      bindingOptions: {
        showRowLines: 'false',
        showBorders: 'false',
        rowAlternationEnabled: 'true',
        showColumnLines: 'true'
      },
      selection: {
        mode: 'single'
      },
      hoverStateEnabled: true,
      // onInitialized: this.onGridInitialized.bind(this),
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
