import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as map from 'lodash/map';
import * as isEmpty from 'lodash/isEmpty';
import * as forEach from 'lodash/forEach';
import * as clone from 'lodash/clone';
import * as split from 'lodash/split';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpPick from 'lodash/fp/pick';
import * as fpMap from 'lodash/fp/map';
import * as fpFilter from 'lodash/fp/filter';
import * as fpForEach from 'lodash/fp/forEach';
import * as moment from 'moment';
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
  @Input('artifactColumns') set setArtifactColumns(artifactColumns: ArtifactColumns) {
    this.artifactColumns = this.preProcessArtifactColumns(artifactColumns);
  };
  @Input('data') set setData(data) {
    this.data = this.preProcessData(data);
  };
  @Input() sorts: any[];
  @Input() designerState: DesignerStates;

  public artifactColumns: ArtifactColumns;
  public data;
  public updater: Subject<IPivotGridUpdate> = new Subject();
  public DesignerStates = DesignerStates;

  preProcessArtifactColumns(artifactColumns: ArtifactColumns) {
    return fpPipe(
      fpFilter('checked'),
      fpMap((column: ArtifactColumnPivot) => {

        if (DATE_TYPES.includes(column.type)) {
          const cloned = clone(column);
          if (['day', 'quarter', 'month'].includes(column.dateInterval)) {
            cloned.type = 'string';
          } else {
            cloned.groupInterval = cloned.dateInterval;
          }
          return cloned;
        }
        return column;
      })
    )(artifactColumns);
  }

  preProcessData(data) {
    const processedData = this.formatDates(data, this.artifactColumns);
    return processedData;
  }

  formatDates(data, fields: ArtifactColumns) {
    if (isEmpty(this.artifactColumns)) {
      return data;
    }

    const formattedData = map(data, dataPoint => {

      const clonedDataPoint = clone(dataPoint);
      fpPipe(
        fpFilter(({type}) => DATE_TYPES.includes(type)),
        fpForEach(({columnName, dateInterval}) => {
          const format = DATE_INTERVALS_OBJ[dateInterval].format;
          clonedDataPoint[columnName] = moment.utc(dataPoint[columnName]).format(format);
          if (dateInterval === 'quarter') {
            const parts = split(clonedDataPoint[columnName], '-');
            clonedDataPoint[columnName] = `${parts[0]}-Q${parts[1]}`;
          }
        })
      )(this.artifactColumns);
      return clonedDataPoint;
    });
    return formattedData;
  }
}
