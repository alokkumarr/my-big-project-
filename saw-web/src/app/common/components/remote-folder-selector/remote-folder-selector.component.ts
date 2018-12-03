import { Component, OnInit, Input } from '@angular/core';

import { NestedTreeControl } from '@angular/cdk/tree';
import { Observable } from 'rxjs';

import { DynamicNode, DynamicTreeDataSourceService } from './dynamic-tree-data-source.service';

@Component({
  selector: 'remote-folder-selector',
  templateUrl: 'remote-folder-selector.component.html'
})
export class RemoteFolderSelectorComponent implements OnInit {

  @Input() getter: (path: string) => Observable<any>;

  treeControl: NestedTreeControl<DynamicNode>;
  dataSource: DynamicTreeDataSourceService;

  constructor() {
    const getChildren = (node: DynamicNode) => node.children;
    this.treeControl = new NestedTreeControl<DynamicNode>(getChildren);
  }

  ngOnInit() {
    this.dataSource = new DynamicTreeDataSourceService(this.treeControl, this.getter);
    this.dataSource.initializeData();
  }

  isDirectory(node: DynamicNode) {
    return node.payload.isDirectory;
  }

  getNodeLabel(node) {
    return node.payload.name;
  }
}
