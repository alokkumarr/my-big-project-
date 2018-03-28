declare function require(string): string;

import { Component, OnInit, ViewChild, AfterViewInit, EventEmitter, Output, OnDestroy } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material';
import { debounceTime } from 'rxjs/operators';
import * as trim from 'lodash/trim';
import * as uniq from 'lodash/uniq';
import * as filter from 'lodash/filter';
import * as get from 'lodash/get';
import * as cloneDeep from 'lodash/cloneDeep';

import { TreeNode, ITreeOptions } from 'angular-tree-component';
import { DxDataGridComponent } from 'devextreme-angular';
import { dxDataGridService } from '../../../../../common/services/dxDataGrid.service';
import { ToastService } from '../../../../../common/services/toastMessage.service'

import { CreatefolderDialogComponent } from '../createFolder-dialog/createfolder-dialog.component';
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
  private treeNodes: Array<any>;
  private treeOptions: ITreeOptions;
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
    this.treeNodes = cloneDeep(STAGING_TREE);
    this.gridConfig = this.getGridConfig();
    this.treeConfig = this.getTreeConfig();
    this.maskHelper = 'INFO_TEXT';
  }

  ngAfterViewInit() {
    this.getPageData();
    this.dataGrid.instance.option(this.gridConfig);
    const stagingNode = this.tree.treeModel.getFirstRoot();
    stagingNode.expand();
    stagingNode.setIsActive(true);
    this.nodeID = stagingNode.id;
    this.fileMaskControl.valueChanges
      .debounceTime(1000)
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

  getTreeConfig() {
    this.treeOptions = {
      displayField: 'name',
      hasChildrenField: 'isDirectory',
      getChildren: (node: TreeNode) => {
        const parentPath = node.data.path;
        const path = parentPath === 'root' ? '/' : `${parentPath}/${node.displayField}`;
        // this.currentPath = path;
        // this.nodeID = node.id;
        return this.workBench.getStagingData(path)
          .toPromise()
          .then(function (data) {
            const dir = filter(data.data, ['isDirectory', true]);
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
    this.workBench.getStagingData(path).subscribe(data => {
      const filteredDataFiles = filter(data.data, ['isDirectory', false])
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
      caption: 'File',
      dataField: 'name',
      dataType: 'string',
      cellTemplate: 'dobjTemplate',
      width: '66%',
      allowSorting: true,
      sortOrder: 'asc'
    }, {
      dataField: 'size',
      caption: 'Size',
      width: '15%',
      dataType: 'number',
      cellTemplate: 'sizeTemplate',
      allowSorting: true
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
        showScrollbar: 'always',
        mode: 'virtual',
        useNative: false
      },
      sorting: {
        mode: 'multiple'
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
    this.workBench.getRawPreviewData(path).subscribe(data => {
      const dialogRef = this.dialog.open(RawpreviewDialogComponent, {
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
    let filesToUpload = event.srcElement.files;
    const validSize = this.workBench.validateMaxSize(filesToUpload);
    const validType = this.workBench.validateFileTypes(filesToUpload);
    if (validSize && validType) {
      const path = this.currentPath;
      this.workBench.uploadFile(filesToUpload, path).subscribe(data => {
        const filteredDataFiles = filter(data.data, ['isDirectory', false])
        this.reloadDataGrid(filteredDataFiles);
        this.clearSelected();
      });
    } else {
      this.notify.warn('Only ".csv" or ".txt" extension files are supported', 'Unsupported file type');
    }
  }
  /**
   * Opens dialog to input folder name. Once closed returns the filename entered.
   * Gets the children of the directory from service output and push only the newly added child to parent.
   * 
   * @memberof SelectRawdataComponent
   */
  createFolder() {
    const dateDialogRef = this.dialog.open(CreatefolderDialogComponent, {
      hasBackdrop: true,
      autoFocus: true,
      closeOnNavigation: true,
      disableClose: true,
      height: '236px',
      width: '350px'
    });

    dateDialogRef
      .afterClosed()
      .subscribe(name => {
        if (trim(name) !== '' && name != 'null') {
          const path = this.currentPath === '/' ? `/${name}` : `${this.currentPath}/${name}`;
          this.workBench.createFolder(path).subscribe(data => {
            const currentNode = this.tree.treeModel.getNodeById(this.nodeID);
            const currChilds = get(currentNode.data, 'children', []);
            var uniqueResults = data.data.filter(obj => {
              return !currChilds.some(obj2 => {
                return obj.name == obj2.name;
              });
            });
            const newDir = filter(uniqueResults, ['isDirectory', true]);
            if (currChilds.length === 0) {
              currentNode.data.children = newDir;
            } else {
              if (newDir.length > 0) {
                currentNode.data.children.push(newDir[0]);
              }
            }
            this.tree.treeModel.update();
            currentNode.expand();
          });
        }
      });
  }
}