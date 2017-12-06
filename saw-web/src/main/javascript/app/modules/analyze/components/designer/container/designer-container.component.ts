import {
  Component,
  Input,
  Output,
  EventEmitter,
  ChangeDetectorRef
} from '@angular/core';
import * as isEmpty from 'lodash/isEmpty';
import * as filter from 'lodash/filter';
import * as unset from 'lodash/unset';
import * as get from 'lodash/get';

import { DesignerService } from '../designer.service';
import {
  DesignerMode,
  AnalysisStarter,
  Analysis,
  AnalysisType,
  SqlBuilder,
  ArtifactColumns
} from '../types'
import * as forEach from 'lodash/forEach';
import * as map from 'lodash/map';
import * as find from 'lodash/find';
const template = require('./designer-container.component.html');
require('./designer-container.component.scss');

export enum DesignerStates {
  NO_SELECTION,
  SELECTION_WAITING_FOR_DATA,
  SELECTION_WITH_NO_DATA,
  SELECTION_WITH_DATA
}

@Component({
  selector: 'designer-container',
  template
})
export class DesignerContainerComponent {
  @Input() public analysisStarter?: AnalysisStarter;
  @Input() public analysis?: Analysis;
  @Input() public designerMode: DesignerMode;
  @Output() public onBack: EventEmitter<any> = new EventEmitter();
  public isInDraftMode: boolean = false;
  public isDataOutOfSynch: boolean = false;
  public designerState: DesignerStates;
  public DesignerStates = DesignerStates;
  public firstArtifactColumns: ArtifactColumns = [];
  public data : any = null;

  constructor(
    private _designerService: DesignerService,
    private _changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit() {
    switch (this.designerMode) {
    case 'new':
      this.designerState = DesignerStates.NO_SELECTION;
      this.initNewAnalysis();
      break;
    case 'edit':
      this.designerState = DesignerStates.SELECTION_WAITING_FOR_DATA;
      break;

    default:
      break;
    }
  }

  initNewAnalysis() {
    const {type, semanticId} = this.analysisStarter;
    this._designerService.createAnalysis(semanticId, type)
      .then((newAnalysis: Analysis) => {
        this.analysis = {...this.analysisStarter, ...newAnalysis};
        unset(this.analysis, 'supports');
      });
  }

  requestData() {
    this.designerState = DesignerStates.SELECTION_WAITING_FOR_DATA;
    this.updateAnalysis();
    this._designerService.getDataForAnalysis(this.analysis)
      .then((data: any) => {
        if (this.isDataEmpty(data.data, this.analysis.type)) {
          this.designerState = DesignerStates.SELECTION_WITH_NO_DATA;
        } else {
          this.designerState = DesignerStates.SELECTION_WITH_DATA;
          this.isDataOutOfSynch = false;
          this.data = this.parseData(data.data, this.analysis.sqlBuilder);
        }
      }, err => {
        this.designerState = DesignerStates.SELECTION_WITH_NO_DATA;
      });
  }

  parseData(data, sqlBuilder) {
    return this._designerService.parseData(data, sqlBuilder);
  }

  getSqlBuilder(): SqlBuilder {
    const partialSqlBuilder = this._designerService.getPartialSqlBuilder(
      this.analysis.artifacts[0].columns,
      this.analysis.type
    )

    return {
      booleanCriteria: 'AND',
      filters: [],
      sorts: [],
      ...partialSqlBuilder
    }
  }

  isDataEmpty(data, type: AnalysisType) {
    switch (type) {
    case 'pivot':
      if (data.row_level_1) {
        return isEmpty(get(data ,'row_level_1.buckets'));
      }
      return isEmpty(get(data ,'column_level_1.buckets'));

    case 'chart':
      // TODO verify if the object returned is empty
    case 'report':
      return isEmpty(data);
    }
  }

  onSettingsChange() {
    this.isDataOutOfSynch = true;
    this.designerState = DesignerStates.SELECTION_WITH_NO_DATA;
    this._changeDetectorRef.detectChanges();
    this.firstArtifactColumns = filter(this.analysis.artifacts[0].columns, 'checked');
  }

  onMainSettingsChange(artifactColumns) {
    this.isDataOutOfSynch = true;
    this.designerState = DesignerStates.SELECTION_WITH_NO_DATA;
    this.analysis.artifacts[0].columns = this.mergeBackupColumnChanges(
      artifactColumns,
      this.analysis.artifacts[0].columns
    );
    this.firstArtifactColumns = this.analysis.artifacts[0].columns;
  }

  mergeBackupColumnChanges(backups, artifactColumns) {
    return map(artifactColumns, column => {
      const backup = find(backups, ({columnName}) => column.columnName === columnName);
      return backup || column;
    });
  }

  updateAnalysis() {
    this.analysis.sqlBuilder = this.getSqlBuilder();
  }

  onSave() {
  }
}
