import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Analysis, DesignerToolbarAciton } from '../types';
import { DesignerStates } from '../consts';
import { DesignerChangeEvent } from '../types';
import { HeaderProgressService } from '../../../../common/services';

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
  @Input() public analysis: Analysis;
  @Input() public isInDraftMode: boolean;
  @Input() public isInQueryMode: boolean;
  @Input() public designerState: DesignerStates;
  @Input() public areMinRequirmentsMet: boolean;

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
