import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as map from 'lodash/map';
import * as get from 'lodash/get';
import * as filter from 'lodash/filter';
import * as isEmpty from 'lodash/isEmpty';
import * as find from 'lodash/find';
import * as has from 'lodash/has';
import * as forEach from 'lodash/forEach';
import * as cloneDeep from 'lodash/cloneDeep';
import * as groupBy from 'lodash/groupBy';
import * as values from 'lodash/values';
import {Subject} from 'rxjs/Subject';

import {
  IDEsignerSettingGroupAdapter,
  ArtifactColumn,
  ArtifactColumns,
  ArtifactColumnFilter,
  Analysis,
  ArtifactColumnPivot
} from '../types';
import { DesignerStates } from '../container';
import { IPivotGridUpdate } from '../../../../../common/components/pivot-grid/pivot-grid.component';
import { SettingsValidationService } from '../settings-validation.service';
import {
  TYPE_ICONS_OBJ,
  DATE_TYPES,
  NUMBER_TYPES,
  DEFAULT_DATE_INTERVAL,
  DEFAULT_AGGREGATE_TYPE
} from '../../../consts';

const template = require('./designer-pivot.component.html');
require('./designer-pivot.component.scss');

@Component({
  selector: 'designer-pivot',
  template
})
export class DesignerPivotComponent {
  @Output() onSettingsChange: EventEmitter<ArtifactColumnPivot[]> = new EventEmitter();
  @Input() artifactColumns: ArtifactColumns;
  @Input() data: any;
  @Input() designerState: DesignerStates;
  @Input() isDataOutOfSynch: boolean;
  @Input() backupColumns: any;

  public changeFromPivotGrid = false;

  public updater: Subject<IPivotGridUpdate> = new Subject();
  public DesignerStates = DesignerStates;

  constructor(
    private _settingsValidationService: SettingsValidationService
  ) {}

  ngOnInit() {
    this.changeFromPivotGrid = false;
  }

  ngOnChanges(changes) {
    if (changes.artifactColumns || changes.data) {
      this.changeFromPivotGrid = false;
    }
  }

  onContentReady({fields}) {
    if (isEmpty(this.artifactColumns) || isEmpty(fields)) {
      return;
    }

    if (this.changeFromPivotGrid) {
      forEach(this.artifactColumns, artifactColumn => {
        const targetField = find(fields, ({dataField}) => {
          if (artifactColumn.type === 'string') {
            return dataField === artifactColumn.columnName.split('.')[0];
          }
          return dataField === artifactColumn.columnName;
        });
        artifactColumn.areaIndex = targetField.areaIndex;
        artifactColumn.area = targetField.area;
        this.applyDefaultsBasedOnAreaChange(artifactColumn);
      });

      if (this._settingsValidationService.checkValidPivotStates(this.artifactColumns)) {
        this.backupColumns = cloneDeep(this.artifactColumns);
      } else if (!isEmpty(this.backupColumns)) {
        this.onSettingsChange.emit(this.backupColumns);
      }
    }
    this.changeFromPivotGrid = true;
  }

  applyDefaultsBasedOnAreaChange(artifactColumn) {
    if (DATE_TYPES.includes(artifactColumn.type) &&
        !has(artifactColumn, 'dateInterval')) {

      artifactColumn.dateInterval = DEFAULT_DATE_INTERVAL.value;
    }
    if (artifactColumn.area === 'data' &&
        NUMBER_TYPES.includes(artifactColumn.type) &&
        !artifactColumn.aggregate) {
      artifactColumn.aggregate = DEFAULT_AGGREGATE_TYPE.value;
    }
  }

}
