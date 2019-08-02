import { Component, Input, Output, EventEmitter } from '@angular/core';
import * as forEach from 'lodash/forEach';
import * as isEmpty from 'lodash/isEmpty';

import {
  Artifact,
  SqlBuilderReport,
  JsPlumbCanvasChangeEvent,
  DesignerChangeEvent
} from '../../types';
import { QueryDSL } from 'src/app/models';

@Component({
  selector: 'designer-settings-multi-table',
  templateUrl: './designer-settings-multi-table.component.html',
  styleUrls: ['./designer-settings-multi-table.component.scss']
})
export class DesignerSettingsMultiTableComponent {
  @Output() change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input() useAggregate: boolean;
  @Input('artifacts')
  set setArtifacts(artifacts: Artifact[]) {
    this.artifacts = this.setDefaultArtifactPosition(artifacts);
  }
  @Input() data;
  @Input() sqlBuilder: QueryDSL | SqlBuilderReport;
  public artifacts: Artifact[];

  onChange(event: JsPlumbCanvasChangeEvent) {
    this.change.emit(event);
  }

  setDefaultArtifactPosition(artifacts: Artifact[]) {
    // set the x, y coordiantes of the artifacts (tables in jsplumb)
    const defaultXPosition = 20;
    const defaultSpacing = 400;
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
