import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { MatDialog } from '@angular/material';
import { NestedTreeControl } from '@angular/cdk/tree';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import trim from 'lodash/trim';

import { CreatefolderDialogComponent } from './createFolder-dialog';
import {
  DynamicNode,
  DynamicTreeDataSourceService
} from './dynamic-tree-data-source.service';

export interface ISelectionEvent {
  folder: any;
  files: any[];
}
export interface IFileSystemAPI {
  getDir: (path: string) => Observable<any>;
  createDir: (path: string) => Observable<any>;
}
@Component({
  selector: 'remote-folder-selector',
  templateUrl: 'remote-folder-selector.component.html',
  styleUrls: ['remote-folder-selector.component.scss']
})
export class RemoteFolderSelectorComponent implements OnInit {
  @Output() selectionChange = new EventEmitter<any>();
  @Input() fileSystemAPI: IFileSystemAPI;
  @Input() rootNode: any;
  @Input() enableFolderCreation = false;

  treeControl: NestedTreeControl<DynamicNode>;
  dataSource: DynamicTreeDataSourceService;

  public selectedNode: DynamicNode = null;

  constructor(private _dialog: MatDialog) {
    this.treeControl = new NestedTreeControl<DynamicNode>(this._getChildren);
    this.onNodeChildrenLoaded = this.onNodeChildrenLoaded.bind(this);
  }

  ngOnInit() {
    this.dataSource = new DynamicTreeDataSourceService(
      this.treeControl,
      this.fileSystemAPI.getDir,
      this.onNodeChildrenLoaded
    );
    this.dataSource.initializeData(this.rootNode);
  }

  private _getChildren(node: DynamicNode) {
    return node.children;
  }

  createFolder(node) {
    const dateDialogRef = this._dialog.open(CreatefolderDialogComponent, {
      hasBackdrop: true,
      autoFocus: true,
      closeOnNavigation: true,
      disableClose: true,
      height: '236px',
      width: '350px'
    });

    dateDialogRef.afterClosed().subscribe(name => {
      if (trim(name) !== '' && name !== 'null') {
        const currentFolderPath = node.payload.path;
        const currentFolderName = node.payload.name;
        let path;
        switch (currentFolderPath) {
          case 'root':
            path = `/${name}`;
            break;
          case '/':
            path = `/${currentFolderName}/${name}`;
            break;
          default:
            path = `${currentFolderPath}/${currentFolderName}/${name}`;
        }

        this.fileSystemAPI
          .createDir(path)
          .pipe(map(data => data.data))
          .subscribe(data => {
            this.dataSource.updateNodeChildren(node, data);
          });
      }
    });
  }

  onSelectionChange() {
    const eventResult = {
      folder: this.selectedNode.payload,
      files: this.selectedNode.nonNodeChildren
    };
    this.selectionChange.emit(eventResult);
  }

  onNodeChildrenLoaded(node: DynamicNode) {
    if (this.selectedNode === node) {
      this.onSelectionChange();
    }
  }

  selectNode(node) {
    this.selectedNode = node;
    this.onSelectionChange();
  }

  isNodeEmpty(node) {
    return node.children && node.children.length === 0;
  }

  getNodeLabel(node) {
    return node.payload.name;
  }
}
