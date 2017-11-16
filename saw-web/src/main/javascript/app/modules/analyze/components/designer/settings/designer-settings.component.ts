import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import map from 'lodash/map';
import Analysis from '../../../models/analysis.model';
import ArtifactColumnPivot from '../../../models/artifact-column-pivot.model';
import DesignerSettingsService from './designer-settings.service';
import {
  IDEsignerSettingGroupAdapter,
  ArtifactColumn
} from './types';

const template = require('./designer-settings.component.html');
require('./designer-settings.component.scss');

@Component({
  selector: 'designer-settings',
  template
})
export default class DesignerSettingsComponent {
  @Output() public onSettingsChange: EventEmitter<ArtifactColumnPivot[]> = new EventEmitter();
  @Input() public artifactColumns: ArtifactColumnPivot[];

  public groupAdapters: IDEsignerSettingGroupAdapter[];

  constructor(private _designerSettingsService: DesignerSettingsService) {}

  ngOnInit() {
    this.groupAdapters = this._designerSettingsService.getPivotGroupAdapters();
    console.log('artifactColumns', this.artifactColumns);
  }

  distributeArtifactColumnsIntoGroups() {

  }

  onSuccessfulSelect() {
    this.onSettingsChange.emit();
  }

  putInGroupIfPossible(artifactColumn: ArtifactColumn) {

  }

}
