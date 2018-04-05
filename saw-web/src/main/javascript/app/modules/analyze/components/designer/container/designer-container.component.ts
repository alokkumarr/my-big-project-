declare const require: any;
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
import * as isNumber from 'lodash/isNumber';
import * as forEach from 'lodash/forEach';
import * as find from 'lodash/find';

import { DesignerService } from '../designer.service';
import {
  DesignerMode,
  AnalysisStarter,
  Analysis,
  AnalysisType,
  SqlBuilder,
  SqlBuilderReport,
  SqlBuilderPivot,
  ArtifactColumns,
  Artifact,
  DesignerToolbarAciton,
  Sort,
  Filter,
  IToolbarActionResult,
  DesignerChangeEvent
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
  @Output() public onSave: EventEmitter<boolean> = new EventEmitter();
  public isInDraftMode: boolean = false;
  public designerState: DesignerStates;
  public DesignerStates = DesignerStates;
  public artifacts: Artifact[] = [];
  public data: any = null;
  public sorts: Sort[] = [];
  public filters: Filter[] = [];
  public booleanCriteria: string = 'AND';
  public layoutConfiguration: 'single' | 'multi';

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
      this.initExistingAnalysis();
      this.requestDataIfPossible();
      break;
    case 'fork':
      this.forkAnalysis().then(() => {
        this.initExistingAnalysis();
        this.requestDataIfPossible();
      });
    default:
      break;
    }
    this.layoutConfiguration = this.getLayoutConfiguration(
      this.analysisStarter || this.analysis
    );
  }

  getLayoutConfiguration(analysis: Analysis | AnalysisStarter) {
    switch (analysis.type) {
    case 'report':
    case 'esReport':
      return 'multi';
    case 'pivot':
    case 'chart':
    default:
      return 'single';
    }
  }

  initNewAnalysis() {
    const {type, semanticId} = this.analysisStarter;
    this._designerService.createAnalysis(semanticId, type)
      .then((newAnalysis: Analysis) => {
        this.analysis = {...this.analysisStarter, ...newAnalysis};
        if (!this.analysis.sqlBuilder) {
          this.analysis.sqlBuilder = {
            joins: []
          };
        }
        this.artifacts = this.analysis.artifacts;
        unset(this.analysis, 'supports');
        unset(this.analysis, 'categoryId');
      });
  }

  initExistingAnalysis() {
    this.artifacts = this.analysis.artifacts;
    this.filters = this.analysis.sqlBuilder.filters;
    this.sorts = this.analysis.sqlBuilder.sorts;
    this.booleanCriteria = this.analysis.sqlBuilder.booleanCriteria;
  }

  forkAnalysis() {
    const {type, semanticId} = this.analysis;
    return this._designerService.createAnalysis(semanticId, type)
      .then((newAnalysis: Analysis) => {
        this.analysis = {...this.analysis, ...{
          id: newAnalysis.id,
          metric: newAnalysis.metric,
          createdTimestamp: newAnalysis.createdTimestamp,
          userId: newAnalysis.userId,
          userFullName: newAnalysis.userFullName,
          metricName: newAnalysis.metricName
        }};
      });
  }

  requestDataIfPossible() {
    this.updateAnalysis();
    if (this.canRequestData()) {
      this.requestData();
    } else {
      this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
    }
  }

  requestData() {
    this.designerState = DesignerStates.SELECTION_WAITING_FOR_DATA;
    this._designerService.getDataForAnalysis(this.analysis)
      .then((response) => {
        if (this.isDataEmpty(response.data, this.analysis.type, this.analysis.sqlBuilder)) {
          this.designerState = DesignerStates.SELECTION_WITH_NO_DATA;
        } else {
          this.designerState = DesignerStates.SELECTION_WITH_DATA;
          this.data = this.parseData(response.data, this.analysis);
        }
      }, err => {
        this.designerState = DesignerStates.SELECTION_WITH_NO_DATA;
      });
  }

  parseData(data, analysis: Analysis) {
    switch (analysis.type) {
    case 'pivot':
      return this._designerService.parseData(data, analysis.sqlBuilder);
    case 'report':
    case 'esReport':
      return data;
    case 'chart':
      break;
    }
  }

  onToolbarAction(action: DesignerToolbarAciton) {
    switch (action) {
    case 'sort':
    // TODO update sorts for multiple artifacts
      const firstArtifactCols = this.artifacts[0].columns;
      this._analyzeDialogService.openSortDialog(this.sorts, firstArtifactCols)
        .afterClosed().subscribe((result: IToolbarActionResult) => {
          if (result) {
            this.sorts = result.sorts;
            this.onSettingsChange({subject: 'sort'});
          }
        });
      break;
    case 'filter':
      this._analyzeDialogService.openFilterDialog(this.filters, this.artifacts, this.booleanCriteria)
        .afterClosed().subscribe((result: IToolbarActionResult) => {
          if (result) {
            this.filters = result.filters;
            this.booleanCriteria = result.booleanCriteria;
            this.onSettingsChange({subject: 'filter'});
          }
        });
      break;
    case 'preview':
      this._analyzeDialogService.openPreviewDialog(this.analysis);
      break;
    case 'description':
      this._analyzeDialogService.openDescriptionDialog(this.analysis.description)
        .afterClosed().subscribe((result: IToolbarActionResult) => {
          if (result) {
            this.analysis.description = result.description;
          }
        });
      break;
    case 'save':
      this._analyzeDialogService.openSaveDialog(this.analysis)
      .afterClosed().subscribe((result: IToolbarActionResult) => {
        if (result) {
          this.onSave.emit(result.isSaveSuccessful);
          this.isInDraftMode = false;
        }
      });
      break;
    case 'refresh':
      this.requestDataIfPossible();
      break;
    }
  }

  getSqlBuilder(): SqlBuilder {
    let partialSqlBuilder;

    switch (this.analysis.type) {
    case 'pivot':
      partialSqlBuilder = this._designerService.getPartialPivotSqlBuilder(this.artifacts[0].columns);
      break;
    case 'esReport':
      partialSqlBuilder = this._designerService.getPartialEsReportSqlBuilder(this.artifacts[0].columns);
      break;
    case 'report':
      partialSqlBuilder = {
        joins: (<SqlBuilderReport>this.analysis.sqlBuilder).joins
      };
      break;
    }

    return {
      booleanCriteria: this.booleanCriteria,
      filters: this.filters,
      sorts: this.sorts,
      ...partialSqlBuilder
    }
  }

  isDataEmpty(data, type: AnalysisType, sqlBuilder: SqlBuilder) {
    switch (type) {
    case 'pivot':
      let isDataEmpty = false;
      if (data.row_level_1) {
        isDataEmpty = isEmpty(get(data ,'row_level_1.buckets'));
      } else if (data.column_level_1) {
        isDataEmpty = isEmpty(get(data ,'column_level_1.buckets'));
      } else {
        // when no row or column fields are selected
        forEach((<SqlBuilderPivot>sqlBuilder).dataFields, ({columnName}) => {
          isDataEmpty = isEmpty(get(data , columnName));
          if (isDataEmpty) {
            return false;
          }
        });
      }
      return isDataEmpty;
    case 'chart':
      // TODO verify if the object returned is empty
    case 'report':
    case 'esReport':
      return isEmpty(data);
    }
  }

  onSettingsChange(event: DesignerChangeEvent) {
    switch (this.analysis.type) {
    case 'report':
    case 'esReport':
      this.handleReportChangeEvents(event);
      break;
    case 'pivot':
    case 'chart':
      this.handleOtherChangeEvents(event);
      break;
    }
    this.isInDraftMode = true;
  }

  handleReportChangeEvents(event: DesignerChangeEvent) {
    switch (event.subject) {
    // backend data refresh needed
    case 'column':
      this.cleanSorts();
      this.resetColumnPropsToDefaultIfNeeded(event.column);
      this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
      break;
    case 'aggregate':
    case 'filter':
    case 'joins':
      this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
      break;
    // only front end data refresh needed
    case 'format':
    case 'aliasName':
    case 'sort':
      this.artifacts = [...this.artifacts];
      break;
    case 'artifactPosition':
    case 'visibleIndex':
    }
  }

  resetColumnPropsToDefaultIfNeeded(column) {
    column.visible = true;
    unset(column, 'aggregate');
  }

  handleOtherChangeEvents(event: DesignerChangeEvent) {
    switch (event.subject) {
    case 'selectedFields':
      this.cleanSorts();
      this.requestDataIfPossible();
      break;
    case 'dateInterval':
    case 'aggregate':
    case 'filter':
      this.requestDataIfPossible();
      break;
    case 'format':
    case 'aliasName':
    case 'sort':
      // reload frontEnd
      this.artifacts = [...this.artifacts];
      break;
    }
  }

  canRequestData() {
    // there has to be at least 1 data field, to make a request
    switch (this.analysis.type) {
    case 'pivot':
      const length = get(this.analysis, 'sqlBuilder.dataFields.length');
      return isNumber(length) ? length > 0 : false;
    case 'chart':
    case 'report':
    case 'esReport':
      return true;
    }

  }

  updateAnalysis() {
    this.analysis.sqlBuilder = this.getSqlBuilder();
  }

  /**
   * If an artifactColumn is unselected, it should be cleared out from the sorts.
   */
  cleanSorts() {
    if (isEmpty(this.artifacts)) {
      return;
    }
    const firstArtifactCols = this.artifacts[0].columns;
    // TODO update sorts for multiple artifacts
    const checkedFields = filter(firstArtifactCols, 'checked');
    this.sorts = filter(this.sorts, sort => {
      return Boolean(find(checkedFields, ({columnName}) => columnName === sort.columnName));
    });
  }
}
