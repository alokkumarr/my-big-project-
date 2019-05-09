import { Component, Input } from '@angular/core';
import * as flatMap from 'lodash/flatMap';
import { BehaviorSubject } from 'rxjs';

import { Artifact, ArtifactColumnPivot } from '../types';
import { IPivotGridUpdate } from '../../../../common/components/pivot-grid/pivot-grid.component';

@Component({
  selector: 'designer-pivot',
  templateUrl: './designer-pivot.component.html',
  styleUrls: ['./designer-pivot.component.scss']
})
export class DesignerPivotComponent {
  @Input('artifacts')
  set setArtifactColumns(artifacts: Artifact[]) {
    this.artifactColumns = flatMap(artifacts, artifact => artifact.fields);
  }
  @Input() data;
  @Input() sorts: any[];
  @Input() updater: BehaviorSubject<IPivotGridUpdate>;

  public artifactColumns: ArtifactColumnPivot[];
}
