declare function require(string): string;

import { Component, OnInit, ViewChild, AfterViewInit, EventEmitter, Output, OnDestroy } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material';
import { debounceTime } from 'rxjs/operators';

import { TreeNode, ITreeOptions } from 'angular-tree-component';
import { DxDataGridComponent } from 'devextreme-angular';
import { dxDataGridService } from '../../../../../common/services/dxDataGrid.service';
import { ToastService } from '../../../../../common/services/toastMessage.service'

import { DateformatDialogComponent } from '../dateformat-dialog/dateformat-dialog.component'
import { RawpreviewDialogComponent } from '../rawpreview-dialog/rawpreview-dialog.component'
import { WorkbenchService } from '../../../services/workbench.service';
import { STAGING_TREE } from '../../../wb-comp-configs'

const template = require('./select-rawdata.component.html');
require('./select-rawdata.component.scss');

@Component({
  selector: 'select-rawdata',
  template,
  styles: []
})

export class SelectRawdataComponent implements OnInit {
  private treeConfig: any;
  private userProject: string = 'project2';
  private treeNodes: Array<any> = STAGING_TREE;
  private treeOptions: ITreeOptions;
  private myHeight: number;
  private maskHelper: any;
  private gridConfig: Array<any>;
  private selFiles: Array<any> = [];
  private filePath: string;
  private fileMask: string = '';
  private fileMaskControl = new FormControl('', Validators.required);
  private currentPath: string = '';
  private nodeID = '';

  constructor(
    public dialog: MatDialog,
    private dxDataGrid: dxDataGridService,
    private workBench: WorkbenchService,
    private notify: ToastService
  ) { }

  @ViewChild(DxDataGridComponent) dataGrid: DxDataGridComponent;
  @ViewChild('tree') tree;
  @Output() onSelectFullfilled: EventEmitter<any> = new EventEmitter<any>();

  ngOnInit() {
    this.gridConfig = this.getGridConfig();
    this.treeConfig = this.getTreeConfig();
    this.myHeight = window.screen.availHeight - 345;
    this.maskHelper = 'INFO_TEXT';

  }

  ngAfterViewInit() {
    this.getPageData();
    this.dataGrid.instance.option(this.gridConfig);
    const stagingNode = this.tree.treeModel.getFirstRoot();
    stagingNode.expand();
    this.fileMaskControl.valueChanges
      .debounceTime(1000)
      .subscribe(mask => this.maskSearch(mask));
  }

  ngOnDestroy() {
    this.treeNodes = [];
  }

  onResize(event) {
    this.myHeight = window.screen.availHeight - 345;
  }

  getPageData() {
    this.workBench.getStagingData(this.userProject, '/').subscribe(data => {
      const filteredDataFiles = data.data.filter(d => d.isDirectory === false);
      this.reloadDataGrid(filteredDataFiles);
    });
  }

  getTreeConfig() {
    this.treeOptions = {
      displayField: 'name',
      hasChildrenField: 'isDirectory',
      getChildren: (node: TreeNode) => {
        const parentPath = node.data.path;
        const path = parentPath === 'root' ? '/' : `${parentPath}/${node.displayField}`;
        this.currentPath = path;
        this.nodeID = node.id;
        return this.workBench.getStagingData(this.userProject, path)
          .toPromise()
          .then(function (data) {
            const dir = data.data.filter(d => d.isDirectory === true);
            return dir;
          });
      },
      useVirtualScroll: false,
      animateExpand: true,
      animateSpeed: 30,
      animateAcceleration: 1.2
    }

    return this.treeOptions
  }

  openFolder(node) {
    const parentPath = node.data.path;
    const path = parentPath === 'root' ? '/' : `${parentPath}/${node.displayField}`;
    this.currentPath = path;
    this.nodeID = node.id;
    this.workBench.getStagingData(this.userProject, path).subscribe(data => {
      const filteredDataFiles = data.filter(d => d.isDirectory === false);
      this.reloadDataGrid(filteredDataFiles);
      this.clearSelected();
    });
  }

  onUpdate(data) {
    setTimeout(() => {
      this.reloadDataGrid(data);
    });
  }

  getGridConfig() {
    const dataSource = [];
    const columns = [{
      caption: 'Data Object',
      dataField: 'name',
      dataType: 'string',
      cellTemplate: 'dobjTemplate',
      width: '66%'
    }, {
      dataField: 'size',
      caption: 'Size',
      width: '15%',
      dataType: 'number',
      cellTemplate: 'sizeTemplate'
    }, {
      dataField: 'name',
      caption: 'Preview',
      alignment: 'center',
      width: '14%',
      allowFiltering: false,
      cellTemplate: 'actionsTemplate'
    }];

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
        mode: 'standard'
      },
      paging: {
        pageSize: 12
      },
      pager: {
        showPageSizeSelector: true,
        allowedPageSizes: [10, 20, 50, 100],
        showInfo: true
      },
      filterRow: {
        visible: false,
        applyFilter: 'auto'
      },
      headerFilter: {
        visible: false
      },
      showRowLines: false,
      showBorders: false,
      rowAlternationEnabled: false,
      showColumnLines: false,
      selection: {
        mode: 'single'
      },
      onSelectionChanged: selectedItems => {
        const currFile = selectedItems.selectedRowsData[0];
        if (currFile) {
          this.filePath = `${currFile.path}/${currFile.name}`;
          this.fileMask = currFile.name;
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
    const tempFiles = this.dataGrid.instance.option('dataSource');
    this.selFiles = this.workBench.filterFiles(mask, tempFiles);
    if (this.selFiles.length > 0) {
      this.filePath = `${this.selFiles[0].path}/${this.fileMask}`;
      this.onSelectFullfilled.emit({ selectFullfilled: true, selectedFiles: this.selFiles, filePath: this.filePath });
    } else {
      this.onSelectFullfilled.emit({ selectFullfilled: false, selectedFiles: this.selFiles, filePath: this.filePath });
    }
  }

  clearSelected() {
    this.selFiles = [];
    this.fileMask = '';
  }

  previewDialog(title): void {
    const path = `${this.currentPath}/${title}`;
    this.workBench.getRawPreviewData(this.userProject, path).subscribe(data => {
      const dialogRef = this.dialog.open(RawpreviewDialogComponent, {
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
    let filesToUpload = event.srcElement.files;
    const validSize = this.workBench.validateMaxSize(filesToUpload);
    const validType = this.workBench.validateFileTypes(filesToUpload);
    if (validSize && validType) {
      const path = this.currentPath;
      this.workBench.uploadFile(filesToUpload, this.userProject, path).subscribe(data => {

      });
    } else {
      this.notify.warn('Only ".csv" or ".txt" extension files are supported', 'Unsupported file type');
    }
  }
  /**
   * Opens dialog to input folder name. Once closed returns the filename entered.
   * 
   * @memberof SelectRawdataComponent
   */
  createFolder() {
    const dateDialogRef = this.dialog.open(DateformatDialogComponent, {
      hasBackdrop: false,
      data: {
        placeholder: 'Enter folder name'
      }
    });

    dateDialogRef
      .afterClosed()
      .subscribe(name => {
        if (name !== '') {
          const path = this.currentPath === '/' ? `/${name}` : `${this.currentPath}/${name}`;
          this.workBench.createFolder(this.userProject, path).subscribe(data => {
            const currentNode = this.tree.treeModel.getNodeById(this.nodeID);
            currentNode.data.children = data.data;
            this.tree.treeModel.update();
          });
        }
      });
  }
}