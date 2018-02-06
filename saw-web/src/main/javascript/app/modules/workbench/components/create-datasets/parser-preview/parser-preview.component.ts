import * as angular from 'angular';
import { Component, Input, OnInit, Inject, ViewChild, AfterViewInit, EventEmitter, Output } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import * as filter from 'lodash/filter';
import * as set from 'lodash/set';
import * as get from 'lodash/get';
import * as map from 'lodash/map';
import * as indexOf from 'lodash/indexOf';
import * as forEach from 'lodash/forEach';
import * as replace from 'lodash/replace';
import * as cloneDeep from 'lodash/cloneDeep';
import * as assign from 'lodash/assign';

import { MatDialog } from '@angular/material';
import { DxDataGridComponent } from 'devextreme-angular';

import { dxDataGridService } from '../../../../../common/services/dxDataGrid.service';
import { DateformatDialogComponent } from '../dateformat-dialog/dateformat-dialog.component'

const template = require('./parser-preview.component.html');
require('./parser-preview.component.scss');

@Component({
  selector: 'parser-preview',
  template,
  styles: []
})

export class ParserPreviewComponent implements OnInit {
  @Input() previewObj: BehaviorSubject<any>;
  private gridListInstance: any;
  private previewgridConfig: Array<any>;
  private gridData: Array<any>;
  private updaterSubscribtion: any;
  private toAddSubscribtion: any;
  private fieldInfo = [];
  private myHeight: Number;
  private parserData: any;

  constructor(
    private dxDataGrid: dxDataGridService,
    private dialog: MatDialog
  ) { }

  @Output() parserConfig: EventEmitter<any> = new EventEmitter<any>();

  @ViewChild(DxDataGridComponent) dataGrid: DxDataGridComponent;

  ngOnInit() {
    this.previewgridConfig = this.getPreviewGridConfig();
    this.myHeight = window.screen.availHeight - 287;
    this.updaterSubscribtion = this.previewObj.subscribe(data => {
      this.onUpdate(data)
    });
  }

  ngOnDestroy() {
    this.updaterSubscribtion.unsubscribe();
  }

  onUpdate(data) {
    if (data.samplesParsed) {
      this.parserData = cloneDeep(data.parser);
      const parsedData = data.samplesParsed;
      this.fieldInfo = cloneDeep(data.parser.fields);
      this.dataGrid.instance.beginCustomLoading('Loading...');
      setTimeout(() => {
        this.reloadDataGrid(parsedData);
      });
    }
  }

  ngAfterViewInit() {
    this.dataGrid.instance.option(this.previewgridConfig);
  }

  onResize(event) {
    this.myHeight = window.screen.availHeight - 287;
  }

  getPreviewGridConfig() {
    const dataSource = [];
    return this.dxDataGrid.mergeWithDefaultConfig({
      dataSource,
      columnAutoWidth: false,
      wordWrapEnabled: false,
      searchPanel: {
        visible: false,
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
        visible: false
      },
      sorting: {
        mode: 'none'
      },
      export: {
        fileName: 'Parsed_Sample'
      },
      scrolling: {
        showScrollbar: 'always',
        mode: 'virtual'
      },
      showRowLines: false,
      showBorders: false,
      rowAlternationEnabled: false,
      showColumnLines: true,
      selection: {
        mode: 'none'
      },
      customizeColumns: columns => {
        for (const column of columns) {
          column.headerCellTemplate = 'headerTemplate';
        }
      }
    });
  }

  reloadDataGrid(data) {
    this.dataGrid.instance.option('dataSource', data);
    this.dataGrid.instance.refresh();
    this.dataGrid.instance.endCustomLoading();
  }

  checkDate(event) {
    const ftype = event.srcElement.value;
    if (ftype === 'date') {
      this.openDateFormatDialog(event);
    } else {
      const formatEdit = event.currentTarget.nextElementSibling;
      formatEdit.style.visibility = 'hidden';
      if (this.fieldInfo[event.srcElement.id].format) {
        set(this.fieldInfo[event.srcElement.id], 'format', '');
      }
    }
  }

  openDateFormatDialog(event) {
    let index: number;
    if (event.type === 'click') {
      index = replace(event.target.id, 'edit_', '');
    } else {
      index = event.srcElement.id;
    }
    const dateformat = get(this.fieldInfo[index], 'format');
    const dateDialogRef = this.dialog.open(DateformatDialogComponent, {
      hasBackdrop: false,
      data: {
        placeholder: 'Enter date format',
        format: dateformat ? dateformat : ''
      }
    });

    dateDialogRef
      .afterClosed()
      .subscribe(format => {
        let index: number = -1;
        if (event.type === 'click') {
          index = replace(event.target.id, 'edit_', '');
        } else {
          index = event.srcElement.id;
        }
        if (format === '') {
          event.srcElement.value = get(this.parserData.fields[index], 'type');
        } else {
          set(this.fieldInfo[index], 'format', format);
          const editIcon = angular.element(document.getElementById(`edit_${index}`));
          editIcon.css('visibility', 'visible');
        }
      });
  }

  toAdd() {
    const config = assign(this.parserData, { fields: this.fieldInfo });
    this.parserConfig.emit(config);
  }
}
