import { Component, Input, Output, EventEmitter } from '@angular/core';
import * as isEmpty from 'lodash/isEmpty';
import {
  Artifact,
  DesignerChangeEvent,
  SqlBuilder,
  Sort,
  Analysis,
  Filter
} from '../../types';
import { DesignerStates } from '../../consts';

@Component({
  selector: 'multi-table-designer-layout',
  templateUrl: './multi-table-designer-layout.component.html',
  styleUrls: ['./multi-table-designer-layout.component.scss']
})
export class MultiTableDesignerLayoutComponent {
  @Output() change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input() artifacts: Artifact[];
  @Input() analysis: Analysis;
  @Input() sorts: Sort[];
  @Input() filters: Filter[];
  @Input() isInQueryMode: boolean;
  @Input() sqlBuilder: SqlBuilder;
  @Input() designerState: DesignerStates;
  @Input() dataCount: number;
  @Input('data')
  set setData(data) {
    if (!isEmpty(data)) {
      this.data = data;
      this.isGridPanelExpanded = true;
    }
  }
  public data;
  public isGridPanelExpanded = false;

  toggleGridPanel() {
    this.isGridPanelExpanded = !this.isGridPanelExpanded;
  }

  onQueryChange() {
    this.change.emit({ subject: 'changeQuery' });
  }

  onSaveQuery(evt) {
    this.change.emit({ subject: 'submitQuery' });
  }
}
