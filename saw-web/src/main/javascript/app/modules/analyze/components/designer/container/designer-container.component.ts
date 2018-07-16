import { Component, Input, Output, EventEmitter } from '@angular/core';
import * as isEmpty from 'lodash/isEmpty';
import * as filter from 'lodash/filter';
import * as unset from 'lodash/unset';
import * as get from 'lodash/get';
import * as isNumber from 'lodash/isNumber';
import * as every from 'lodash/every';
import * as forEach from 'lodash/forEach';
import * as find from 'lodash/find';
import * as map from 'lodash/map';
import { flattenPivotData, flattenChartData } from '../../../../../common/utils/dataFlattener';

import { DesignerService } from '../designer.service';
import {
  DesignerMode,
  AnalysisStarter,
  Analysis,
  AnalysisType,
  SqlBuilder,
  SqlBuilderReport,
  SqlBuilderPivot,
  SqlBuilderChart,
  Artifact,
  DesignerToolbarAciton,
  Sort,
  Filter,
  IToolbarActionResult,
  DesignerChangeEvent,
  DesignerSaveEvent,
  AnalysisReport
} from '../types';
import {
  DesignerStates,
  FLOAT_TYPES,
  DEFAULT_PRECISION,
  DATE_TYPES
} from '../consts';
import { AnalyzeDialogService } from '../../../services/analyze-dialog.service';
import { ChartService } from '../../../services/chart.service';

const template = require('./designer-container.component.html');
require('./designer-container.component.scss');

const GLOBAL_FILTER_SUPPORTED = ['chart', 'esReport'];

@Component({
  selector: 'designer-container',
  template
})
export class DesignerContainerComponent {
  @Input() public analysisStarter?: AnalysisStarter;
  @Input() public analysis?: Analysis;
  @Input() public designerMode: DesignerMode;
  @Output() public onBack: EventEmitter<boolean> = new EventEmitter();
  @Output() public onSave: EventEmitter<DesignerSaveEvent> = new EventEmitter();
  public isInDraftMode: boolean = false;
  public designerState: DesignerStates;
  public DesignerStates = DesignerStates;
  public artifacts: Artifact[] = [];
  public data: any = [];
  public dataCount: number;
  public auxSettings: any = {};
  public sorts: Sort[] = [];
  public sortFlag = [];
  public filters: Filter[] = [];
  public booleanCriteria: string = 'AND';
  public layoutConfiguration: 'single' | 'multi';
  public isInQueryMode = false;
  public chartTitle = '';
  // minimum requirments for requesting data, obtained with: canRequestData()
  public areMinRequirmentsMet = false;

  constructor(
    private _designerService: DesignerService,
    private _analyzeDialogService: AnalyzeDialogService,
    private _chartService: ChartService
  ) {}

  ngOnInit() {
    const isReport = ['report', 'esReport'].includes(
      get(this.analysis, 'type') || get(this.analysisStarter, 'type')
    );
    this.designerState = DesignerStates.WAITING_FOR_COLUMNS;
    /* prettier-ignore */
    switch (this.designerMode) {
    case 'new':
      this.initNewAnalysis().then(() => {
        this.designerState = DesignerStates.NO_SELECTION;
      });
      this.layoutConfiguration = this.getLayoutConfiguration(
        this.analysisStarter
      );
      break;
    case 'edit':
      this.initExistingAnalysis();
      this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
      this.layoutConfiguration = this.getLayoutConfiguration(
        this.analysis
      );
      if (!isReport) {
        this.requestDataIfPossible();
      }
      break;
    case 'fork':
      this.forkAnalysis().then(() => {
        this.initExistingAnalysis();
        this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
        this.layoutConfiguration = this.getLayoutConfiguration(
          this.analysis
        );
        if (!isReport) {
          this.requestDataIfPossible();
        }
      });
    default:
      break;
    }
  }

  getLayoutConfiguration(analysis: Analysis | AnalysisStarter) {
    /* prettier-ignore */
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
    const { type, semanticId } = this.analysisStarter;
    return this._designerService
      .createAnalysis(semanticId, type)
      .then((newAnalysis: Analysis) => {
        this.analysis = { ...this.analysisStarter, ...newAnalysis };
        if (!this.analysis.sqlBuilder) {
          this.analysis.sqlBuilder = {
            joins: []
          };
        }
        this.artifacts = this.analysis.artifacts;
        this.initAuxSettings();
        this.analysis.edit = this.analysis.edit || false;
        unset(this.analysis, 'supports');
        unset(this.analysis, 'categoryId');
      });
  }

  initExistingAnalysis() {
    const sqlBuilder = this.analysis.sqlBuilder;
    this.artifacts = this.fixLegacyArtifacts(this.analysis.artifacts);
    this.filters = sqlBuilder.filters;
    this.sorts = sqlBuilder.sorts || sqlBuilder.orderByColumns;
    this.booleanCriteria = sqlBuilder.booleanCriteria;
    this.isInQueryMode = this.analysis.edit;

    this.initAuxSettings();

    this.addDefaultSorts();
    this.areMinRequirmentsMet = this.canRequestData();
  }

  initAuxSettings() {
    /* prettier-ignore */
    switch(this.analysis.type) {
    case 'chart':
      this._chartService.updateAnalysisModel(this.analysis);
      if (this.designerMode === 'new') {
        (<any>this.analysis).isInverted = (<any>this.analysis).chartType === 'bar';
        (<any>this.analysis).isStockChart = (<any>this.analysis).chartType.substring(0, 2) === 'ts';
      }
      this.chartTitle = this.analysis.chartTitle || this.analysis.name;

      this.auxSettings = {
        ...this.auxSettings,
        ...(this.analysis.type === 'chart' ? {
          legend: (<any>this.analysis).legend,
          labelOptions: (<any>this.analysis).labelOptions || {},
          isInverted: (<any>this.analysis).isInverted
        } : {})
      };
      break;
    }
  }

  fixLegacyArtifacts(artifacts): Array<Artifact> {
    /* prettier-ignore */
    switch(this.analysis.type) {
    case 'chart':
      const indices = {};
      forEach(artifacts, table => {
        table.columns = map(table.columns, column => {
          if (column.checked && column.checked !== true) {
            column.area = column.checked;
            column.checked = true;
            indices[column.area] = indices[column.area] || 0;
            column.areaIndex = indices[column.area]++;
          }

          return column;
        });
      });
      break;
    }
    return artifacts;
  }

  checkNodeForSorts() {
    if ((this.analysisStarter || this.analysis).type !== 'chart') return;
    const sqlBuilder = this.getSqlBuilder() as SqlBuilderChart;
    forEach(sqlBuilder.nodeFields, node => {
      let identical = false;
      forEach(this.sorts || [], sort => {
        const hasSort = this.sorts.some(
          sort => node.columnName === sort.columnName
        );
        if (!hasSort) {
          this.sorts.push({
            order: 'asc',
            columnName: node.columnName,
            type: node.type
          });
        }
      });
    });
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
    const analysis = this.analysis;
    this.analysis = null;
    return this._designerService
      .createAnalysis(semanticId, type)
      .then((newAnalysis: Analysis) => {
        this.analysis = {
          ...analysis,
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
    this.areMinRequirmentsMet = this.canRequestData();
    if (this.areMinRequirmentsMet) {
      this.requestData();
    } else {
      this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
    }
  }

  requestData() {
    this.designerState = DesignerStates.SELECTION_WAITING_FOR_DATA;
    this._designerService.getDataForAnalysis(this.analysis).then(
      response => {
        if (
          this.isDataEmpty(
            response.data,
            this.analysis.type,
            this.analysis.sqlBuilder
          )
        ) {
          this.designerState = DesignerStates.SELECTION_WITH_NO_DATA;
          this.data = [];
        } else {
          this.designerState = DesignerStates.SELECTION_WITH_DATA;
          this.dataCount = response.count;
          this.data = this.flattenData(response.data, this.analysis);
        }
      },
      err => {
        this.designerState = DesignerStates.SELECTION_WITH_NO_DATA;
        this.data = [];
      }
    );
  }

  flattenData(data, analysis: Analysis) {
    /* prettier-ignore */
    switch (analysis.type) {
    case 'pivot':
      return flattenPivotData(data, analysis.sqlBuilder);
    case 'report':
    case 'esReport':
      return data;
    case 'chart':
      return flattenChartData(
        data,
        analysis.sqlBuilder
      );
    }
  }

  onToolbarAction(action: DesignerToolbarAciton) {
    /* prettier-ignore */
    switch (action) {
    case 'sort':
      // TODO update sorts for multiple artifacts
      this._analyzeDialogService.openSortDialog(this.sorts, this.artifacts)
        .afterClosed().subscribe((result: IToolbarActionResult) => {
          if (result) {
            this.sorts = result.sorts;
            this.onSettingsChange({ subject: 'sort' });
          }
        });
      break;
    case 'filter':
      const supportsGlobalFilters = GLOBAL_FILTER_SUPPORTED.includes((this.analysis || this.analysisStarter).type);
      this._analyzeDialogService.openFilterDialog(this.filters, this.artifacts, this.booleanCriteria, supportsGlobalFilters)
        .afterClosed().subscribe((result: IToolbarActionResult) => {
          if (result) {
            this.filters = result.filters;
            this.booleanCriteria = result.booleanCriteria;
            this.onSettingsChange({ subject: 'filter' });
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
      if (this.isInQueryMode && !this.analysis.edit) {
        this._analyzeDialogService.openQueryConfirmationDialog().afterClosed().subscribe(result => {
          if (result) {
            this.changeToQueryModePermanently();
            this.openSaveDialog();
          }
        });
      } else {
        this.openSaveDialog();
      }
      break;
    case 'refresh':
      this.requestDataIfPossible();
      break;
    case 'modeToggle':
      this.toggleDesignerQueryModes();
      break;
    }
  }

  openSaveDialog() {
    this._analyzeDialogService
      .openSaveDialog(this.analysis)
      .afterClosed()
      .subscribe((result: IToolbarActionResult) => {
        if (result) {
          this.onSave.emit({
            isSaveSuccessful: result.isSaveSuccessful,
            analysis: result.analysis
          });
          this.isInDraftMode = false;
        }
      });
  }

  toggleDesignerQueryModes() {
    this.isInQueryMode = !this.isInQueryMode;
    if (!this.isInQueryMode) {
      delete (this.analysis as AnalysisReport).queryManual;
    }
  }

  getSqlBuilder(): SqlBuilder {
    let partialSqlBuilder;
    let sortProp: 'sorts' | 'orderByColumns';

    /* prettier-ignore */
    switch (this.analysis.type) {
    case 'chart':
      partialSqlBuilder = this._designerService.getPartialChartSqlBuilder(this.artifacts[0].columns);
      sortProp = 'sorts';
      break;
    case 'pivot':
      partialSqlBuilder = this._designerService.getPartialPivotSqlBuilder(this.artifacts[0].columns);
      sortProp = 'sorts';
      break;
    case 'esReport':
      partialSqlBuilder = this._designerService.getPartialEsReportSqlBuilder(this.artifacts[0].columns);
      sortProp = 'sorts';
      break;
    case 'report':
      partialSqlBuilder = {
        joins: (<SqlBuilderReport>this.analysis.sqlBuilder).joins
      };
      sortProp = 'orderByColumns';
      break;
    }

    const sqlBuilder = {
      booleanCriteria: this.booleanCriteria,
      filters: this.filters,
      [sortProp]: this.sorts,
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
      const parsedData = flattenChartData(data, sqlBuilder);
      return isEmpty(parsedData);
    // TODO verify if the object returned is empty
    case 'report':
    case 'esReport':
      return isEmpty(data);
    }
  }

  onSettingsChange(event: DesignerChangeEvent) {
    /* prettier-ignore */
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
    /* prettier-ignore */
    switch (event.subject) {
    // backend data refresh needed
    case 'column':
      this.cleanSorts();
      this.setColumnPropsToDefaultIfNeeded(event.column);
      this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
      this.areMinRequirmentsMet = this.canRequestData();
      break;
    case 'removeColumn':
      this.cleanSorts();
      this.setColumnPropsToDefaultIfNeeded(event.column);
      this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
      this.artifacts = [...this.artifacts];
      break;
    case 'aggregate':
    case 'filterRemove':
    case 'joins':
    case 'changeQuery':
      this.areMinRequirmentsMet = this.canRequestData();
      this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
      break;
    case 'submitQuery':
      this.changeToQueryModePermanently();
      this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
      this.requestDataIfPossible();
      break;
    case 'filter':
    case 'sort':
      this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
      this.requestDataIfPossible();
      break;
    // only front end data refresh needed
    case 'format':
    case 'aliasName':
      this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
      this.artifacts = [...this.artifacts];
      break;
    case 'artifactPosition':
    case 'visibleIndex':
    }
  }

  changeToQueryModePermanently() {
    this.analysis.edit = true;
    this.filters = [];
    this.sorts = [];
  }

  setColumnPropsToDefaultIfNeeded(column) {
    unset(column, 'aggregate');
    if (FLOAT_TYPES.includes(column.type)) {
      if (!column.format) {
        column.format = {};
      }
      if (!column.format.precision) {
        column.format.precision = DEFAULT_PRECISION;
      }
    }
    if (DATE_TYPES.includes(column.type) && !column.format) {
      column.format = 'yyyy-MM-dd';
    }
  }

  handleOtherChangeEvents(event: DesignerChangeEvent) {
    /* prettier-ignore */
    switch (event.subject) {
    case 'selectedFields':
      this.cleanSorts();
      this.addDefaultSorts();
      this.checkNodeForSorts();
      this.areMinRequirmentsMet = this.canRequestData();
      this.requestDataIfPossible();
      break;
    case 'dateInterval':
    case 'aggregate':
    case 'filter':
    case 'format':
      this.requestDataIfPossible();
      break;
    case 'aliasName':
      // reload frontEnd
      this.updateAnalysis();
      this.artifacts = [...this.artifacts];
      if (this.analysis.type === 'chart') {
        this.refreshDataObject();
      }
      break;
    case 'sort':
      this.cleanSorts();
      this.addDefaultSorts();
      this.requestDataIfPossible();
    case 'comboType':
      this.updateAnalysis();
      this.refreshDataObject();
      break;
    case 'labelOptions':
      (<any>this.analysis).labelOptions = event.data.labelOptions;
      this.auxSettings = { ...this.auxSettings, ...event.data };
      this.refreshDataObject();
      break;
    case 'legend':
      if (!event.data || !event.data.legend) return;
      (<any>this.analysis).legend = event.data.legend;
      this.auxSettings = { ...this.auxSettings, ...event.data };
      this.artifacts = [...this.artifacts];
      break;
    case 'inversion':
      if (!event.data) return;
      (<any>this.analysis).isInverted = event.data.isInverted;
      this.auxSettings = { ...this.auxSettings, ...event.data };
      this.artifacts = [...this.artifacts];
      break;
    case 'chartTitle':
      if (!event.data) return;
      (<any>this.analysis).chartTitle = event.data.title;
      this.auxSettings = { ...this.auxSettings, ...event.data };
      this.artifacts = [...this.artifacts];
      break;
    }
  }

  /**
   * refreshFrontData Refreshes the data object to trigger
   * updates on child components
   *
   * @returns {undefined}
   */
  refreshDataObject() {
    this.data = this.data ? [...this.data] : [];
  }

  canRequestData() {
    this.updateAnalysis();
    // there has to be at least 1 data field, to make a request
    /* prettier-ignore */
    switch (this.analysis.type) {
    case 'pivot':
      const length = get(this.analysis, 'sqlBuilder.dataFields.length');
      return isNumber(length) ? length > 0 : false;
    case 'chart':
      /* At least one y and x field needs to be present. If this is a bubble chart,
       * at least one z axis field is also needed */
      const sqlBuilder = get(this.analysis, 'sqlBuilder') || {};
      const requestCondition = [
        find(sqlBuilder.dataFields || [], field => field.checked === 'y'),
        find(sqlBuilder.nodeFields || [], field => field.checked === 'x'),
        /* prettier-ignore */
        ...((<any>this.analysis).chartType === 'bubble' ?
        [
          find(sqlBuilder.dataFields || [], field => field.checked === 'z')
        ] : [])
      ]
      return every(requestCondition, Boolean);
    case 'report':
    case 'esReport':
      return this.canRequestReport(this.analysis.artifacts);
    }
  }

  canRequestReport(artifacts) {
    if (this.analysis.edit) {
      return Boolean((<AnalysisReport>this.analysis).queryManual);
    }

    let atLeastOneIsChecked = false;
    forEach(artifacts, artifact => {
      forEach(artifact.columns, column => {
        if (column.checked) {
          atLeastOneIsChecked = true;
          return false;
        }
      });
      if (atLeastOneIsChecked) {
        return false;
      }
    });
    return atLeastOneIsChecked;
  }

  updateAnalysis() {
    const { edit, type, artifacts } = this.analysis;
    if (type === 'report' && edit) {
      // reset checked fields, sorts, and filters for query mode report analysis
      this.filters = [];
      this.sorts = [];
      forEach(artifacts, artifact => {
        forEach(artifact.column, col => col.checked = false);
      });
    }
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
      return Boolean(
        find(checkedFields, ({ columnName }) => columnName === sort.columnName)
      );
    });
  }
}
