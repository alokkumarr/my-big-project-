declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as forEach from 'lodash/forEach';
import * as isEmpty from 'lodash/isEmpty';

import {
  Artifact
} from '../../types';

const template = require('./designer-settings-multi-table.component.html');
require('./designer-settings-multi-table.component.scss');

@Component({
  selector: 'designer-settings-multi-table',
  template
})
export class DesignerSettingsMultiTableComponent {
  @Input('artifacts') set setArtifacts(artifacts: Artifact[]) {
    this.artifacts = this.setDefaultArtifactPosition(artifacts);
  }
  @Input() data;
  public artifacts: Artifact[];

  setDefaultArtifactPosition(artifacts: Artifact[]) {
    // set the x, y coordiantes of the artifacts (tables in jsplumb)
    const defaultXPosition = 20;
    const defaultSpacing = 300;
    let xPosition = defaultXPosition;
    forEach(artifacts, (artifact: Artifact) => {
      if (isEmpty(artifact.artifactPosition)) {
        artifact.artifactPosition = [xPosition, 0];
        xPosition += defaultSpacing;
      }
    });
    return artifacts;
  }
}
