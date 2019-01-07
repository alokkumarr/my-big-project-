import { Component, Input, Output, EventEmitter } from '@angular/core';
import * as get from 'lodash/get';

import {
  Artifact,
  DesignerChangeEvent,
  Sort,
  Filter,
  SqlBuilder,
} from '../../types';
import { DesignerStates, CHART_TYPES_OBJ } from '../../consts';

@Component({
  selector: 'single-table-designer-layout',
  templateUrl: './single-table-designer-layout.component.html',
  styleUrls: ['./single-table-designer-layout.component.scss']
})
export class SingleTableDesignerLayoutComponent {
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

  public DesignerStates = DesignerStates;

  onRemoveFilter(index) {
    this.filters.splice(index, 1);
    this.change.emit({ subject: 'filter' });
  }

  onRemoveFilterAll() {
    this.filters.splice(0, this.filters.length);
    this.change.emit({ subject: 'filter' });
  }

  getNonIdealStateIcon() {
    switch (this.analysisType) {
    case 'chart':
      const chartTypeObj = CHART_TYPES_OBJ['chart:' + this.analysisSubtype];
      return get(chartTypeObj, 'icon.font');
    case 'pivot':
      return 'icon-pivot';
    }
  }
}
