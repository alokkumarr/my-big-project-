declare const require: any;
import { Component, Input, Output, EventEmitter } from '@angular/core';
import * as isEmpty from 'lodash/isEmpty';
import * as filter from 'lodash/filter';
import * as unset from 'lodash/unset';
import * as get from 'lodash/get';
import * as isNumber from 'lodash/isNumber';
import * as forEach from 'lodash/forEach';
import * as find from 'lodash/find';
import * as orderBy from 'lodash/orderBy';
import * as map from 'lodash/map';

import { DesignerService } from '../designer.service';
import {
  DesignerMode,
  AnalysisStarter,
  Analysis,
  AnalysisType,
  SqlBuilder,
  SqlBuilderPivot,
  SqlBuilderChart,
  ArtifactColumns,
  DesignerToolbarAciton,
  Sort,
  Filter,
  IToolbarActionResult
} from '../types';
import { AnalyzeDialogService } from '../../../services/analyze-dialog.service';
import { ChartService } from '../../../services/chart.service';
import { FieldChangeEvent } from '../settings/single';

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
  public firstArtifactColumns: ArtifactColumns = [];
  public data: any = null;
  public auxSettings: any = {};
  public sorts: Sort[] = [];
  public filters: Filter[] = [];
  public booleanCriteria: string = 'AND';

  constructor(
    private _designerService: DesignerService,
    private _analyzeDialogService: AnalyzeDialogService,
    private _chartService: ChartService
  ) {}

  ngOnInit() {
    /* prettier-ignore */
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
  }

  initNewAnalysis() {
    const { type, semanticId } = this.analysisStarter;
    this._designerService
      .createAnalysis(semanticId, type)
      .then((newAnalysis: Analysis) => {
        this.analysis = { ...this.analysisStarter, ...newAnalysis };
        unset(this.analysis, 'supports');
        unset(this.analysis, 'categoryId');
      });
  }

  initExistingAnalysis() {
    this.firstArtifactColumns = this.getFirstArtifactColumns();
    this.filters = this.analysis.sqlBuilder.filters;
    this.sorts = this.analysis.sqlBuilder.sorts;

    /* prettier-ignore */
    this.auxSettings = {
      ...this.auxSettings,
      ...(this.analysis.type === 'chart' ? {
        legend: (<any>this.analysis).legend,
        isInverted: (<any>this.analysis).isInverted
      } : {})
    };

    this.addDefaultSorts();
    this.booleanCriteria = this.analysis.sqlBuilder.booleanCriteria;
  }

  addDefaultSorts() {
    if ((this.analysisStarter || this.analysis).type !== 'chart') return;

    const sqlBuilder = this.getSqlBuilder() as SqlBuilderChart;

    if (isEmpty(this.sorts)) {
      forEach(sqlBuilder.nodeFields || [], node => {
        this.sorts.push({
          order: 'asc',
          columnName: node.columnName,
          type: node.type
        });
      });
    }
  }

  forkAnalysis() {
    const { type, semanticId } = this.analysis;
    return this._designerService
      .createAnalysis(semanticId, type)
      .then((newAnalysis: Analysis) => {
        this.analysis = {
          ...this.analysis,
          ...{
            id: newAnalysis.id,
            metric: newAnalysis.metric,
            createdTimestamp: newAnalysis.createdTimestamp,
            userId: newAnalysis.userId,
            userFullName: newAnalysis.userFullName,
            metricName: newAnalysis.metricName
          }
        };
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
    this._designerService.getDataForAnalysis(this.analysis).then(
      (data: any) => {
        if (
          this.isDataEmpty(
            data.data,
            this.analysis.type,
            this.analysis.sqlBuilder
          )
        ) {
          this.designerState = DesignerStates.SELECTION_WITH_NO_DATA;
        } else {
          this.designerState = DesignerStates.SELECTION_WITH_DATA;
          this.parseData(data.data, this.analysis.sqlBuilder);
        }
      },
      err => {
        this.designerState = DesignerStates.SELECTION_WITH_NO_DATA;
      }
    );
  }

  parseData(data, sqlBuilder: SqlBuilder) {
    /* prettier-ignore */
    switch(this.analysis.type) {
    case 'pivot':
      this.data = this._designerService.parseData(
        data,
        sqlBuilder
      );
      break;
    case 'chart':
      let chartData = this._chartService.parseData(
        data,
        sqlBuilder
      );

      /* Order chart data manually. Backend doesn't sort chart data. */
      if (!isEmpty(this.sorts)) {
        chartData = orderBy(
          chartData,
          map(this.sorts, 'columnName'),
          map(this.sorts, 'order')
        );
      }

      this.data = chartData;
      break;
    case 'report':
    default:
      this.data = data;
    }
  }

  onToolbarAction(action: DesignerToolbarAciton) {
    /* prettier-ignore */
    switch (action) {
    case 'sort':
      this._analyzeDialogService
        .openSortDialog(this.sorts, this.firstArtifactColumns)
        .afterClosed()
        .subscribe((result: IToolbarActionResult) => {
          if (result) {
            this.sorts = result.sorts;
            this.onSettingsChange({ requiresDataChange: true });
          }
        });
      break;
    case 'filter':
      this._analyzeDialogService
        .openFilterDialog(
          this.filters,
          this.analysis.artifacts,
          this.booleanCriteria,
          (this.analysis || this.analysisStarter).type === 'chart' // supports global filters?
        )
        .afterClosed()
        .subscribe((result: IToolbarActionResult) => {
          if (result) {
            this.filters = result.filters;
            this.booleanCriteria = result.booleanCriteria;
            this.onSettingsChange({ requiresDataChange: true });
          }
        });
      break;
    case 'preview':
      this._analyzeDialogService.openPreviewDialog(this.analysis);
      break;
    case 'description':
      this._analyzeDialogService
        .openDescriptionDialog(this.analysis.description)
        .afterClosed()
        .subscribe((result: IToolbarActionResult) => {
          if (result) {
            this.analysis.description = result.description;
          }
        });
      break;
    case 'save':
      this._analyzeDialogService
        .openSaveDialog(this.analysis)
        .afterClosed()
        .subscribe((result: IToolbarActionResult) => {
          if (result) {
            this.onSave.emit(result.isSaveSuccessful);
          }
        });
      break;
    }
  }

  getSqlBuilder(): SqlBuilder {
    const partialSqlBuilder = this._designerService.getPartialSqlBuilder(
      this.analysis.artifacts[0].columns,
      this.analysis.type
    );

    const sqlBuilder = {
      booleanCriteria: this.booleanCriteria,
      filters: this.filters,
      sorts: this.sorts,
      ...partialSqlBuilder
    };

    return sqlBuilder;
  }

  isDataEmpty(data, type: AnalysisType, sqlBuilder: SqlBuilder) {
    /* prettier-ignore */
    switch (type) {
    case 'pivot':
      let isDataEmpty = false;
      if (data.row_level_1) {
        isDataEmpty = isEmpty(get(data, 'row_level_1.buckets'));
      } else if (data.column_level_1) {
        isDataEmpty = isEmpty(get(data, 'column_level_1.buckets'));
      } else {
        // when no row or column fields are selected
        forEach(
          (<SqlBuilderPivot>sqlBuilder).dataFields,
          ({ columnName }) => {
            isDataEmpty = isEmpty(get(data, columnName));
            if (isDataEmpty) {
              return false;
            }
          }
        );
      }
      return isDataEmpty;
    case 'chart':
      const parsedData = this._chartService.parseData(data, sqlBuilder);
      return isEmpty(parsedData);
    // TODO verify if the object returned is empty
    case 'report':
      return isEmpty(data);
    }
  }

  onSettingsChange(event: FieldChangeEvent) {
    this.firstArtifactColumns = this.getFirstArtifactColumns();
    this.cleanSorts();
    this.addDefaultSorts();
    if (event.requiresDataChange) {
      this.requestDataIfPossible();
    } else {
      this.updateAnalysis();
      this.data = this.data ? [...this.data] : [];
    }
  }

  onAuxSettingsChange(event) {
    this.analysis = { ...this.analysis, ...event };
    this.auxSettings = { ...this.auxSettings, ...event };
    this.data = this.data ? [...this.data] : [];
  }

  canRequestData() {
    // there has to be at least 1 data field, to make a request
    /* prettier-ignore */
    switch (this.analysis.type) {
    case 'pivot':
      const length = get(this.analysis, 'sqlBuilder.dataFields.length');
      return isNumber(length) ? length > 0 : false;
    case 'chart':
      const dataLength = get(this.analysis, 'sqlBuilder.dataFields.length', 0);
      const nonDataLength = get(this.analysis, 'sqlBuilder.nodeFields.length', 0);
      return dataLength && nonDataLength;
    case 'report':
      return false;
    }
  }

  getFirstArtifactColumns() {
    const indices = {};
    return map(get(this.analysis, 'artifacts.0.columns') || [], column => {
      if (column.checked && column.checked !== true) {
        column.area = column.checked;
        column.checked = true;
        indices[column.area] = indices[column.area] || 0;
        column.areaIndex = indices[column.area]++;
      }

      return column;
    });
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
      return Boolean(
        find(checkedFields, ({ columnName }) => columnName === sort.columnName)
      );
    });
  }
}
