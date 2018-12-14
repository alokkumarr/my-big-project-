import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { ITreeOptions, TREE_ACTIONS } from 'angular-tree-component';
import { Observable } from 'rxjs/Observable';
import { MenuItem } from '../../../../common/state/common.state.model';

interface SelectMenuOutput {
  moduleName: string;
  menuItem: MenuItem;
}

@Component({
  selector: 'admin-export-tree',
  templateUrl: './tree.component.html',
  styleUrls: ['./tree.component.scss']
})
export class AdminExportTreeComponent implements OnInit {
  // @Select(state => state.common.observeMenu) observeMenu$: Observable<Menu>;

  @Input() menu: Observable<MenuItem[]>;
  @Output() select: EventEmitter<SelectMenuOutput> = new EventEmitter();

  treeOptions: ITreeOptions = {
    isExpandedField: 'expanded',
    actionMapping: {
      mouse: {
        click: (tree, node, $event) => {
          if (node.hasChildren) {
            TREE_ACTIONS.TOGGLE_EXPANDED(tree, node, $event);
          } else {
            TREE_ACTIONS.ACTIVATE(tree, node, $event);
          }
        }
      }
    }
  };

  constructor() {}

  ngOnInit() {}

  onClickMenuItem(event) {
    this.select.emit({ moduleName: 'ANALYZE', menuItem: event.node.data });
  }
}
