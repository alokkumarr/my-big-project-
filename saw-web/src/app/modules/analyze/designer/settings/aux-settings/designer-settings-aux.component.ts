import { Component, Output, EventEmitter, Input } from '@angular/core';
import { DesignerChangeEvent, Artifact } from '../../types';

@Component({
  selector: 'designer-settings-aux',
  templateUrl: 'designer-settings-aux.component.html'
})
export class DesignerSettingsAuxComponent {
  @Input() analysisType: string;
  @Input() analysisSubtype: string;
  @Input() auxSettings: any;
  @Input() chartTitle: string;
  @Input() artifacts: Artifact[];

  @Output() change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
}
