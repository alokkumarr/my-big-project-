import { Injectable } from '@angular/core';
import { FlatTreeControl } from '@angular/cdk/tree';
import { CollectionViewer, SelectionChange } from '@angular/cdk/collections';
import { BehaviorSubject, merge, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as isFunction from 'lodash/isFunction';
import * as lodashMap from 'lodash/map';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpGroupBy from 'lodash/fp/groupBy';

export interface IFileSystemUnit {
  isDirectory: boolean;
  name: string;
  path: string;
  size: number;
  subDirectories: boolean;
}

export class DynamicNode {
  public isLoading = false;
  public children: DynamicNode[] = null;
  public nonNodeChildren: IFileSystemUnit[] = null;

  constructor(public payload: IFileSystemUnit) {
    if (payload.size === 0) {
      this.children = [];
    }
  }
}
const defaultRootData: IFileSystemUnit = {
  name: 'root',
  path: 'root',
  size: Infinity,
  isDirectory: true,
  subDirectories: true
};

@Injectable()
export class DynamicTreeDataSourceService {
  dataChange = new BehaviorSubject<DynamicNode[]>([]);

  get data(): DynamicNode[] {
    return this.dataChange.value;
  }
  set data(value: DynamicNode[]) {
    this.treeControl.dataNodes = value;
    this.dataChange.next(value);
  }

  constructor(
    private treeControl: FlatTreeControl<DynamicNode>,
    private getter: (path: string) => Observable<any>,
    public onNodeChildrenLoaded: (node: DynamicNode) => void
  ) {}

  connect(collectionViewer: CollectionViewer): Observable<DynamicNode[]> {
    this.treeControl.expansionModel.onChange.subscribe(change => {
      if (
        (change as SelectionChange<DynamicNode>).added ||
        (change as SelectionChange<DynamicNode>).removed
      ) {
        this.handleTreeControl(change as SelectionChange<DynamicNode>);
      }
    });

    return merge(collectionViewer.viewChange, this.dataChange).pipe(
      map(() => this.data)
    );
  }

  public updateNodeChildren(node, data) {
    this._setNodeChildren(node, data);
    this.onNodeChildrenLoaded(node);
    this.treeControl.expand(node);
    const _data = this.data;
    this.dataChange.next(null);
    this.dataChange.next(_data);
  }

  private _setNodeChildren(node, data) {
    const { nodeChildren, nonNodeChildren } = this._getGroupedChildren(
      data
    );
    node.children = nodeChildren;
    node.nonNodeChildren = nonNodeChildren;
  }

  initializeData(rootData = defaultRootData) {
    if (isFunction(this.getter)) {
      this.getter('/')
        .pipe(
          map(data => data.data)
        )
        .toPromise()
        .then(data => {
          const rootNode = new DynamicNode(rootData);
          this._setNodeChildren(rootNode, data);
          this.dataChange.next([rootNode]);
          this.treeControl.expand(rootNode);
        });
    }
  }

  /** Handle expand/collapse behaviors */
  handleTreeControl(change: SelectionChange<DynamicNode>) {
    if (change.added) {
      change.added.forEach(node => this._toggleNode(node, true));
    }
    if (change.removed) {
      change.removed
        .slice()
        .reverse()
        .forEach(node => this._toggleNode(node, false));
    }
  }

  /**
   * Toggle the node, remove from display list
   */
  private _toggleNode(node: DynamicNode, expand: boolean) {
    if (expand) {
      this.loadNodeChildrenIfNeeded(node);
    }
  }

  private _isNode(payload) {
    return payload.isDirectory;
  }

  private _getGroupedChildren(children) {
    return fpPipe(
      fpGroupBy(child =>
        this._isNode(child) ? 'nodeChildren' : 'nonNodeChildren'
      ),
      ({ nodeChildren = [], nonNodeChildren = [] }) => ({
        nodeChildren: lodashMap(nodeChildren, datum => new DynamicNode(datum)),
        nonNodeChildren
      })
    )(children);
  }

  loadNodeChildrenIfNeeded(node: DynamicNode) {
    if (node.children) {
      return;
    }
    const { path, name } = node.payload;

    this.getter(`${path}/${name}`)
      .pipe(
        map(data => data.data)
      )
      .toPromise()
      .then(data => {
        this._setNodeChildren(node, data);
        this.onNodeChildrenLoaded(node);
        // have to set this.data to null first because of a bug in mat-tree
        // https://stackoverflow.com/questions/51100217/why-is-my-angular-app-becoming-very-slow-after-changing-the-data-backing-a-mat-t
        const _data = this.data;
        this.dataChange.next(null);
        this.dataChange.next(_data);
      });
  }
}
