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
  SqlBuilder,
  Sort,
  Analysis
} from '../../types';
import { DesignerStates } from '../../container';

const template = require('./multi-table-designer-layout.component.html');
require('./multi-table-designer-layout.component.scss');

@Component({
  selector: 'multi-table-designer-layout',
  template
})
export class MultiTableDesignerLayout {
  @Output() change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input() artifacts: Artifact[];
  @Input() analysis: Analysis;
  @Input() sorts: Sort[];
  @Input() isInQueryMode: boolean;
  @Input() sqlBuilder: SqlBuilder;
  @Input() designerState: DesignerStates;
  @Input('data') set setData(data) {
    if (data) {
      this.data = data;
      this.isGridPanelExpanded = true;
    }
  };
  public data;
  public isGridPanelExpanded: boolean = false;

  toggleGridPanel() {
    this.isGridPanelExpanded = !this.isGridPanelExpanded;
  }

  onQueryChange() {
    this.change.emit({subject: 'changeQuery'});
  }

  onSaveQuery(evt) {
    this.change.emit({subject: 'submitQuery'});
  }
}
