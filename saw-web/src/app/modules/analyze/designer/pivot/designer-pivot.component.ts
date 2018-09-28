import { Component, Input } from '@angular/core';
import * as isEmpty from 'lodash/isEmpty';
import { Subject } from 'rxjs/Subject';

import { Artifact, ArtifactColumnPivot } from '../types';
import { DesignerStates } from '../consts';
import { IPivotGridUpdate } from '../../../../common/components/pivot-grid/pivot-grid.component';

const template = require('./designer-pivot.component.html');
const style = require('./designer-pivot.component.scss');

@Component({
  selector: 'designer-pivot',
  template,
  styles: [
    `:host {
      height: calc(100% - 20px);
      width: calc(100% - 20px);
    }`,
    style
  ]
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
