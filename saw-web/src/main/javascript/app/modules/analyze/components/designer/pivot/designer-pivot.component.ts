import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import {Subject} from 'rxjs/Subject';

import {
  ArtifactColumns,
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
  @Input() artifactColumns: ArtifactColumns;
  @Input() data;
  @Input() sorts: any[];
  @Input() designerState: DesignerStates;

  public updater: Subject<IPivotGridUpdate> = new Subject();
  public DesignerStates = DesignerStates;

}
