import {
  Component,
  OnInit,
  Input,
  Output,
  EventEmitter,
  ChangeDetectionStrategy
} from '@angular/core';
import { ITreeOptions, TREE_ACTIONS } from 'angular-tree-component';
import { MenuItem } from '../../../../common/state/common.state.model';

interface SelectMenuOutput {
  moduleName: string;
  menuItem: MenuItem;
}

@Component({
  selector: 'admin-export-tree',
  templateUrl: './tree.component.html',
  styleUrls: ['./tree.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminExportTreeComponent implements OnInit {
  @Input() menu: MenuItem[];

  /**
   * Happens when a leaf node (which has no children) is clicked.
   *
   * @type {EventEmitter<SelectMenuOutput>}
   * @memberof AdminExportTreeComponent
   */
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

  /**
   * Handles clicking a menu item
   *
   * @param {*} event
   * @memberof AdminExportTreeComponent
   */
  onClickMenuItem(event) {
    this.select.emit({ moduleName: 'ANALYZE', menuItem: event.node.data });
  }
}
