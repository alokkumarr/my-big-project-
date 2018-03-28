declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';

import {
  Artifact,
  DesignerChangeEvent,
  SqlBuilder
} from '../../types';
import { DesignerStates } from '../../container';

const template = require('./multi-table-designer-layout.component.html');
require('./multi-table-designer-layout.component.scss');

@Component({
  selector: 'multi-table-designer-layout',
  template
})
export class MultiTableDesignerLayout {
  @Output() public settingsChange: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input() artifacts: Artifact[];
  @Input() isInQueryMode: boolean;
  @Input() query: boolean;
  @Input() data;
  @Input() sqlBuilder: SqlBuilder;
  @Input() designerState: DesignerStates;
  public isGridPanelExpanded: boolean = false;

  toggleGridPanel() {
    this.isGridPanelExpanded = !this.isGridPanelExpanded;
  }
}
