import { Component, Input } from '@angular/core';
import * as isEmpty from 'lodash/isEmpty';
import { Subject } from 'rxjs';

import { Artifact, ArtifactColumnPivot } from '../types';
import { DesignerStates } from '../consts';
import { IPivotGridUpdate } from '../../../../common/components/pivot-grid/pivot-grid.component';

@Component({
  selector: 'designer-pivot',
  templateUrl: './designer-pivot.component.html',
  styleUrls: ['./designer-pivot.component.scss']
})
export class DesignerPivotComponent {
  @Input('artifacts')
  set setArtifactColumns(artifacts: Artifact[]) {
    if (!isEmpty(artifacts)) {
      this.artifactColumns = [...artifacts[0].columns] as ArtifactColumnPivot[];
    }
  }
  @Input() data;
  @Input() sorts: any[];
  @Input() designerState: DesignerStates;

  public artifactColumns: ArtifactColumnPivot[];

  public updater: Subject<IPivotGridUpdate> = new Subject();
  public DesignerStates = DesignerStates;
}
