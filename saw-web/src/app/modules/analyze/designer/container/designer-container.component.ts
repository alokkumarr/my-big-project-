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
import * as isNumber from 'lodash/isNumber';
import * as every from 'lodash/every';
import * as forEach from 'lodash/forEach';
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
  AnalysisReport,
  MapSettings,
  isDSLAnalysis
} from '../types';
import { AnalysisDSL, LabelOptions } from '../../../../models';
import {
  DesignerStates,
  FLOAT_TYPES,
  DEFAULT_PRECISION,
  DATE_TYPES,
  DEFAULT_MAP_SETTINGS
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
  DesignerUpdateAnalysisChartType,
  DesignerUpdateSorts,
  DesignerUpdateFilters,
  DesignerUpdatebooleanCriteria,
  DesignerLoadMetric,
  DesignerResetState
} from '../actions/designer.actions';
import { DesignerState } from '../state/designer.state';
import { CUSTOM_DATE_PRESET_VALUE } from './../../consts';

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
  @Select(state => state.designerState.analysis) dslAnalysis$: Observable<
    AnalysisDSL
  >;
  dslSorts$: Observable<Sort[]> = this.dslAnalysis$.pipe(
    map$(analysis => analysis.sipQuery.sorts)
  );
  chartType$: Observable<string> = this.dslAnalysis$.pipe(
    map$(analysis => analysis.chartOptions.chartType)
  );

  public isInDraftMode = false;
  public designerState: DesignerStates;
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
  public fieldCount: number;
  private subscriptions: Subscription[] = [];
  // minimum requirments for requesting data, obtained with: canRequestData()
  public areMinRequirmentsMet = false;

  get chartType(): string {
    return isDSLAnalysis(this.analysis)
      ? this._store.selectSnapshot(DesignerState).analysis.chartOptions
          .chartType
      : (<AnalysisChart>this.analysis).chartType;
  }

  constructor(
    public _designerService: DesignerService,
    public _analyzeDialogService: AnalyzeDialogService,
    public _chartService: ChartService,
    public _analyzeService: AnalyzeService,
    private _store: Store,
    private _jwtService: JwtService
  ) {
    window['designer'] = this;
    window['DesignerState'] = DesignerState;
  }

  ngOnDestroy() {
    this.subscriptions.forEach(s => s.unsubscribe());
    this._store.dispatch(new DesignerResetState());
  }

  ngOnInit() {
    const analysisType: string =
      get(this.analysis, 'type') || get(this.analysisStarter, 'type');
    const isReport = ['report', 'esReport'].includes(analysisType);
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
      isDSLAnalysis(this.analysis) &&
        this._store.dispatch(new DesignerInitEditAnalysis(this.analysis));
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
      break;
    default:
      break;
    }
    this.dslAnalysis$.subscribe(analysis => {
      if (!analysis || ['report', 'esReport'].includes(analysisType)) {
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

  getLayoutConfiguration(analysis: Analysis | AnalysisStarter | AnalysisDSL) {
    /* prettier-ignore */
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

  initNewAnalysis() {
    const { type, semanticId } = this.analysisStarter;
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
        this.analysis = {
          ...this.analysisStarter,
          ...(newAnalysis['analysis'] || newAnalysis),
          artifacts
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
            new DesignerUpdateAnalysisChartType(this.analysisStarter.chartType)
          );
        }
        this.artifacts = this.fixLegacyArtifacts(this.analysis.artifacts);
        this.initAuxSettings();
        unset(this.analysis, 'categoryId');
      }
    );
  }

  generateDSLDateFilters(filters) {
    forEach(filters, filtr => {
      if (filtr.type === 'date' && filtr.model.operator === 'BTW') {
        filtr.model.gte = moment(filtr.model.value).format('YYYY-MM-DD');
        filtr.model.lte = moment(filtr.model.otherValue).format('YYYY-MM-DD');
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
    this.filters = queryBuilder.filters;
    this.sorts = queryBuilder.sorts || queryBuilder.orderByColumns;
    this.booleanCriteria = queryBuilder.booleanCriteria;
    this.isInQueryMode = this.analysis.edit;

    this.initAuxSettings();

    this.addDefaultSorts();
    this.areMinRequirmentsMet = this.canRequestData();
  }

  initAuxSettings() {
    /* prettier-ignore */
    switch (this.analysis.type) {
      case 'chart':
        this.addDefaultLabelOptions();
        if (this.designerMode === 'new') {
          if (isDSLAnalysis(this.analysis)) {
            this._store.dispatch(new DesignerUpdateAnalysisChartInversion(this.chartType === 'bar'));
          } else {
            (<any>this.analysis).isInverted = this.chartType === 'bar';
          }
        }
        const chartOptions = this._store.selectSnapshot(state => state.designerState.analysis.chartOptions);
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
        const mapOnlySettings: MapSettings = DEFAULT_MAP_SETTINGS;
        this.auxSettings = {
          ...this.auxSettings,
          ...mapOnlySettings
        };
        (<AnalysisDSL>this.analysis).mapOptions = this.auxSettings;
        break;
    }
  }

  addDefaultLabelOptions() {
    if (this.analysis.type !== 'chart' || this.designerMode !== 'new') {
      return;
    }

    let labelOptions: LabelOptions;

    switch (this.chartType) {
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
    /* prettier-ignore */
    switch (this.analysis.type) {
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

    case 'report':
      forEach(artifacts, table => {
        table.columns = map(table.columns, column => {
          forEach((<Analysis>this.analysis).sqlBuilder.dataFields, fields => {
            forEach(fields.columns, field => {
              if (field.columnName === column.columnName) {
                column.checked = true;
              }
            });
          });
          return column;
        });
      });
      break;
    }
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
          dataField.aggregate === 'percentageByRow'
            ? 'percentage'
            : dataField.aggregate;
      });

      forEach(this.artifacts[0].columns, col => {
        col.aggregate =
          col.aggregate === 'percentageByRow' ? 'percentage' : col.aggregate;
      });
    }
    return analysis;
  }

  requestDataIfPossible() {
    this.areMinRequirmentsMet = this.canRequestData();
    if (this.areMinRequirmentsMet) {
      /* If it's a DSL analysis, since we're depending on group adapters
         to generate sipQuery, wait until group adapters are loaded before
         requesting data.
         */
      if (isDSLAnalysis(this.analysis)) {
        const subscription = this._store
          .select(DesignerState.groupAdapters)
          .pipe(
            takeWhile(adapters => isEmpty(adapters)),
            finalize(() => {
              this.requestData();
            })
          )
          .subscribe();
        this.subscriptions.push(subscription);
      } else {
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
      if (field.checked === 'y' || field.area === 'y') {
        this.fieldCount++;
      }

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
    this.designerState = DesignerStates.SELECTION_WAITING_FOR_DATA;
    this.fieldCount = 0;

    const requestAnalysis = isDSLAnalysis(this.analysis)
      ? this.dslAnalysisForRequest()
      : this.nonDSLAnalysisForRequest(this.analysis);

    this._designerService.getDataForAnalysis(requestAnalysis).then(
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
          this.data = this.setEmptyData();
        } else {
          this.designerState = DesignerStates.SELECTION_WITH_DATA;
          this.dataCount = response.count;
          this.data = this.flattenData(response.data, this.analysis);
          if (this.analysis.type === 'report' && response.designerQuery) {
            (this.analysis as AnalysisReport).queryManual =
              response.designerQuery;

            (this.analysis as AnalysisReport).query = response.designerQuery;
          }
        }
      },
      err => {
        this.designerState = DesignerStates.SELECTION_WITH_NO_DATA;
        this.data = [];
      }
    );
  }

  flattenData(data, analysis: Analysis | AnalysisDSL) {
    /* prettier-ignore */
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
    /* prettier-ignore */
    switch (action) {
    case 'sort':
      // TODO update sorts for multiple artifacts
      this._analyzeDialogService.openSortDialog(this.sorts, this.artifacts)
        .afterClosed().subscribe((result: IToolbarActionResult) => {
          if (result) {
            this._store.dispatch(new DesignerUpdateSorts(result.sorts));
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
            this._store.dispatch(new DesignerUpdateFilters(result.filters));
            this.filters = result.filters;
            this._store.dispatch(new DesignerUpdatebooleanCriteria(result.booleanCriteria));
            this.booleanCriteria = result.booleanCriteria;
            this.onSettingsChange({ subject: 'filter' });
          }
        });
      break;
    case 'preview':
      this._analyzeDialogService.openPreviewDialog(<Analysis>this.analysis);
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
            analysis: result.analysis.type === 'report' ?
              this._designerService.generateReportPayload(cloneDeep(result.analysis)) :
              result.analysis
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
      if (this.isInQueryMode && !this.analysis.edit) {
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
    if (isDSLAnalysis(this.analysis) && this.designerMode === 'new') {
      this._store.dispatch(
        new DesignerUpdateAnalysisMetadata({
          category: this._jwtService.userAnalysisCategoryId
        })
      );
    } else if (this.designerMode === 'new') {
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
    case 'map':
      partialSqlBuilder = this._designerService.getPartialChartSqlBuilder(this.artifacts[0].columns);
      sortProp = 'sorts';
      break;
    case 'pivot':
      partialSqlBuilder = this._designerService.getPartialPivotSqlBuilder(this.artifacts[0].columns);
      sortProp = 'sorts';
      break;
    case 'esReport':
      partialSqlBuilder = {
        dataFields: this._designerService.generateReportDataField(this.artifacts)
      };
      sortProp = 'sorts';
      break;
    case 'report':
      partialSqlBuilder = {
        dataFields: this._designerService.generateReportDataField(this.artifacts),
        joins: ((<Analysis>this.analysis).sqlBuilder as SqlBuilderReport).joins
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
    case 'map':
      const parsedData = flattenChartData(data, sqlBuilder);
      return isEmpty(parsedData);
    // TODO verify if the object returned is empty
    case 'report':
    case 'esReport':
      return isEmpty(data);
    }
  }

  checkifSortsApplied(event) {
    const query = isDSLAnalysis(this.analysis)
      ? this.analysis.sipQuery
      : this.analysis.sqlBuilder;
    forEach(query.orderByColumns, field => {
      if (event.column.columnName === field.columnName) {
        field.aggregate = event.column.aggregate;
      }
    });
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
    case 'map':
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
      this.loadGridWithoutData(event.column, 'add');
      break;
    case 'removeColumn':
      this.cleanSorts();
      this.setColumnPropsToDefaultIfNeeded(event.column);
      this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
      this.artifacts = [...this.artifacts];
      this.artifacts = this.fixLegacyArtifacts(this.analysis.artifacts);
      this.loadGridWithoutData(event.column, 'remove');
      break;
    case 'aggregate':
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
      this.checkifSortsApplied(event);

      this.areMinRequirmentsMet = this.canRequestData();
      this.designerState = DesignerStates.SELECTION_OUT_OF_SYNCH_WITH_DATA;
      break;
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
      if (isDSLAnalysis(this.analysis)) {
        this.analysis.sipQuery = {...this.analysis.sipQuery};
      } else {
        this.analysis.sqlBuilder = {...this.analysis.sqlBuilder};
      }
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
        this.artifacts = [...this.artifacts];
        this.checkNodeForSorts();
        this.areMinRequirmentsMet = this.canRequestData();
        this.requestDataIfPossible();
        break;
      case 'dateInterval':
      case 'aggregate':
      case 'filter':
      case 'format':
        this.artifacts = [...this.artifacts];
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
          new DesignerUpdateAnalysisChartLabelOptions(event.data.labelOptions)
        )
      : (<any>this.analysis).labelOptions = event.data.labelOptions;
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
        : (<any>this.analysis).legend = event.data.legend;
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
        (<AnalysisDSL>this.analysis).mapOptions = this.auxSettings;
        break;
      case 'fetchLimit':
        (<Analysis>this.analysis).sqlBuilder = this.getSqlBuilder();
        this.requestDataIfPossible();
        break;
      case 'region':
        this.updateAnalysis();
        this.refreshDataObject();
        break;
      case 'chartType':
        this.changeChartType(event.data);
        this._store.dispatch(new DesignerClearGroupAdapters());
        this._store.dispatch(
          new DesignerInitGroupAdapters()
        );
        setTimeout(() => {
          this.resetAnalysis();
        });
        break;
    }
  }

  changeChartType(to: string) {
    if (isDSLAnalysis(this.analysis)) {
      this._store.dispatch(new DesignerUpdateAnalysisChartType(to));
      isDSLAnalysis(this.analysis);
      this._store.dispatch(
        new DesignerUpdateAnalysisChartInversion(to === 'bar')
      );
    } else {
      (<AnalysisChart>this.analysis).chartType = to;
      (<any>this.analysis).isInverted = to === 'bar';
    }

    this.auxSettings = { ...this.auxSettings, isInverted: to === 'bar' };
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
    /* prettier-ignore */
    switch (this.analysis.type) {
    case 'pivot':
      const length = get(this.analysis, 'sqlBuilder.dataFields.length');
      return isNumber(length) ? length > 0 : false;
    case 'map':
      const sqlB = get(this.analysis, 'sqlBuilder') || {};
      const requestConditions = [
        find(sqlB.nodeFields || [], field => field.checked === 'x'),
        find(sqlB.dataFields || [], field => field.checked === 'y')
      ];
      return every(requestConditions, Boolean);
    case 'chart':
      /* At least one y and x field needs to be present. If this is a bubble chart,
       * at least one z axis field is also needed */
      const sqlBuilder = get(this.analysis, 'sqlBuilder') || {};
      const requestCondition = [
        find(sqlBuilder.dataFields || [], field => field.checked === 'y'),
        find(sqlBuilder.nodeFields || [], field => field.checked === 'x'),
        /* prettier-ignore */
        ...(this.chartType === 'bubble' ?
        [
          find(sqlBuilder.dataFields || [], field => field.checked === 'z')
        ] : [])
      ];
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
        forEach(artifact.column, col => (col.checked = false));
      });
    }
    (<Analysis>this.analysis).sqlBuilder = this.getSqlBuilder();
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
