declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as isEmpty from 'lodash/isEmpty';
import {Subject} from 'rxjs/Subject';

import {
  Artifact,
  ArtifactColumnPivot
} from '../types';
import { DesignerStates } from '../container';
import { IPivotGridUpdate } from '../../../../../common/components/pivot-grid/pivot-grid.component';
import {
  DATE_TYPES,
  DATE_INTERVALS_OBJ
} from '../../../consts';

const template = require('./designer-pivot.component.html');
require('./designer-pivot.component.scss');

@Component({
  selector: 'designer-pivot',
  template
})
export class DesignerPivotComponent {
  @Input('artifacts') set setArtifactColumns(artifacts: Artifact[]) {
    if (!isEmpty(artifacts)) {
      this.artifactColumns = [...artifacts[0].columns];
    }
  };
  @Input() data;
  @Input() sorts: any[];
  @Input() designerState: DesignerStates;

  public artifactColumns: ArtifactColumnPivot[];

  public updater: Subject<IPivotGridUpdate> = new Subject();
  public DesignerStates = DesignerStates;

}
