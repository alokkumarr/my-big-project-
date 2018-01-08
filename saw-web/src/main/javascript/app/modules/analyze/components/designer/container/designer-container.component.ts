import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as isEmpty from 'lodash/isEmpty';
import * as filter from 'lodash/filter';
import * as unset from 'lodash/unset';
import * as get from 'lodash/get';
import * as forEach from 'lodash/forEach';
import * as map from 'lodash/map';
import * as find from 'lodash/find';

import { DesignerService } from '../designer.service';
import {
  DesignerMode,
  AnalysisStarter,
  Analysis,
  AnalysisType,
  SqlBuilder,
  ArtifactColumns,
  DesignerToolbarAciton,
  Sort,
  Filter
} from '../types'

import { AnalyzeDialogService } from '../../../services/analyze-dialog.service'

const template = require('./designer-container.component.html');
require('./designer-container.component.scss');

export enum DesignerStates {
  NO_SELECTION,
  SELECTION_WAITING_FOR_DATA,
  SELECTION_WITH_NO_DATA,
  SELECTION_WITH_DATA,
  SELECTION_OUT_OF_SYNCH_WITH_DATA
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
  public designerState: DesignerStates;
  public DesignerStates = DesignerStates;
  public firstArtifactColumns: ArtifactColumns = [];
  public data: any = null;
  public sorts: Sort[] = [];
  public filters: Filter[] = [];
  public booleanCriteria: string = 'AND';

  constructor(
    private _designerService: DesignerService,
    private _analyzeDialogService: AnalyzeDialogService
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

  onToolbarAction(action: DesignerToolbarAciton) {
    switch (action) {
    case 'sort':
      this._analyzeDialogService.openSortDialog(this.sorts, this.firstArtifactColumns)
        .afterClosed().subscribe((result) => {
          if (result) {
            this.sorts = result.sorts;
          }
        });
      break;
    case 'filter':
      this._analyzeDialogService.openFilterDialog(this.filters, this.analysis.artifacts, this.booleanCriteria)
        .afterClosed().subscribe((result) => {
          if (result) {
            this.filters = result.filters;
            this.booleanCriteria = result.booleanCriteria;
          }
        });
      break;
    case 'preview':
      this.updateAnalysis();
      this._analyzeDialogService.openPreviewDialog(this.analysis);
      break;
    case 'description':
      this._analyzeDialogService.openDescriptionDialog(this.analysis.description)
        .afterClosed().subscribe((result) => {
          if (result) {
            this.analysis.description = result.description;
          }
        });
      break;
    case 'save':
      this._analyzeDialogService.openSaveDialog(this.analysis)
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
      sorts: this.sorts,
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
    this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
    this.firstArtifactColumns = this.getFirstArtifactColumns();
    this.cleanSorts();
  }

  getFirstArtifactColumns() {
    return [...this.analysis.artifacts[0].columns];
  }

  updateAnalysis() {
    this.analysis.sqlBuilder = this.getSqlBuilder();
  }

  /**
   * If an artifactColumn is unselected, it should be cleared out from the sorts.
   */
  cleanSorts() {
    const checkedFields = filter(this.firstArtifactColumns, 'checked');
    this.sorts = filter(this.sorts, sort => {
      return Boolean(find(checkedFields, ({columnName}) => columnName === sort.columnName));
    });
  }

  onSave() {
  }
}
