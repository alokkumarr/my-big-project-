import { Component, Input, OnInit, ViewChild, AfterViewInit, EventEmitter, Output } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material';
import { debounceTime } from 'rxjs/operators';

import { TreeNode, ITreeOptions } from 'angular-tree-component';
import { DxDataGridComponent } from 'devextreme-angular';
import { dxDataGridService } from '../../../../../common/services/dxDataGrid.service';

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

  constructor(
    public dialog: MatDialog,
    private dxDataGrid: dxDataGridService,
    private workBench: WorkbenchService
  ) { }

  @ViewChild(DxDataGridComponent) dataGrid: DxDataGridComponent;
  @ViewChild('tree') tree;
  @Output() onSelectFullfilled: EventEmitter<any> = new EventEmitter<any>();

  ngOnInit() {
    this.gridConfig = this.getGridConfig();
    this.treeConfig = this.getTreeConfig();
    this.myHeight = window.screen.availHeight - 345;
    this.maskHelper = `Follow one of the following formats for selection.
                       1.file1.csv -- To select single file.
                       2. file* -- Select all files starting with specified characters.
                       3. *.csv -- To select all files of given type(csv/parquet/txt).`

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

  onResize(event) {
    this.myHeight = window.screen.availHeight - 345;
  }

  getPageData() {
    this.workBench.getTreeData(this.userProject, null).subscribe(data => {
      const filteredDataFiles = data.filter(d => d.d === false);
      this.reloadDataGrid(filteredDataFiles);
    });
  }

  getTreeConfig() {
    this.treeOptions = {
      displayField: 'name',
      hasChildrenField: 'd',
      getChildren: (node: TreeNode) => {
        const tempPath = node.data.cat === 'root' ? node.displayField : `${node.data.cat}/${node.displayField}`;
        const path = tempPath === '/Staging' ? null : tempPath;
        this.currentPath = path;
        return this.workBench.getTreeData(this.userProject, path)
          .toPromise()
          .then(function (data) {
            const dir = data.filter(d => d.d === true);
            return dir;
          });
      },
      useVirtualScroll: true,
      animateExpand: true,
      animateSpeed: 30,
      animateAcceleration: 1.2
    }

    return this.treeOptions
  }

  openFolder(node) {
    const tempPath = node.data.cat === 'root' ? node.displayField : `${node.data.cat}/${node.displayField}`;
    const path = tempPath === '/Staging' ? null : tempPath;
    this.currentPath = path;
    this.workBench.getTreeData(this.userProject, path).subscribe(data => {
      const filteredDataFiles = data.filter(d => d.d === false);
      this.reloadDataGrid(filteredDataFiles);
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
          this.filePath = currFile.cat === 'root' ? currFile.name : `${currFile.cat}/${currFile.name}`;
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
      this.filePath = this.selFiles[0].cat === 'root' ? `/${this.fileMask}` : `${this.selFiles[0].cat}/${this.fileMask}`;
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
    const path = this.currentPath === null ? `/${title}` : `${this.currentPath}/${title}`;
    this.workBench.getRawPreviewData(this.userProject, path).subscribe(data => {
      const dialogRef = this.dialog.open(RawpreviewDialogComponent, {
        data: {
          title: title,
          rawData: data.samplesRaw
        }
      });
    });
  }

  fileInput(event) {
    let fileToUpload = event.srcElement.files[0];
    const path = this.currentPath === null ? '/' : this.currentPath;
    this.workBench.uploadFile(fileToUpload, this.userProject, path).subscribe(data => {

    });
  }

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
          const path = this.currentPath === null ? `/${name}` : `${this.currentPath}/${name}`;
          this.workBench.createFolder(this.userProject, path).subscribe(data => {
            // this.nodes.push({ name: 'another node' });
            this.tree.treeModel.update();
          });
        }

      });
  }
}