import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Select } from '@ngxs/store';
import { Observable } from 'rxjs';
import { AnalysisDSL, DesignerToolbarAciton } from '../types';
import { DesignerStates } from '../consts';
import { DesignerChangeEvent } from '../types';
import { HeaderProgressService } from '../../../../common/services';
import { DesignerState } from '../state/designer.state';

@Component({
  selector: 'designer-header',
  templateUrl: './designer-header.component.html',
  styleUrls: ['./designer-header.component.scss']
})
export class DesignerHeaderComponent {
  @Output() public onBack: EventEmitter<null> = new EventEmitter();
  @Output()
  requestAction: EventEmitter<DesignerToolbarAciton> = new EventEmitter();
  @Output() public change: EventEmitter<
    DesignerChangeEvent
  > = new EventEmitter();
  @Input() public analysis: AnalysisDSL;
  @Input() public isInDraftMode: boolean;
  @Input() public isInQueryMode: boolean;
  @Input() public designerState: DesignerStates;
  @Input() public areMinRequirmentsMet: boolean;

  @Select(DesignerState.metricName)
  metricName$: Observable<string>;
  @Select(DesignerState.isDataTooMuchForChart)
  isDataTooMuchForChart$: Observable<Boolean>;

  public DesignerStates = DesignerStates;
  public progressSub;
  public showProgress = false;

  constructor(public _headerProgress: HeaderProgressService) {
    this.progressSub = _headerProgress.subscribe(showProgress => {
      this.showProgress = showProgress;
    });
  }

  onNgDestroy() {
    if (this.progressSub) {
      this.progressSub.unsubscribe();
    }
  }

  onChartTypeChange(chartType) {
    this.change.emit({
      subject: 'chartType',
      data: chartType
    });
  }
}
