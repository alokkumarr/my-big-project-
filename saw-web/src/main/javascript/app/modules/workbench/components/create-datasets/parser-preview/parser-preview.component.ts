declare function require(string): string;

import * as angular from 'angular';
import { Component, Input, OnInit, Inject, ViewChild, AfterViewInit, EventEmitter, Output } from '@angular/core';
import { Subject } from 'rxjs/Subject';
import * as filter from 'lodash/filter';
import * as set from 'lodash/set';
import * as get from 'lodash/get';
import * as map from 'lodash/map';
import * as indexOf from 'lodash/indexOf';
import * as forEach from 'lodash/forEach';
import * as replace from 'lodash/replace';
import * as cloneDeep from 'lodash/cloneDeep';
import * as assign from 'lodash/assign';
import * as has from 'lodash/has';
import * as take from 'lodash/take';

import { MatDialog } from '@angular/material';
import { DxDataGridComponent } from 'devextreme-angular';

import { dxDataGridService } from '../../../../../common/services/dxDataGrid.service';
import { DateformatDialogComponent } from '../dateformat-dialog/dateformat-dialog.component'
import { WorkbenchService } from '../../../services/workbench.service';

const template = require('./parser-preview.component.html');
require('./parser-preview.component.scss');

@Component({
  selector: 'parser-preview',
  template,
  styles: []
})

export class ParserPreviewComponent implements OnInit {
  @Input() previewObj: Subject<any>;
  private gridListInstance: any;
  private previewgridConfig: Array<any>;
  private gridData: Array<any>;
  private updaterSubscribtion: any;
  private toAddSubscribtion: any;
  private fieldInfo = [];
  private parserData: any;
  private rawFile: any;
  private userProject = 'project2';

  constructor(
    private dxDataGrid: dxDataGridService,
    private dialog: MatDialog,
    private workBench: WorkbenchService
  ) { }

  @Output() parserConfig: EventEmitter<any> = new EventEmitter<any>();

  @ViewChild(DxDataGridComponent) dataGrid: DxDataGridComponent;

  ngOnInit() {
    this.previewgridConfig = this.getPreviewGridConfig();
    this.updaterSubscribtion = this.previewObj.subscribe(data => {
      this.onUpdate(data)
    });
  }

  ngOnDestroy() {
    this.updaterSubscribtion.unsubscribe();
  }

  onUpdate(data) {
    if (data.samplesParsed) {
      this.parserData = cloneDeep(data);
      const parsedData = data.samplesParsed;
      this.fieldInfo = cloneDeep(data.fields);
      setTimeout(() => {
        this.dataGrid.instance.beginCustomLoading('Loading...');
        this.reloadDataGrid(parsedData);
      });
      this.rawPreview(data.info.file);
    }
  }

  ngAfterViewInit() {
    this.dataGrid.instance.option(this.previewgridConfig);
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
      const index = event.srcElement.id;
      if (has(this.parserData.fields[index], 'format')) {
        set(this.fieldInfo[index], 'format', get(this.parserData.fields[index], 'format'));
      }
    }
  }
  /*set the user provided date format for the Field. If the format is empty revert back the field type to original. */
  openDateFormatDialog(event) {
    let index: number;
    let dateformat = '';
    let formatArr = [];
    if (event.type === 'click') {
      index = replace(event.target.id, 'edit_', '');
    } else {
      index = event.srcElement.id;
    }
    if (has(this.fieldInfo[index], 'format')) {
      if (this.fieldInfo[index].format.length > 1) {
        formatArr = this.fieldInfo[index].format;
      } else {
        dateformat = get(this.fieldInfo[index], 'format[0]');
      }
    }
    const dateDialogRef = this.dialog.open(DateformatDialogComponent, {
      hasBackdrop: false,
      data: {
        placeholder: 'Enter date format',
        format: dateformat,
        formatArr: formatArr
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
        const editIcon = angular.element(document.getElementById(`edit_${index}`));
        if (format === '' && has(this.parserData.fields[index], 'format')) {
          if (this.parserData.fields[index].format.length === 0 || this.parserData.fields[index].format.length > 1) {
            event.srcElement.value = get(this.parserData.fields[index], 'type');
            editIcon.css('visibility', 'hidden');
          } else if (this.parserData.fields[index].format.length === 1) {
            set(this.fieldInfo[index], 'format[0]', get(this.parserData.fields[index], 'format[0]'));
            editIcon.css('visibility', 'visible');
          }

        } else if (format === '' && !has(this.parserData.fields[index], 'format')) {
          event.srcElement.value = get(this.parserData.fields[index], 'type');
          editIcon.css('visibility', 'hidden');
        } else if (format !== '') {
          set(this.fieldInfo[index], 'format', [format]);
          editIcon.css('visibility', 'visible');
        }
      });
  }

  toAdd() {
    const fieldsObj = map(this.fieldInfo,  obj => {
      if (obj.format) {
        obj.format = obj.format[0];  // Parser component expects format to be a string.
      }
      return obj;
    });
    const config = assign(this.parserData, { fields: fieldsObj });
    this.parserConfig.emit(config);
  }

  errorInfoVisibility(index) {
    if (get(this.fieldInfo[index], 'format').length > 1) {
      return 'visible';
    }
    return 'hidden';
  }

  rawPreview(filePath) {
    this.workBench.getRawPreviewData(this.userProject, filePath).subscribe(data => {
      this.rawFile = take(data.data, 50);
    });
  }
}
