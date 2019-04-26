import { Component, Input, Output, EventEmitter } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { PerfectScrollbarConfigInterface } from 'ngx-perfect-scrollbar';
import { BehaviorSubject } from 'rxjs';

import * as get from 'lodash/get';

import { Artifact, DesignerChangeEvent, Sort, Filter } from '../../types';
import { DesignerStates, CHART_TYPES_OBJ } from '../../consts';
import { IPivotGridUpdate } from '../../../../../common/components/pivot-grid/pivot-grid.component';
import { QueryDSL, SqlBuilderChart } from 'src/app/models';

// the delay needed to animate opening and closing the sidemenus
const SIDEMENU_ANIMATION_TIME = 300;

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
  @Input() sipQuery: QueryDSL;
  @Input() sqlBuilder: SqlBuilderChart;
  @Input() designerState: DesignerStates;
  @Input() chartTitle: string;

  public DesignerStates = DesignerStates;
  public isOptionsPanelOpen = false;
  public isFieldsPanelOpen = true;
  public optionsPanelMode: 'side' | 'over' = 'side';
  private isInTabletMode = false;
  public chartUpdater: BehaviorSubject<[] | {}> = new BehaviorSubject([]);
  public pivotUpdater: BehaviorSubject<IPivotGridUpdate> = new BehaviorSubject(
    {}
  );
  public config: PerfectScrollbarConfigInterface = {};

  constructor(breakpointObserver: BreakpointObserver) {
    breakpointObserver
      .observe([Breakpoints.Medium, Breakpoints.Small])
      .subscribe(result => {
        this.isInTabletMode = result.matches;
        if (result.matches) {
          this.isOptionsPanelOpen = false;
          this.optionsPanelMode = 'over';
        } else {
          this.isOptionsPanelOpen = true;
          this.optionsPanelMode = 'side';
        }
      });
  }

  onClickedOutsideOptionsPanel(drawer) {
    if (this.isInTabletMode && this.isOptionsPanelOpen) {
      drawer.close();
      this.isOptionsPanelOpen = false;
    }
  }

  toggleFieldsDrawer(drawer) {
    drawer.toggle();
    this.isFieldsPanelOpen = !this.isFieldsPanelOpen;
    setTimeout(() => {
      this.rePaintAnalysis(this.analysisType);
    }, SIDEMENU_ANIMATION_TIME);
  }

  openDrawer(drawer) {
    if (this.isInTabletMode) {
      setTimeout(() => {
        drawer.toggle();
        this.isOptionsPanelOpen = !this.isOptionsPanelOpen;
      });
    } else {
      drawer.toggle();
      this.isOptionsPanelOpen = !this.isOptionsPanelOpen;
    }
    setTimeout(() => {
      this.rePaintAnalysis(this.analysisType);
    }, SIDEMENU_ANIMATION_TIME);
  }

  rePaintAnalysis(type) {
    if (type === 'pivot') {
      this.pivotUpdater.next({ rePaint: true });
    } else {
      this.chartUpdater.next({ reflow: true });
    }
  }

  onRemoveFilter(index) {
    this.filters.splice(index, 1);
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
