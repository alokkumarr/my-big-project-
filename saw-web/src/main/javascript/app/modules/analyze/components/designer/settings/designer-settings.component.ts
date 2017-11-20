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

const template = require('./designer-settings.component.html');
require('./designer-settings.component.scss');

@Component({
  selector: 'designer-settings',
  template
})
export default class DesignerSettingsComponent {
  @Output() public onSettingsChange: EventEmitter<ArtifactColumnPivot[]> = new EventEmitter();
  @Input() public artifactColumns: ArtifactColumns;

  public unselectedArtifactColumns: ArtifactColumns
  public TYPE_ICONS_OBJ = TYPE_ICONS_OBJ;
  public isUnselectedExpanded: boolean = false;
  public groupAdapters: IDEsignerSettingGroupAdapter[];
  public filterObj: ArtifactColumnFilter = {
    keyword: '',
    type: ''
  };

  constructor(private _designerService: DesignerService) {}

  ngOnInit() {
    this.groupAdapters = this._designerService.getPivotGroupAdapters(this.artifactColumns);
    console.log('artifactColumns', this.artifactColumns);
    console.log('unselectedArtifactColumns', this.unselectedArtifactColumns);
  }

  ngOnChanges() {
    this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
  }

  hideUnselectedSection() {
    this.isUnselectedExpanded = false;
  }

  expandUnselectedSection() {
    this.isUnselectedExpanded = true;
  }

  getUnselectedArtifactColumns() {
    return filter(this.artifactColumns, ({checked}) => !checked);
  }

  onSuccessfulSelect() {
    this.onSettingsChange.emit();
  }

  addToGroupIfPossible(artifactColumn: ArtifactColumn) {
    console.log('selected', artifactColumn);
    this._designerService.addArtifactColumnIntoGroup(
      artifactColumn,
      this.groupAdapters
    );
    this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
  }

}
