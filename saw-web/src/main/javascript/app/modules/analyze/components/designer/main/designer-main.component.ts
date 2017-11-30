import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as map from 'lodash/map';
import * as filter from 'lodash/filter';
import Analysis from '../../../models/analysis.model';
import ArtifactColumnPivot from '../../../models/artifact-column-pivot.model';
import DesignerService from '../designer.service';
import {
  IDEsignerSettingGroupAdapter,
  ArtifactColumn,
  ArtifactColumns,
  ArtifactColumnFilter
} from '../types';
import { TYPE_ICONS_OBJ } from '../../../consts';

const template = require('./designer-main.component.html');
require('./designer-main.component.scss');

@Component({
  selector: 'designer-main',
  template
})
export class DesignerMainComponent {
  @Output() public onSettingsChange: EventEmitter<ArtifactColumnPivot[]> = new EventEmitter();
  @Input() public artifactColumns: ArtifactColumns;

  constructor(private _designerService: DesignerService) {}

  onSuccessfulSelect() {
    this.onSettingsChange.emit();
  }
}
