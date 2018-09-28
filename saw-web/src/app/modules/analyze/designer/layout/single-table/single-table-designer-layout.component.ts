import { Component, Input, Output, EventEmitter } from '@angular/core';

import {
  Artifact,
  DesignerChangeEvent,
  Sort,
  Filter,
  SqlBuilder
} from '../../types';
import { DesignerStates } from '../../consts';

const template = require('./single-table-designer-layout.component.html');
const style = require('./single-table-designer-layout.component.scss');

@Component({
  selector: 'single-table-designer-layout',
  template,
  styles: [
    `:host {
      background-color: white;
      max-height: 93vh;
      max-width: 100vw;
      display: grid;
      grid-template:
        "data-settings content" 93vh
        / 25vw 75vw;
    }`,
    style
  ]
})
export class SingleTableDesignerLayout {
  @Output() change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input() artifacts: Artifact[];
  @Input() data;
  @Input() auxSettings: any;
  @Input() analysisType: string;
  @Input() analysisSubtype: string;
  @Input() sorts: Sort[];
  @Input() filters: Filter[];
  @Input() sqlBuilder: SqlBuilder;
  @Input() designerState: DesignerStates;
  @Input() chartTitle: string;
  @Input() fieldCount: number;

  onRemoveFilter(index) {
    this.filters.splice(index, 1);
    this.change.emit({ subject: 'filter' });
  }

  onRemoveFilterAll() {
    this.filters.splice(0, this.filters.length);
    this.change.emit({ subject: 'filter' });
  }
}
