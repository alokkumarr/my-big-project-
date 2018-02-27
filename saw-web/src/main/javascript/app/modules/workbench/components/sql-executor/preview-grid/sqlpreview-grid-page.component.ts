declare function require(string): string;

import { Component, Input, OnInit, ViewChild, AfterViewInit, OnDestroy, EventEmitter, Output } from '@angular/core';
import { Subject } from 'rxjs/Subject';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import { DxDataGridModule, DxDataGridComponent, DxTemplateModule } from 'devextreme-angular';
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
  private fullScreen: boolean = false;
  
  constructor(
    private dxDataGrid: dxDataGridService,
    private headerProgress: HeaderProgressService
  ) {  }

  @ViewChild(DxDataGridComponent) dataGrid: DxDataGridComponent;
  @Output() onToggleScreenMode: EventEmitter<any> = new EventEmitter<any>();

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
        fileName: 'Preview_Sample',
        enabled: false
      },
      scrolling: {
        showScrollbar: 'always',
        mode: 'virtual',
        useNative: false
      },
      showRowLines: false,
      showBorders: false,
      rowAlternationEnabled: false,
      showColumnLines: true,
      selection: {
        mode: 'none'
      },
      onToolbarPreparing: e => {
        e.toolbarOptions.items.unshift({
          location: 'before',
          template: 'toggleViewTemplate'
        })
      }
    });
  }

  reloadDataGrid(data) {
    this.dataGrid.instance.option('dataSource', data);
    this.dataGrid.instance.refresh();
    this.dataGrid.instance.endCustomLoading();
  }

  togglePreview(fullScrMode: boolean) {
    this.fullScreen = fullScrMode;
    this.onToggleScreenMode.emit(fullScrMode);
    setTimeout(() => {
      this.dataGrid.instance.refresh();
    }, 100);
  }
}
