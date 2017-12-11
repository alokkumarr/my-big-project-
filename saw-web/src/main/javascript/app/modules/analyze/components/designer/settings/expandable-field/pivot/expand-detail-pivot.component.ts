import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import {
  ArtifactColumn,
  IDEsignerSettingGroupAdapter,
  AnalysisType
}  from '../../../types';
import { TYPE_ICONS_OBJ } from '../../../../../consts';

const template = require('./expand-detail.component.html');

@Component({
  selector: 'expand-detail-pivot',
  template
})
export class ExpandDetailPivotComponent {
  // @Output() public onSettingsChange: EventEmitter<ArtifactColumns[]> = new EventEmitter();

  @Input() public type: AnalysisType;
  @Input() public artifactColumn: ArtifactColumn;

}
