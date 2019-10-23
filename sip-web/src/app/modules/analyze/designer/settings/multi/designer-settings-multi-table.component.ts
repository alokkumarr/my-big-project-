import { Component, Input, Output, EventEmitter } from '@angular/core';
import * as forEach from 'lodash/forEach';
import * as isEmpty from 'lodash/isEmpty';
import * as flatMap from 'lodash/flatMap';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFilter from 'lodash/fp/filter';
import * as fpMap from 'lodash/fp/map';

import {
  Artifact,
  JsPlumbCanvasChangeEvent,
  DesignerChangeEvent
} from '../../types';
import { QueryDSL, Join } from 'src/app/models';

function getConditionOnSide(joinCondition, side: 'left' | 'right') {
  const { artifactsName, columnName } = joinCondition[side];
  return {
    tableName: artifactsName,
    columnName,
    side
  };
}

export function refactorJoins(joins) {
  const analysisJoins = fpPipe(
    fpFilter(join => !join.type),
    fpMap(join => {
      const criteria = flatMap(join.criteria, ({ joinCondition }) => [
        getConditionOnSide(joinCondition, 'left'),
        getConditionOnSide(joinCondition, 'right')
      ]);
      return {
        type: join.join,
        criteria
      };
    })
  )(joins);
  return !isEmpty(analysisJoins) ? analysisJoins : joins;
}

function setDefaultArtifactPosition(artifacts: Artifact[]) {
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

@Component({
  selector: 'designer-settings-multi-table',
  templateUrl: './designer-settings-multi-table.component.html',
  styleUrls: ['./designer-settings-multi-table.component.scss']
})
export class DesignerSettingsMultiTableComponent {
  @Output() change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input() useAggregate: boolean;
  @Input('sipQuery') set setJoins(sipQuery: QueryDSL) {
    this.joins = refactorJoins(sipQuery.joins);
  }
  @Input('artifacts')
  set setArtifacts(artifacts: Artifact[]) {
    this.artifacts = setDefaultArtifactPosition(artifacts);
  }
  @Input() data;
  public artifacts: Artifact[];

  public joins: Join[];

  onChange(event: JsPlumbCanvasChangeEvent) {
    this.change.emit(event);
  }
}
