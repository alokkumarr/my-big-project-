import {
  Component,
  Input,
  OnInit,
  OnDestroy,
  Output,
  EventEmitter
} from '@angular/core';
import * as isEmpty from 'lodash/isEmpty';
import * as filter from 'lodash/filter';
import * as unset from 'lodash/unset';
import * as get from 'lodash/get';
import * as flatMap from 'lodash/flatMap';
import * as every from 'lodash/every';
import * as some from 'lodash/some';
import * as forEach from 'lodash/forEach';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as fpReduce from 'lodash/fp/reduce';
import * as forOwn from 'lodash/forOwn';
import * as find from 'lodash/find';
import * as map from 'lodash/map';
import * as cloneDeep from 'lodash/cloneDeep';
import { Store, Select } from '@ngxs/store';
import { Observable, Subscription } from 'rxjs';
import { takeWhile, finalize, map as map$ } from 'rxjs/operators';

import {
  flattenPivotData,
  flattenChartData
} from '../../../../common/utils/dataFlattener';

import { DesignerService } from '../designer.service';
import {
  DesignerMode,
  AnalysisStarter,
  Analysis,
  AnalysisChart,
  AnalysisType,
  SqlBuilder,
  SqlBuilderPivot,
  SqlBuilderChart,
  Artifact,
  DesignerToolbarAciton,
  Sort,
  Filter,
  IToolbarActionResult,
  DesignerChangeEvent,
  DesignerSaveEvent,
  isDSLAnalysis
} from '../types';
import {
  AnalysisDSL,
  LabelOptions,
  AnalysisChartDSL,
  AnalysisMapDSL,
  QueryDSL,
  ArtifactColumn
} from '../../../../models';
import {
  DesignerStates,
  FLOAT_TYPES,
  DEFAULT_PRECISION,
  DATE_TYPES
} from '../consts';
import moment from 'moment';

import { AnalyzeDialogService } from '../../services/analyze-dialog.service';
import { ChartService } from '../../../../common/services/chart.service';
import { AnalyzeService } from '../../services/analyze.service';
import { JwtService } from '../../../../common/services';

import {
  DesignerClearGroupAdapters,
  DesignerInitGroupAdapters,
  DesignerInitNewAnalysis,
  DesignerInitEditAnalysis,
  DesignerInitForkAnalysis,
  DesignerUpdateAnalysisChartTitle,
  DesignerUpdateAnalysisChartInversion,
  DesignerUpdateAnalysisChartLegend,
  DesignerUpdateAnalysisChartLabelOptions,
  DesignerUpdateAnalysisMetadata,
  DesignerUpdateAnalysisSubType,
  DesignerUpdateSorts,
  DesignerUpdateFilters,
  DesignerUpdatebooleanCriteria,
  DesignerMergeMetricColumns,
  DesignerMergeSupportsIntoAnalysis,
  DesignerLoadMetric,
  // DesignerResetState,
  DesignerSetData,
  DesignerAddArtifactColumn,
  DesignerRemoveArtifactColumn,
  DesignerUpdateArtifactColumn,
  DesignerUpdateEditMode,
  DesignerUpdateQuery,
  DesignerJoinsArray,
  ConstructDesignerJoins,
  DesignerUpdateAggregateInSorts
} from '../actions/designer.actions';
import { DesignerState } from '../state/designer.state';
import { CUSTOM_DATE_PRESET_VALUE, NUMBER_TYPES } from './../../consts';
import { MatDialog } from '@angular/material';
import { DerivedMetricComponent } from '../derived-metric/derived-metric.component';

const GLOBAL_FILTER_SUPPORTED = ['chart', 'esReport', 'pivot', 'map'];

@Component({
  selector: 'designer-container',
  templateUrl: './designer-container.component.html',
  styleUrls: ['./designer-container.component.scss']
})
export class DesignerContainerComponent implements OnInit, OnDestroy {
  @Input() public analysisStarter?: AnalysisStarter;
  @Input() public analysis?: Analysis | AnalysisDSL;
  @Input() public designerMode: DesignerMode;

  @Output() public onBack: EventEmitter<boolean> = new EventEmitter();
  @Output() public onSave: EventEmitter<DesignerSaveEvent> = new EventEmitter();
  @Select(DesignerState.data) data$: Observable<any[]>;
  @Select(DesignerState.analysis) dslAnalysis$: Observable<AnalysisDSL>;
  dslSorts$: Observable<Sort[]> = this.dslAnalysis$.pipe(
    map$(analysis => analysis.sipQuery.sorts)
  );

  sipQuery$: Observable<QueryDSL> = this.dslAnalysis$.pipe(
    map$(analysis => analysis.sipQuery)
  );

  public isInDraftMode = false;
  public designerState = DesignerStates.WAITING_FOR_COLUMNS;
  public DesignerStates = DesignerStates;
  public artifacts: Artifact[] = [];
  public data: any = [];
  public dataCount: number;
  public auxSettings: any = {};
  public sorts: Sort[] = [];
  public sortFlag = [];
  public filters: Filter[] = [];
  public booleanCriteria = 'AND';
  public layoutConfiguration: 'single' | 'multi';
  public isInQueryMode = false;
  public chartTitle = '';
  private subscriptions: Subscription[] = [];
  // minimum requirments for requesting data, obtained with: canRequestData()
  public areMinRequirmentsMet = false;

  get analysisSubType(): string {
    const analysis = this._store.selectSnapshot(DesignerState).analysis;
    const normalAnalysisSubType = (<AnalysisChart>this.analysis).chartType;
    const subTypePath =
      this.analysis.type === 'chart'
        ? 'chartOptions.chartType'
        : 'mapOptions.mapType';
    return isDSLAnalysis(this.analysis)
      ? get(analysis, subTypePath)
      : normalAnalysisSubType;
  }

  constructor(
    public _designerService: DesignerService,
    public _analyzeDialogService: AnalyzeDialogService,
    public _chartService: ChartService,
    public _analyzeService: AnalyzeService,
    private dialog: MatDialog,
    private _store: Store,
    private _jwtService: JwtService
  ) {}

  ngOnDestroy() {
    // this._store.dispatch(new DesignerResetState());
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  ngOnInit() {
    const analysisType: string =
      get(this.analysis, 'type') || get(this.analysisStarter, 'type');
    const isReport = ['report', 'esReport'].includes(analysisType);
    this.designerState = DesignerStates.WAITING_FOR_COLUMNS;
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
        this.analysis = isDSLAnalysis(this.analysis)
          ? this.mapTableWithFields(this.analysis)
          : this.analysis;
        isDSLAnalysis(this.analysis) &&
          this._store.dispatch(new DesignerInitEditAnalysis(this.analysis));
        this._store.dispatch(new ConstructDesignerJoins(this.analysis));
        this.initExistingAnalysis();
        this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
        this.layoutConfiguration = this.getLayoutConfiguration(this.analysis);

        this._analyzeService
          .getSemanticObject(this.analysis.semanticId)
          .toPromise()
          .then(semanticObj => {
            this._store.dispatch(
              new DesignerMergeMetricColumns(semanticObj.artifacts[0].columns)
            );
            this._store.dispatch(
              new DesignerMergeSupportsIntoAnalysis(semanticObj.supports)
            );
          });

        if (!isReport) {
          this.requestDataIfPossible();
        }
        break;
      case 'fork':
        this.forkAnalysis().then(() => {
          this.analysis = isDSLAnalysis(this.analysis)
            ? this.mapTableWithFields(this.analysis)
            : this.analysis;
          this.initExistingAnalysis();
          this._store.dispatch(new ConstructDesignerJoins(this.analysis));
          this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
          this.layoutConfiguration = this.getLayoutConfiguration(this.analysis);
          this._analyzeService
            .getSemanticObject(this.analysis.semanticId)
            .toPromise()
            .then(semanticObj => {
              this._store.dispatch(
                new DesignerMergeSupportsIntoAnalysis(semanticObj.supports)
              );
            });
          if (!isReport) {
            this.requestDataIfPossible();
          }
        });
        break;
      default:
        break;
    }
    this.data$.subscribe(data => (this.data = data));
    this.dslAnalysis$.subscribe(analysis => {
      if (!analysis || ['report'].includes(analysisType)) {
        return;
      }
      this.analysis = analysis;
      // const sqlBuilder = this._designerService.getSqlBuilder(
      //   this.analysis,
      //   this.booleanCriteria,
      //   this.filters,
      //   this.sorts
      // );
      // set(this.analysis, 'sqlBuilder', sqlBuilder);
    });
  }

  mergeSemnticArtifactColumnsWithAnalysisArtifactColumn() {}

  getLayoutConfiguration(analysis: Analysis | AnalysisStarter | AnalysisDSL) {
    switch (analysis.type) {
      case 'report':
      case 'esReport':
        return 'multi';
      case 'pivot':
      case 'chart':
      case 'map':
      default:
        return 'single';
    }
  }

  mapTableWithFields(analysis) {
    let tableName = '';
    forEach(analysis.sipQuery.artifacts, table => {
      tableName = table.artifactsName;
      table.fields = map(table.fields, field => {
        if (field.table) {
          return field;
        }
        field.table = tableName;
        return field;
      });
    });
    return analysis;
  }

  initNewAnalysis() {
    const { type, semanticId, chartType } = this.analysisStarter;
    const artifacts$ = this._analyzeService
      .getArtifactsForDataSet(semanticId)
      .toPromise();
    const newAnalysis$ = this._designerService.createAnalysis(semanticId, type);
    return Promise.all([artifacts$, newAnalysis$]).then(
      ([metric, newAnalysis]) => {
        this._store.dispatch(
          new DesignerLoadMetric({
            metricName: metric.metricName,
            artifacts: metric.artifacts
          })
        );
        const artifacts = this._store.selectSnapshot(
          state => state.designerState.metric.artifacts
        );

        if (type === 'map') {
          (<AnalysisMapDSL>newAnalysis).mapOptions.mapType = chartType;
        }
        this.analysis = {
          ...(newAnalysis['analysis'] || newAnalysis),
          artifacts: cloneDeep(artifacts),
          ...this.analysisStarter
        };
        isDSLAnalysis(this.analysis) &&
          this._store.dispatch(new DesignerInitNewAnalysis(this.analysis));

        if (!isDSLAnalysis(this.analysis)) {
          this.analysis.edit = this.analysis.edit || false;
          this.analysis.supports = this.analysisStarter.supports;
          !this.analysis.sqlBuilder &&
            (this.analysis.sqlBuilder = {
              joins: []
            });
        } else if (this.analysis.type === 'chart') {
          this._store.dispatch(
            new DesignerUpdateAnalysisSubType(this.analysisStarter.chartType)
          );
        } else if (this.analysis.type === 'report') {
          this._store.dispatch(new DesignerUpdateEditMode(false));
        }
        this.artifacts = this.fixLegacyArtifacts(this.analysis.artifacts);
        // Forking an analysis and without saving it and trying to create a new one copies the artifcats
        // properties of the forked one to the new one. hence the below change.
        this.resetArtifacts();
        this.initAuxSettings();
        unset(this.analysis, 'categoryId');
      }
    );
  }

  resetArtifacts() {
    fpPipe(
      fpFlatMap(artifact => artifact.columns || artifact.fields),
      fpReduce((accumulator, column) => {
        column.alias = '';
        delete column.aggregate;
        if (column.type === 'date') {
          column.dateFormat = 'MMM d YYYY';
        }
        delete column.checked;
        return accumulator;
      }, {})
    )(this.artifacts);
  }

  generateDSLDateFilters(filters) {
    forEach(filters, filtr => {
      if (
        !filtr.isRuntimeFilter &&
        !filtr.isGlobalFilter &&
        (filtr.type === 'date' && filtr.model.operator === 'BTW')
      ) {
        filtr.model.gte = moment(filtr.model.value, 'MM-DD-YYYY').format(
          'YYYY-MM-DD'
        );
        filtr.model.lte = moment(filtr.model.otherValue, 'MM-DD-YYYY').format(
          'YYYY-MM-DD'
        );
        filtr.model.preset = CUSTOM_DATE_PRESET_VALUE;
      }
    });
    return filters;
  }

  initExistingAnalysis() {
    const queryBuilder = isDSLAnalysis(this.analysis)
      ? this.analysis.sipQuery
      : this.analysis.sqlBuilder;
    this.artifacts = this.fixLegacyArtifacts(this.analysis.artifacts);
    this.filters = isDSLAnalysis(this.analysis)
      ? this.generateDSLDateFilters(queryBuilder.filters)
      : queryBuilder.filters;
    this.sorts = queryBuilder.sorts || queryBuilder.orderByColumns;
    this.booleanCriteria = queryBuilder.booleanCriteria;
    this.isInQueryMode = this._store.selectSnapshot(
      DesignerState
    ).analysis.designerEdit;

    this.initAuxSettings();

    this.addDefaultSorts();
    this.areMinRequirmentsMet = this.canRequestData();
  }

  initAuxSettings() {
    switch (this.analysis.type) {
      case 'chart':
        this.addDefaultLabelOptions();
        if (this.designerMode === 'new') {
          if (isDSLAnalysis(this.analysis)) {
            this._store.dispatch(
              new DesignerUpdateAnalysisChartInversion(
                this.analysisSubType === 'bar'
              )
            );
          } else {
            (<any>this.analysis).isInverted = this.analysisSubType === 'bar';
          }
        }
        const chartOptions = this._store.selectSnapshot(
          state => (<AnalysisChartDSL>state.designerState.analysis).chartOptions
        );
        this.chartTitle = isDSLAnalysis(this.analysis)
          ? chartOptions.chartTitle || this.analysis.name
          : (this.chartTitle = this.analysis.chartTitle || this.analysis.name);

        const chartOnlySettings = {
          legend: isDSLAnalysis(<any>this.analysis)
            ? chartOptions.legend
            : (<any>this.analysis).legend,
          labelOptions: isDSLAnalysis(<any>this.analysis)
            ? chartOptions.labelOptions
            : (<any>this.analysis).labelOptions || {},
          isInverted: isDSLAnalysis(<any>this.analysis)
            ? chartOptions.isInverted
            : (<any>this.analysis).isInverted
        };

        this.auxSettings = {
          ...this.auxSettings,
          ...chartOnlySettings
        };
        break;
      case 'map':
        this.auxSettings = {
          ...(<AnalysisMapDSL>this.analysis).mapOptions
        };
        break;
    }
  }

  addDefaultLabelOptions() {
    if (this.analysis.type !== 'chart' || this.designerMode !== 'new') {
      return;
    }

    let labelOptions: LabelOptions;

    switch (this.analysisSubType) {
      case 'pie':
        labelOptions = {
          enabled: true,
          value: 'percentage'
        };
        break;
      default:
        labelOptions = {
          enabled: false,
          value: ''
        };
    }
    this._store.dispatch(
      new DesignerUpdateAnalysisChartLabelOptions(labelOptions)
    );
  }

  fixLegacyArtifacts(artifacts): Array<Artifact> {
    if (this.analysis.type === 'chart') {
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
    }
    const fields = flatMap(
      (<AnalysisDSL>this.analysis).sipQuery.artifacts,
      artifact => artifact.fields
    );

    const flatArtifacts = flatMap(artifacts, artifact => artifact.columns);
    flatArtifacts.forEach(col => {
      col.checked = '';
      fields.forEach(field => {
        if (col.table === field.table && col.columnName === field.columnName) {
          col.checked = true;
          if (this.analysis.type === 'pivot') {
            col.format = field.format;
            col.aliasName = field.aliasName;
          }
        }
      });
    });

    return artifacts;
  }

  checkNodeForSorts() {
    if ((this.analysisStarter || this.analysis).type !== 'chart') {
      return;
    }
    const sqlBuilder = this.getSqlBuilder() as SqlBuilderChart;
    forEach(sqlBuilder.nodeFields, node => {
      forEach(this.sorts || [], sort => {
        const hasSort = this.sorts.some(
          sortCol => node.columnName === sortCol.columnName
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
    isDSLAnalysis(this.analysis) &&
      this._store.dispatch(new DesignerUpdateSorts(this.sorts));
  }

  addDefaultSorts() {
    if ((this.analysisStarter || this.analysis).type !== 'chart') {
      return;
    }

    const nonDataFields = this._store.selectSnapshot(
      DesignerState.selectedNonDataFields
    );

    if (isEmpty(this.sorts)) {
      forEach(nonDataFields || [], node => {
        this.sorts.push({
          order: 'asc',
          columnName: node.columnName,
          type: node.type
        });
      });
    }
    isDSLAnalysis && this._store.dispatch(new DesignerUpdateSorts(this.sorts));
  }

  forkAnalysis() {
    const { type, semanticId } = this.analysis;
    const analysis = this.analysis;
    this.analysis = null;
    return this._designerService
      .createAnalysis(semanticId, type)
      .then((newAnalysis: Analysis | AnalysisDSL) => {
        this.analysis = {
          ...analysis,
          ...{
            id: newAnalysis.id
          },
          ...(isDSLAnalysis(newAnalysis)
            ? {
                id: newAnalysis.id,
                semanticId: newAnalysis.semanticId,
                createdTime: newAnalysis.createdTime,
                createdBy: newAnalysis.createdBy
              }
            : {
                metric: newAnalysis.metric,
                createdTimestamp: newAnalysis.createdTimestamp,
                userId: newAnalysis.userId,
                userFullName: newAnalysis.userFullName,
                metricName: newAnalysis.metricName
              })
        };
        isDSLAnalysis(this.analysis) &&
          this._store.dispatch(new DesignerInitForkAnalysis(this.analysis));
      });
  }

  loadGridWithoutData(column, type) {
    if (isEmpty(this.data)) {
      this.data = [{}];
    }
    this.data.map(row => {
      if (type === 'add') {
        row[column.name] = '';
      } else {
        delete row[column.name];
      }
    });
    this.data = cloneDeep(this.data);
  }

  /**
   * setEmptyData - In case of no data returned from refresh,
   * this returns a sane default emtpy data object based on
   * analysis type.
   *
   * @returns {Array<any>}
   */
  setEmptyData(): Array<any> {
    const emptyReportData = () => {
      const columnMap = this.data[0] || {};

      forOwn(columnMap, (val, key) => {
        columnMap[key] = '';
      });

      return [columnMap];
    };

    switch (this.analysis.type) {
      case 'pivot':
      case 'chart':
      case 'map':
        return [];

      case 'report':
      case 'esReport':
        return emptyReportData();
    }
  }

  formulateChartRequest(analysis) {
    let isGroupByPresent = false;
    forEach(analysis.sqlBuilder.nodeFields, node => {
      if (node.checked === 'g') {
        isGroupByPresent = true;
      }
    });
    if (!isGroupByPresent) {
      forEach(analysis.sqlBuilder.dataFields, dataField => {
        dataField.aggregate =
          dataField.aggregate === 'percentagebyrow'
            ? 'percentage'
            : dataField.aggregate;
      });

      forEach(this.artifacts[0].columns, col => {
        col.aggregate =
          col.aggregate === 'percentagebyrow' ? 'percentage' : col.aggregate;
      });
    }
    return analysis;
  }

  requestDataIfPossible() {
    this.areMinRequirmentsMet = this.canRequestData();
    if (this.areMinRequirmentsMet) {
      /* If it's a DSL analysis, check if there are any fields added before trying
        to load data.
         */
      if (isDSLAnalysis(this.analysis)) {
        this.designerState = DesignerStates.SELECTION_WAITING_FOR_DATA;
        const subscription = this._store
          .select(DesignerState.canRequestData)
          .pipe(
            takeWhile(canRequestData => !canRequestData),
            finalize(() => {
              this.requestData();
            })
          )
          .subscribe();
        this.subscriptions.push(subscription);
      } else {
        this.designerState = DesignerStates.SELECTION_WAITING_FOR_DATA;
        this.requestData();
      }
    } else {
      this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
    }
  }

  nonDSLAnalysisForRequest(analysis: Analysis): Analysis {
    const dataFields = analysis.sqlBuilder.dataFields;
    const filters = analysis.sqlBuilder.filters;

    forEach(dataFields, field => {
      if (dataFields.length > 1 && field.limitType) {
        delete field.limitType;
        delete field.limitValue;
      }
    });

    forEach(filters, filt => {
      if (filt.isRuntimeFilter) {
        delete filt.model;
      }
    });

    this.analysis =
      this.analysis.type === 'chart'
        ? this.formulateChartRequest(analysis)
        : analysis;
    return <Analysis>this.analysis;
  }

  dslAnalysisForRequest(): AnalysisDSL {
    return this._store.selectSnapshot(state => state.designerState.analysis);
  }

  requestData() {
    const requestAnalysis = isDSLAnalysis(this.analysis)
      ? this.dslAnalysisForRequest()
      : this.nonDSLAnalysisForRequest(this.analysis);
    const clonedAnalysis = cloneDeep(requestAnalysis);
    // unset properties that are set by merging data from semantic layer
    // because these properties are not part of dsl analysis definition
    if (isDSLAnalysis(clonedAnalysis)) {
      unset(clonedAnalysis, 'supports');
      forEach(clonedAnalysis.sipQuery.artifacts, artifact => {
        forEach(artifact.fields, column => {
          unset(column, 'geoType');
        });
      });
    }

    this._designerService.getDataForAnalysis(clonedAnalysis).then(
      response => {
        if (
          this.isDataEmpty(
            response.data,
            this.analysis.type,
            (<AnalysisDSL>this.analysis).sipQuery ||
              (<Analysis>this.analysis).sqlBuilder
          )
        ) {
          this.designerState = DesignerStates.SELECTION_WITH_NO_DATA;
          this.dataCount = 0;
          this._store.dispatch(new DesignerSetData(this.setEmptyData()));
        } else {
          this.designerState = DesignerStates.SELECTION_WITH_DATA;
          this.dataCount = response.count;
          this._store.dispatch(
            new DesignerSetData(this.flattenData(response.data, this.analysis))
          );
          if (this.analysis.type === 'report' && response.designerQuery) {
            if (!this.isInQueryMode) {
              this._store.dispatch(
                new DesignerUpdateQuery(response.designerQuery)
              );
            }
          }
        }
      },
      err => {
        this.designerState = DesignerStates.SELECTION_WITH_NO_DATA;
        this._store.dispatch(new DesignerSetData([]));
      }
    );
  }

  flattenData(data, analysis: Analysis | AnalysisDSL) {
    switch (analysis.type) {
      case 'pivot':
        return flattenPivotData(
          data,
          (<AnalysisDSL>analysis).sipQuery || (<Analysis>analysis).sqlBuilder
        );
      case 'report':
      case 'esReport':
        return data;
      case 'chart':
      case 'map':
        return flattenChartData(
          data,
          (<AnalysisDSL>analysis).sipQuery || (<Analysis>analysis).sqlBuilder
        );
    }
  }

  onToolbarAction(action: DesignerToolbarAciton) {
    switch (action) {
      case 'sort':
        // TODO update sorts for multiple artifacts
        this._analyzeDialogService
          .openSortDialog(
            this._store.selectSnapshot(DesignerState.analysis).sipQuery.sorts,
            this._store.selectSnapshot(DesignerState.analysis).sipQuery
              .artifacts
          )
          .afterClosed()
          .subscribe((result: IToolbarActionResult) => {
            if (result) {
              this._store.dispatch(new DesignerUpdateSorts(result.sorts));
              this.sorts = result.sorts;
              this.onSettingsChange({ subject: 'sort' });
            }
          });
        break;
      case 'filter':
        const supportsGlobalFilters = GLOBAL_FILTER_SUPPORTED.includes(
          (this.analysis || this.analysisStarter).type
        );
        this._analyzeDialogService
          .openFilterDialog(
            this.filters,
            this.artifacts,
            this.booleanCriteria,
            supportsGlobalFilters
          )
          .afterClosed()
          .subscribe((result: IToolbarActionResult) => {
            if (result) {
              this._store.dispatch(new DesignerUpdateFilters(result.filters));
              this.filters = this.generateDSLDateFilters(result.filters);
              this._store.dispatch(
                new DesignerUpdatebooleanCriteria(result.booleanCriteria)
              );
              this.booleanCriteria = result.booleanCriteria;
              this.onSettingsChange({ subject: 'filter' });
            }
          });
        break;
      case 'preview':
        const analysisForPreview = isDSLAnalysis(this.analysis)
          ? this._store.selectSnapshot(state => state.designerState.analysis)
          : this.analysis;
        this._analyzeDialogService.openPreviewDialog(<Analysis | AnalysisDSL>(
          analysisForPreview
        ));
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
        this.openSaveDialogIfNeeded().then((result: IToolbarActionResult) => {
          if (result) {
            const shouldClose = result.action === 'saveAndClose';
            this.onSave.emit({
              requestExecution: shouldClose,
              analysis:
                result.analysis.type === 'report'
                  ? this._designerService.generateReportPayload(
                      cloneDeep(result.analysis)
                    )
                  : result.analysis
            });

            if (!shouldClose) {
              this.requestDataIfPossible();
            }
            this.isInDraftMode = false;
          }
        });
        break;
      case 'refresh':
        this.requestDataIfPossible();
        break;
      case 'modeToggle':
        this.toggleDesignerQueryModes();
        break;
    }
  }

  openSaveDialogIfNeeded(): Promise<any> {
    return new Promise(resolve => {
      const designerEdit = this._store.selectSnapshot(DesignerState).analysis
        .designerEdit;
      if (this.isInQueryMode && !designerEdit) {
        this._analyzeDialogService
          .openQueryConfirmationDialog()
          .afterClosed()
          .subscribe(result => {
            if (result) {
              this.changeToQueryModePermanently();
              resolve(this.openSaveDialog());
            }
          });
      } else {
        resolve(this.openSaveDialog());
      }
    });
  }

  openSaveDialog(): Promise<any> {
    if (
      isDSLAnalysis(this.analysis) &&
      ['new', 'fork'].includes(this.designerMode)
    ) {
      this._store.dispatch(
        new DesignerUpdateAnalysisMetadata({
          category: this._jwtService.userAnalysisCategoryId
        })
      );
    } else if (['new', 'fork'].includes(this.designerMode)) {
      (<Analysis>(
        this.analysis
      )).categoryId = this._jwtService.userAnalysisCategoryId;
    }

    const analysisForSave = isDSLAnalysis(this.analysis)
      ? this._store.selectSnapshot(state => state.designerState.analysis)
      : this.analysis;

    return this._analyzeDialogService
      .openSaveDialog(
        <Analysis | AnalysisDSL>analysisForSave,
        this.designerMode
      )
      .afterClosed()
      .toPromise();
  }

  toggleDesignerQueryModes() {
    this.isInQueryMode = !this.isInQueryMode;
  }

  getSqlBuilder(): SqlBuilder {
    let partialSqlBuilder;
    let sortProp: 'sorts' | 'orderByColumns';

    switch (this.analysis.type) {
      case 'chart':
      case 'map':
        partialSqlBuilder = this._designerService.getPartialChartSqlBuilder(
          this.artifacts[0].columns
        );
        sortProp = 'sorts';
        break;
      case 'pivot':
        partialSqlBuilder = this._designerService.getPartialPivotSqlBuilder(
          this.artifacts[0].columns
        );
        sortProp = 'sorts';
        break;
      case 'esReport':
        partialSqlBuilder = {
          dataFields: this._designerService.generateReportDataField(
            this.artifacts
          )
        };
        sortProp = 'sorts';
        break;
      case 'report':
        partialSqlBuilder = {
          dataFields: this._designerService.generateReportDataField(
            this.artifacts
          ),
          joins: (<AnalysisDSL>this.analysis).sipQuery.joins
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
      case 'map':
        const parsedData = flattenChartData(data, sqlBuilder);
        return isEmpty(parsedData);
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
      case 'map':
        this.handleOtherChangeEvents(event);
        break;
    }
    this.isInDraftMode = true;
  }

  handleReportChangeEvents(event: DesignerChangeEvent) {
    switch (event.subject) {
      // backend data refresh needed
      case 'column':
        this.setColumnPropsToDefaultIfNeeded(event.column);
        if (event.column.checked) {
          this._store.dispatch(new DesignerAddArtifactColumn(event.column));
        } else {
          this._store.dispatch(new DesignerRemoveArtifactColumn(event.column));
        }
        this.cleanSorts();
        this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
        this.areMinRequirmentsMet = this.canRequestData();
        this.loadGridWithoutData(event.column, 'add');
        break;
      case 'removeColumn':
        this.setColumnPropsToDefaultIfNeeded(event.column);
        this._store.dispatch(new DesignerRemoveArtifactColumn(event.column));
        this.cleanSorts();
        this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
        this.artifacts = [...this.artifacts];
        this.artifacts = this.removeColumn(event.column);
        this.loadGridWithoutData(event.column, 'remove');
        break;
      case 'aggregate':
        this._store.dispatch(
          new DesignerUpdateArtifactColumn({
            columnName: event.column.columnName,
            table: event.column.table || event.column['tableName'],
            aggregate: event.column.aggregate
          })
        );
        forEach(this.analysis.artifacts, artifactcolumns => {
          forEach(artifactcolumns.columns, col => {
            if (col.name === event.column.name) {
              col.aggregate = event.column.aggregate;
            }
          });
        });
        if (!isEmpty(this.data)) {
          this.data.map(row => {
            if (row[event.column.name]) {
              row[event.column.name] = '';
            }
          });
        }
        this.data = cloneDeep(this.data);
        // Need this fucntion to check if aggregation is applied for the same sorted column.
        // Need to remove this function once backend moves all logic of aggrregation to datafields.
        this._store.dispatch(new DesignerUpdateAggregateInSorts(event.column));

        this.areMinRequirmentsMet = this.canRequestData();
        this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
        break;
      case 'filterRemove':
      case 'joins':
        this._store.dispatch(new DesignerJoinsArray(event.data));
        this.areMinRequirmentsMet = this.canRequestData();
        this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
        break;
      case 'changeQuery':
        if (typeof event.data.query !== 'string') {
          break;
        }
        this.areMinRequirmentsMet = this.canRequestData();
        this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
        this._store.dispatch(new DesignerUpdateQuery(event.data.query));
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
      case 'reorder':
        this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
        if (isDSLAnalysis(this.analysis)) {
          this._store.dispatch(
            new DesignerUpdateArtifactColumn({
              columnName: event.column.columnName,
              table: event.column.table || event.column['tableName'],
              visibleIndex: event.column['visibleIndex']
            })
          );
        }
        break;
      case 'format':
      case 'alias':
        this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
        if (isDSLAnalysis(this.analysis)) {
          this._store.dispatch(
            new DesignerUpdateArtifactColumn({
              alias: event.column.alias,
              columnName: event.column.columnName,
              table: event.column.table || event.column['tableName'],
              format: event.column.format,
              dateFormat: event.column['dateFormat']
            })
          );
          this.analysis.sipQuery = { ...this.analysis.sipQuery };
        } else {
          this.analysis.sqlBuilder = { ...this.analysis.sqlBuilder };
        }
        this.artifacts = [...this.artifacts];
        break;
      case 'artifactPosition':
    }
  }

  removeColumn(data) {
    fpPipe(
      fpFlatMap(artifact => artifact.columns),
      fpReduce((acc, column) => {
        if (
          column.columnName === data.columnName &&
          column.table === data.table
        ) {
          delete column.checked;
        }
      }, {})
    )(this.artifacts);
    return this.artifacts;
  }

  changeToQueryModePermanently() {
    this._store.dispatch(new DesignerUpdateEditMode(true));
    this.filters = [];
    this.sorts = [];
  }

  setColumnPropsToDefaultIfNeeded(column) {
    unset(column, 'aggregate');
    if (NUMBER_TYPES.includes(column.type)) {
      if (!column.format) {
        column.format = {};
      }
      if (FLOAT_TYPES.includes(column.type) && !column.format.precision) {
        column.format.precision = DEFAULT_PRECISION;
      }
    }

    if (DATE_TYPES.includes(column.type)) {
      column.dateFormat = 'yyyy-MM-dd';
      column.format = 'yyyy-MM-dd';
    }
  }

  handleOtherChangeEvents(event: DesignerChangeEvent) {
    switch (event.subject) {
      case 'selectedFields':
        this.cleanSorts();
        this.addDefaultSorts();
        this.artifacts = [...this.artifacts];
        this.checkNodeForSorts();
        this.areMinRequirmentsMet = this.canRequestData();
        this.requestDataIfPossible();
        break;
      case 'dateInterval':
      case 'aggregate':
      case 'filter':
      case 'format':
        this.artifacts = this.fixLegacyArtifacts(this.analysis.artifacts);
        this.requestDataIfPossible();
        break;
      case 'updateDerivedMetric':
        this.openDerivedMetricDialog(event.column);
        break;
      case 'addNewDerivedMetric':
        this.openDerivedMetricDialog(null);
        break;
      case 'expressionUpdated':
        this._store.dispatch(
          new DesignerUpdateArtifactColumn({
            columnName: event.column.columnName,
            table: event.column.table,
            formula: event.column.formula,
            expression: event.column.expression
          })
        );
        forEach(this.artifacts[0].columns, col => {
          if (col.columnName === event.column.columnName) {
            col.formula = event.column.formula;
            col.expression = event.column.expression;
          }
        });
        this.artifacts = [...this.artifacts];
        this.requestDataIfPossible();
        break;
      case 'derivedMetricAdded':
        const artifact = this.artifacts[0];
        (<ArtifactColumn[]>artifact.columns).push(
          event.column as ArtifactColumn
        );
        this.artifacts = [artifact];
        break;
      case 'alias':
        // reload frontEnd
        this.updateAnalysis();
        this.artifacts = this.fixLegacyArtifacts(this.analysis.artifacts);
        this.artifacts = [...this.artifacts];
        if (this.analysis.type === 'chart' || this.analysis.type === 'pivot') {
          this.requestDataIfPossible();
        }
        break;
      case 'sort':
        this.cleanSorts();
        this.addDefaultSorts();
        this.requestDataIfPossible();
        this.updateAnalysis();
        this.refreshDataObject();
        break;
      case 'comboType':
        this.updateAnalysis();
        this.refreshDataObject();
        break;
      case 'labelOptions':
        isDSLAnalysis(this.analysis)
          ? this._store.dispatch(
              new DesignerUpdateAnalysisChartLabelOptions(
                event.data.labelOptions
              )
            )
          : ((<any>this.analysis).labelOptions = event.data.labelOptions);
        this.auxSettings = { ...this.auxSettings, ...event.data };
        this.refreshDataObject();
        break;
      case 'legend':
        if (!event.data || !event.data.legend) {
          return;
        }
        isDSLAnalysis(this.analysis)
          ? this._store.dispatch(
              new DesignerUpdateAnalysisChartLegend(event.data.legend)
            )
          : ((<any>this.analysis).legend = event.data.legend);
        this.auxSettings = { ...this.auxSettings, ...event.data };
        this.artifacts = [...this.artifacts];
        break;
      case 'inversion':
        if (!event.data) {
          return;
        }
        isDSLAnalysis(this.analysis)
          ? this._store.dispatch(
              new DesignerUpdateAnalysisChartInversion(event.data.isInverted)
            )
          : ((<any>this.analysis).isInverted = event.data.isInverted);
        this.auxSettings = { ...this.auxSettings, ...event.data };
        this.artifacts = [...this.artifacts];
        break;
      case 'chartTitle':
        if (!event.data) {
          return;
        }
        isDSLAnalysis(this.analysis)
          ? this._store.dispatch(
              new DesignerUpdateAnalysisChartTitle(event.data.title)
            )
          : ((<AnalysisChart>this.analysis).chartTitle = event.data.title);
        this.auxSettings = { ...this.auxSettings, ...event.data };
        this.artifacts = [...this.artifacts];
        break;
      case 'mapSettings':
        this.auxSettings = event.data.mapSettings;
        (<AnalysisMapDSL>this.analysis).mapOptions = this.auxSettings;
        break;
      case 'fetchLimit':
        (<Analysis>this.analysis).sqlBuilder = this.getSqlBuilder();
        this.requestDataIfPossible();
        break;
      case 'geoRegion':
        this.updateAnalysis();
        this.refreshDataObject();
        break;
      case 'chartType':
        this.changeSubType(event.data);
        this._store.dispatch(new DesignerClearGroupAdapters());
        this._store.dispatch(new DesignerInitGroupAdapters());
        setTimeout(() => {
          this.resetAnalysis();
        });
        break;
    }
  }

  openDerivedMetricDialog(artifactColumn: ArtifactColumn) {
    const dialogRef = this.dialog.open(DerivedMetricComponent, {
      width: '60%',
      height: '60%',
      autoFocus: false,
      data: { artifactColumn, columns: this.artifacts[0].columns }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const column = {
          ...artifactColumn,
          ...result,
          dataField: result.columnName,
          table: get(this.artifacts[0], 'artifactName'),
          type: 'double'
        };

        const existing = find(
          this.artifacts[0].columns,
          col => col.columnName === result.columnName
        );
        if (existing) {
          this.handleOtherChangeEvents({
            subject: 'expressionUpdated',
            column
          });
        } else {
          this.handleOtherChangeEvents({
            subject: 'derivedMetricAdded',
            column
          });
        }
      }
    });
  }

  changeSubType(to: string) {
    if (isDSLAnalysis(this.analysis)) {
      this._store.dispatch(new DesignerUpdateAnalysisSubType(to));
      isDSLAnalysis(this.analysis);
      if (this.analysis.type === 'chart') {
        this._store.dispatch(
          new DesignerUpdateAnalysisChartInversion(to === 'bar')
        );
      }
    } else {
      (<AnalysisChart>this.analysis).chartType = to;
      (<any>this.analysis).isInverted = to === 'bar';
    }
    this.auxSettings = { ...this.auxSettings, isInverted: to === 'bar' };
    this.auxSettings = {
      ...this.auxSettings,
      labelOptions: {
        enabled: to === 'pie',
        value: to === 'pie' ? 'percentage' : ''
      }
    };
    this.resetArtifacts();
    // this.artifacts = [...this.artifacts];
  }

  resetAnalysis() {
    this.cleanSorts();
    this.sorts = [];
    this.checkNodeForSorts();
    this.designerState = DesignerStates.NO_SELECTION;
    const artifactColumns = this.artifacts[0].columns;
    forEach(artifactColumns, col => {
      col.checked = false;
      unset(col, 'area');
    });
    this.artifacts = [...this.artifacts];
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
    switch (this.analysis.type) {
      case 'pivot':
        const query = this._store.selectSnapshot(
          state => state.designerState.analysis.sipQuery
        );
        const pivotFields = get(query, 'artifacts.0.fields');
        return some(pivotFields, field => field.area === 'data');
      case 'map':
        const selectedFieldsMap = this._store.selectSnapshot(
          DesignerState.allSelectedFields
        );
        const requestConditions = [
          find(
            selectedFieldsMap || [],
            field => field.checked === 'x' || field.area === 'x'
          ),
          find(
            selectedFieldsMap || [],
            field => field.checked === 'y' || field.area === 'y'
          )
        ];
        return every(requestConditions, Boolean);
      case 'chart':
        /* At least one y and x field needs to be present. If this is a bubble chart,
         * at least one z axis field is also needed */
        const sipQuery = this._store.selectSnapshot(
          state => state.designerState.analysis.sipQuery
        );
        const fields = get(sipQuery, 'artifacts.0.fields');
        const requestCondition = [
          find(fields || [], field => field.area === 'y'),
          find(fields || [], field => field.area === 'x'),
          ...(this.analysisSubType === 'bubble'
            ? [find(fields || [], field => field.area === 'x')]
            : [])
        ];
        return every(requestCondition, Boolean);
      case 'report':
      case 'esReport':
        return this.canRequestReport(this.analysis.artifacts);
    }
  }

  canRequestReport(artifacts) {
    const analysis: AnalysisDSL = this._store.selectSnapshot(DesignerState)
      .analysis;
    if (analysis.designerEdit) {
      return Boolean(analysis.sipQuery.query);
    }

    const selectedFields = flatMap(
      analysis.sipQuery.artifacts,
      artifact => artifact.fields
    );
    return selectedFields.length > 0;
  }

  updateAnalysis() {
    const designerEdit = this._store.selectSnapshot(DesignerState).analysis
      .designerEdit;
    const { type, artifacts } = this.analysis;
    if (type === 'report' && designerEdit) {
      // reset checked fields, sorts, and filters for query mode report analysis
      this.filters = [];
      this.sorts = [];
      forEach(artifacts, artifact => {
        forEach(artifact.column, col => (col.checked = false));
      });
    }
    (<Analysis>this.analysis).sqlBuilder = this.getSqlBuilder();
  }

  /**
   * If an artifactColumn is unselected, it should be cleared out from the sorts.
   */
  cleanSorts() {
    const selectedFields = map(
      this._store.selectSnapshot(DesignerState.allSelectedFields),
      field => field.columnName
    );

    this.sorts = filter(this.sorts, sort =>
      selectedFields.includes(sort.columnName)
    );
  }
}
