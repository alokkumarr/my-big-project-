declare function require(string): string;

import { Component, Input, OnInit, Inject, ViewChild, AfterViewInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs/Subject';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import { DxDataGridComponent } from 'devextreme-angular';

import { HeaderProgressService } from '../../../../../common/services/header-progress.service';
import { dxDataGridService } from '../../../../../common/services/dxDataGrid.service';

const template = require('./sqlpreview-grid-page.component.html');
require('./sqlpreview-grid-page.component.scss');

@Component({
  selector: 'sqlpreview-grid-page',
  template,
  styles: []
})

export class SqlpreviewGridPageComponent implements OnInit, AfterViewInit, OnDestroy {
  @Input() updater: BehaviorSubject<any>;

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
    return this.dxDataGrid.mergeWithDefaultConfig({
      dataSource,
      columnAutoWidth: false,
      wordWrapEnabled: false,
      searchPanel: {
        visible: true,
        width: 240,
        placeholder: 'Search...'
      },
      height: '100%',
      width: '100%',
      filterRow: {
        visible: true,
        applyFilter: 'auto'
      },
      headerFilter: {
        visible: true
      },
      sorting: {
        mode: 'none'
      },
      export: {
        fileName: 'Parsed_Sample'
      },
      scrolling: {
        showScrollbar: 'always',
        mode: 'virtual',
        useNative: false
      },
      showRowLines: false,
      showBorders: false,
      rowAlternationEnabled: true,
      showColumnLines: false,
      selection: {
        mode: 'none'
      }
    });
  }

  reloadDataGrid(data) {
    this.dataGrid.instance.option('dataSource', data);
    this.dataGrid.instance.refresh();
    this.dataGrid.instance.endCustomLoading();
  }
}
