import { Component, Input, Output, EventEmitter } from '@angular/core';

import { Artifact, DesignerChangeEvent, Sort, SqlBuilder } from '../../types';
import { DesignerStates } from '../../container';

const template = require('./single-table-designer-layout.component.html');
require('./single-table-designer-layout.component.scss');

@Component({
  selector: 'single-table-designer-layout',
  template
})
export class SingleTableDesignerLayout {
  @Output() change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input() artifacts: Artifact[];
  @Input() data;
  @Input() auxSettings: any;
  @Input() analysisType: string;
  @Input() analysisSubtype: string;
  @Input() sorts: Sort[];
  @Input() sqlBuilder: SqlBuilder;
  @Input() designerState: DesignerStates;
}
