import {
  Component,
  OnInit,
  OnDestroy,
  AfterViewInit,
  ViewChild,
  EventEmitter,
  Output
} from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material';
import * as filter from 'lodash/filter';
import * as cloneDeep from 'lodash/cloneDeep';

import { DxDataGridComponent } from 'devextreme-angular/ui/data-grid';
import { DxDataGridService } from '../../../../../common/services/dxDataGrid.service';
import { ToastService } from '../../../../../common/services/toastMessage.service';
import { IFileSystemAPI, ISelectionEvent } from '../../../../../common/components/remote-folder-selector';

import { RawpreviewDialogComponent } from '../rawpreview-dialog/rawpreview-dialog.component';
import { WorkbenchService } from '../../../services/workbench.service';
import { STAGING_TREE } from '../../../wb-comp-configs';

import { debounceTime } from 'rxjs/operators';

@Component({
  selector: 'select-rawdata',
  templateUrl: './select-rawdata.component.html',
  styleUrls: ['./select-rawdata.component.scss']
})
export class SelectRawdataComponent
  implements OnInit, AfterViewInit, OnDestroy {
  public treeConfig: any; // tslint:disable-line
  public treeNodes: Array<any>; // tslint:disable-line
  public gridConfig: Array<any>;
  public selFiles: Array<any> = [];
  public filePath: string;
  public fileMask = '';
  public fileMaskControl = new FormControl('', Validators.required);
  public currentPath = '';
  public nodeID = '';
  public fileSystemAPI: IFileSystemAPI;

  constructor(
    public dialog: MatDialog,
    public dxDataGrid: DxDataGridService,
    public workBench: WorkbenchService,
    public notify: ToastService
  ) {
    this.fileSystemAPI = {
      getDir: this.workBench.getStagingData,
      createDir: this.workBench.createFolder
    };
  }

  @ViewChild(DxDataGridComponent) dataGrid: DxDataGridComponent;
  @Output() onSelectFullfilled: EventEmitter<any> = new EventEmitter<any>();

  ngOnInit() {
    this.treeNodes = cloneDeep(STAGING_TREE);
    this.gridConfig = this.getGridConfig();
  }

  ngAfterViewInit() {
    this.getPageData();
    this.dataGrid.instance.option(this.gridConfig);
    this.fileMaskControl.valueChanges
      .pipe(debounceTime(1000))
      .subscribe(mask => this.maskSearch(mask));
  }

  ngOnDestroy() {
    this.treeNodes = [];
  }

  getPageData() {
    this.workBench.getStagingData('/').subscribe(data => {
      const filteredDataFiles = filter(data.data, ['isDirectory', false]);
      this.reloadDataGrid(filteredDataFiles);
    });
  }

  onFolderSelected({files}: ISelectionEvent) {
    this.reloadDataGrid(files);
    this.clearSelected();
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
        caption: 'File',
        dataField: 'name',
        dataType: 'string',
        cellTemplate: 'dobjTemplate',
        width: '66%',
        allowSorting: true,
        sortOrder: 'asc'
      },
      {
        dataField: 'size',
        caption: 'Size',
        width: '15%',
        dataType: 'number',
        cellTemplate: 'sizeTemplate',
        allowSorting: true
      },
      {
        dataField: 'name',
        caption: 'Preview',
        alignment: 'right',
        width: '14%',
        allowFiltering: false,
        cellTemplate: 'actionsTemplate'
      }
    ];

    return this.dxDataGrid.mergeWithDefaultConfig({
      columns,
      dataSource,
      searchPanel: {
        visible: false,
        width: 240,
        placeholder: 'Search...'
      },
      height: '100%',
      scrolling: {
        showScrollbar: 'always',
        mode: 'virtual',
        useNative: false
      },
      sorting: {
        mode: 'multiple'
      },
      filterRow: {
        visible: true,
        applyFilter: 'auto'
      },
      headerFilter: {
        visible: false
      },
      showRowLines: false,
      showBorders: false,
      rowAlternationEnabled: true,
      showColumnLines: false,
      selection: {
        mode: 'single'
      },
      onSelectionChanged: selectedItems => {
        const currFile = selectedItems.selectedRowsData[0];
        if (currFile) {
          this.filePath = `${currFile.path}/${currFile.name}`;
          this.fileMask = currFile.name;
          this.fileMaskControl.setValue(this.fileMask);
        }
        this.selFiles = [];
        this.selFiles = selectedItems.selectedRowsData;
      }
    });
  }

  reloadDataGrid(data) {
    this.dataGrid.instance.option('dataSource', data);
    this.dataGrid.instance.refresh();
  }

  maskSearch(mask) {
    this.fileMask = this.fileMaskControl.value;
    const tempFiles = this.dataGrid.instance.option('dataSource');
    this.selFiles = this.workBench.filterFiles(mask, tempFiles);
    if (this.selFiles.length > 0) {
      this.filePath = `${this.selFiles[0].path}/${this.fileMask}`;
      this.onSelectFullfilled.emit({
        selectFullfilled: true,
        selectedFiles: this.selFiles,
        filePath: this.filePath
      });
    } else {
      this.onSelectFullfilled.emit({
        selectFullfilled: false,
        selectedFiles: this.selFiles,
        filePath: this.filePath
      });
    }
  }

  clearSelected() {
    this.selFiles = [];
    this.fileMask = '';
  }

  previewDialog(title): void {
    const path = `${this.currentPath}/${title}`;
    this.workBench.getRawPreviewData(path).subscribe(data => {
      this.dialog.open(RawpreviewDialogComponent, {
        minHeight: 500,
        minWidth: 600,
        data: {
          title: title,
          rawData: data.data
        }
      });
    });
  }
  /**
   * File upload function.
   * Validates size and type(Allows only txt/csv)
   * If valid then only sends the formdata to upload
   * @param {any} event
   * @memberof SelectRawdataComponent
   */
  fileInput(event) {
    const filesToUpload = event.srcElement.files;
    const validSize = this.workBench.validateMaxSize(filesToUpload);
    const validType = this.workBench.validateFileTypes(filesToUpload);
    if (validSize && validType) {
      const path = this.currentPath;
      this.workBench.uploadFile(filesToUpload, path).subscribe(data => {
        const filteredDataFiles = filter(data.data, ['isDirectory', false]);
        this.reloadDataGrid(filteredDataFiles);
        this.clearSelected();
      });
    } else {
      this.notify.warn(
        'Only ".csv" or ".txt" extension files are supported',
        'Unsupported file type'
      );
    }
  }
}
