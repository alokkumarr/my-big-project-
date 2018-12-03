import { Injectable } from '@angular/core';
import { FlatTreeControl } from '@angular/cdk/tree';
import { CollectionViewer, SelectionChange } from '@angular/cdk/collections';
import { BehaviorSubject, merge, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as isFunction from 'lodash/isFunction';
import * as lodashMap from 'lodash/map';

/** Flat node with expandable and level information */
export class DynamicNode {
  public isLoading = false;
  public children: DynamicNode[] = null;

  constructor(public payload: any) {
    if (payload.size === 0) {
      this.children = [];
    }
  }
}

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
    private getter: (path: string) => Observable<any>
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

  initializeData() {
    if (isFunction(this.getter)) {
      this.getter('/')
        .pipe(
          map(data => data.data),
          map(data => lodashMap(data, datum => new DynamicNode(datum)))
        )
        .toPromise()
        .then(data => {
          this.data = data;
        });
    }
  }

  /** Handle expand/collapse behaviors */
  handleTreeControl(change: SelectionChange<DynamicNode>) {
    if (change.added) {
      change.added.forEach(node => this.toggleNode(node, true));
    }
    if (change.removed) {
      change.removed
        .slice()
        .reverse()
        .forEach(node => this.toggleNode(node, false));
    }
  }

  /**
   * Toggle the node, remove from display list
   */
  toggleNode(node: DynamicNode, expand: boolean) {
    if (expand) {
      this.loadNodeChildrenIfNeeded(node);
    }
  }

  loadNodeChildrenIfNeeded(node: DynamicNode) {
    if (node.children) {
      return;
    }
    const { path, name } = node.payload;

    this.getter(`${path}/${name}`)
      .pipe(
        map(data => data.data),
        map(children => lodashMap(children, child => new DynamicNode(child)))
      )
      .toPromise()
      .then(children => (node.children = children));
  }
}
