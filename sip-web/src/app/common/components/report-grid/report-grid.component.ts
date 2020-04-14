import {
  Component,
  Input,
  Output,
  OnInit,
  OnDestroy,
  EventEmitter,
  ViewChild,
  ElementRef
} from '@angular/core';
import { DxDataGridComponent } from 'devextreme-angular/ui/data-grid';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as fpPickBy from 'lodash/fp/pickBy';
import * as fpMap from 'lodash/fp/map';
import * as fpMapValues from 'lodash/fp/mapValues';
import * as fpReduce from 'lodash/fp/reduce';
import * as isUndefined from 'lodash/isUndefined';
import * as fpFilter from 'lodash/fp/filter';
import * as forEach from 'lodash/forEach';
import * as split from 'lodash/split';
import * as isFunction from 'lodash/isFunction';
import * as isEmpty from 'lodash/isEmpty';
import { MatDialog, MatDialogConfig } from '@angular/material';
import CustomStore from 'devextreme/data/custom_store';
import { DateFormatDialogComponent } from '../date-format-dialog';
import { DataFormatDialogComponent } from '../data-format-dialog';
import { AliasRenameDialogComponent } from '../alias-rename-dialog';
import { getFormatter } from '../../utils/numberFormatter';
import { Subscription, BehaviorSubject } from 'rxjs';
import * as filter from 'lodash/filter';
import * as map from 'lodash/map';
import * as isEqual from 'lodash/isEqual';

import {
  AGGREGATE_TYPES,
  AGGREGATE_TYPES_OBJ,
  DATE_FORMATS,
  DATE_FORMATS_OBJ
} from '../../consts';

import {
  ArtifactColumnReport,
  Artifact,
  Sort,
  ReportGridChangeEvent
} from './types';
import { DATE_TYPES, NUMBER_TYPES } from '../../consts';
import { DEFAULT_PRECISION } from '../data-format-dialog/data-format-dialog.component';

import { flattenReportData } from '../../../common/utils/dataFlattener';
import { ArtifactDSL, AnalysisDSL } from 'src/app/models';
import moment from 'moment';

interface ReportGridSort {
  order: 'asc' | 'desc';
  index: number;
}

interface ReportGridField {
  caption: string;
  dataField: string;
  dataType: string;
  type: string;
  visibleIndex: number;
  payload: ArtifactColumnReport;
  visible: boolean;
  allowSorting?: boolean;
  alignment?: 'center' | 'left' | 'right';
  format?: string | object;
  sortOrder?: 'asc' | 'desc';
  sortIndex?: number;
  changeColumnProp: Function;
  headerCellTemplate: string;
}

const DEFAULT_PAGE_SIZE = 25;
const LOAD_PANEL_POSITION_SELECTOR = '.report-dx-grid';

/**
 * Checks the artifacts if there are more than one fields with same
 * column name, and returns an object with those column names
 * for easy lookup.
 *
 * @param {ArtifactDSL[]} artifacts
 * @returns {{[columnName: string]: boolean}}
 * @memberof ReportGridComponent
 */
export const findDuplicateColumns = (
  artifacts: ArtifactDSL[]
): { [columnName: string]: boolean } => {
  return fpPipe(
    /* Flatten fields from all tables in one array */
    fpFlatMap(artifact => artifact.fields),

    /* Create an object with each column name assigned
         to number of times it occurs */
    fpReduce((result, field) => {
      const caption = field.alias || field.columnName;
      result[caption] = result[caption] || 0;
      result[caption]++;
      return result;
    }, {}),

    /* Find column names which occur more than once */
    fpPickBy(repetition => repetition > 1),

    /* We don't need the number of occurences, just the column
         names. So replace the number of occurences with true */
    fpMapValues(() => true)
  )(artifacts);
};

@Component({
  selector: 'report-grid-upgraded',
  templateUrl: './report-grid.component.html',
  styleUrls: ['./report-grid.component.scss']
})
export class ReportGridComponent implements OnInit, OnDestroy {
  public columns: ReportGridField[];
  public data;
  analysis: AnalysisDSL;
  private listeners: Array<Subscription> = [];
  @Output() change: EventEmitter<ReportGridChangeEvent> = new EventEmitter();
  @ViewChild(DxDataGridComponent, { static: true })
  dataGrid: DxDataGridComponent;
  @Input() query: string;
  @Input() isInQueryMode;
  @Input('analysis') set _analysis(analysis: AnalysisDSL) {
    if (!analysis) {
      return;
    }
    this.analysis = analysis;

    this.duplicateColumns = findDuplicateColumns(
      this.analysis.sipQuery.artifacts
    );
  }
  @Input() dimensionChanged: BehaviorSubject<any>;
  @Input('sorts')
  set setSorts(sorts: Sort[]) {
    this.sorts = fpReduce(
      (acc, sort, index) => {
        const reportGirdSort: ReportGridSort = {
          order: sort.order,
          index
        };
        acc[sort.columnName] = reportGirdSort;
        return acc;
      },
      {},
      sorts
    );
  }
  @Input('artifacts')
  set setArtifactColumns(artifacts: ArtifactDSL[]) {
    if (!artifacts) {
      this.artifacts = null;
      this.columns = null;
      return;
    }
    if (isEqual(this.artifacts, artifacts)) {
      /* If artifacts are equal to what is currently set, don't proceeding.
         If we proceed, the columns will be reset to new javascript object.
         This will cause issues with things such as when we sort by clicking on column names.
         This is because setting new columns will always call CustomStore.load again,
         thereby clearing report column header sort user selects.
      */
      return;
    }
    this.artifacts = artifacts;
    this.duplicateColumns = findDuplicateColumns(this.artifacts);
    this.columns = this.artifacts2Columns(this.artifacts);
    // if there are less then 5 columns, divide the grid up into equal slices for the columns
    if (this.columns.length > 5) {
      this.columnAutoWidth = true;
    }

    if (isEmpty(this.columns)) {
      this.columns = null;
    }
  }
  @Input('queryColumns')
  set setQueryColumns(queryColumns) {
    // TODO merge with SAW - 2002 for queryColumns
    // for query mode
    this.columns = this.queryColumns2Columns(queryColumns);
  }
  @Input('data')
  set setData(data: any[]) {
    if (data || data.length < 7) {
      this.gridHeight = 'auto';
    } else {
      this.gridHeight = '100%';
    }

    if (!this.isInQueryMode) {
      const artifact = this.fetchColumsUponCheck();
      if (!this.artifacts) {
        return;
      }
      this.columns = this.artifacts2Columns(artifact);
    }
    this.data = transformDateFields(
      flattenReportData(data, this.analysis),
      this.columns
    );
  }
  @Input('dataLoader')
  set setDataLoader(
    dataLoader: (options: {}) => Promise<{ data: any[]; totalCount: number }>
  ) {
    // setup pagination for paginated data
    if (isFunction(dataLoader)) {
      this.dataLoader = dataLoader;
      this.data = new CustomStore({
        load: options =>
          this.dataLoader(options).then(value => {
            return {
              data: transformDateFields(
                flattenReportData(value.data, this.analysis),
                this.columns
              ),
              totalCount: value.totalCount
            };
          })
      });
      this.remoteOperations = { paging: true };
      /* Reset pager after a new dataLoader is set */
      this.paging = { pageSize: DEFAULT_PAGE_SIZE, pageIndex: 0 };
    } else {
      throw new Error('Data loader requires a Function');
    }
  }
  @Input() isEditable = false;
  @Input() columnHeaders;

  public dataLoader: (options: {}) => Promise<{
    data: any[];
    totalCount: number;
  }>;

  public duplicateColumns: { [columnName: string]: boolean } = {};
  public sorts: {};
  public artifacts: ArtifactDSL[];
  public pageSize: number = DEFAULT_PAGE_SIZE;
  public isColumnChooserListenerSet = false;
  public onLoadPanelShowing: Function;

  // grid settings
  public columnAutoWidth = false;
  public columnMinWidth = 172;
  public columnResizingMode = 'widget';
  public allowColumnReordering = true;
  public allowColumnResizing = true;
  public showColumnHeaders = true;
  public showColumnLines = false;
  public showRowLines = false;
  public showBorders = false;
  public rowAlternationEnabled = true;
  public hoverStateEnabled = true;
  public wordWrapEnabled = false;
  public scrolling = { mode: 'Virtual' };
  public sorting = { mode: 'multiple' };
  public columnChooser;
  public gridWidth = '100%';
  public gridHeight: '100%' | 'auto' = '100%';
  public remoteOperations;
  public paging;
  public pager;
  public loadPanel;
  public AGGREGATE_TYPES_OBJ = AGGREGATE_TYPES_OBJ;
  public aggregates;
  public isQueryMode;

  constructor(private _dialog: MatDialog, public _elemRef: ElementRef) {
    this.onLoadPanelShowing = ({ component }) => {
      const instance = this.dataGrid.instance;
      if (instance) {
        const elem =
          this.pageSize > DEFAULT_PAGE_SIZE
            ? window
            : this._elemRef.nativeElement.querySelector(
                LOAD_PANEL_POSITION_SELECTOR
              );
        component.option('position.of', elem);
        this.pageSize = instance.pageSize();
      }
    };
    this.customizeColumns = this.customizeColumns.bind(this);
  }

  ngOnInit() {
    if (this.dimensionChanged) {
      this.listeners.push(this.subscribeForRepaint());
    }

    this.pager = {
      showNavigationButtons: true,
      allowedPageSizes: [DEFAULT_PAGE_SIZE, 50, 75, 100],
      showPageSizeSelector: true
    };

    // disable editing if needed
    if (!this.isEditable) {
      this.columnChooser = {
        enabled: isUndefined(this.columnHeaders) ? true : this.columnHeaders,
        mode: 'select'
      };

      // paging is used in situations where the grid is not editable
      this.loadPanel = {
        onShowing: this.onLoadPanelShowing,
        position: {
          of: this._elemRef.nativeElement.querySelector(
            LOAD_PANEL_POSITION_SELECTOR
          ),
          at: 'center',
          my: 'center'
        }
      };
    }
  }

  ngOnDestroy() {
    this.listeners.forEach(sub => sub.unsubscribe());
  }

  isAggregateEligible() {
    return filter(AGGREGATE_TYPES, aggregate => {
      if (aggregate.valid.includes(this.analysis.type)) {
        return true;
      }
    });
  }

  onContentReady({ component }) {
    if (this.isEditable) {
      this.updateVisibleIndices(component);
    } else {
      if (!this.isColumnChooserListenerSet) {
        this.setColumnChooserOptions(component);
        this.isColumnChooserListenerSet = true;
      }
    }
  }

  subscribeForRepaint() {
    return this.dimensionChanged.subscribe(() => {
      this.dataGrid &&
        this.dataGrid.instance &&
        this.dataGrid.instance.repaint();
    });
  }

  customizeColumns(columns) {
    forEach(columns, (col: ReportGridField) => {
      col.allowSorting = !this.isEditable;
      col.alignment = 'left';
    });
  }

  /** Update the visible indices when the column order changes */
  updateVisibleIndices(component) {
    if (!this.columns) {
      return;
    }
    const colsAffectedFromReorder = [];

    const cols = component.getVisibleColumns();
    forEach(cols, (col: ReportGridField) => {
      if (col.visibleIndex !== col.payload.visibleIndex) {
        col.changeColumnProp('visibleIndex', col.visibleIndex);
        colsAffectedFromReorder.push({
          columnName: col.payload.columnName,
          table: col.payload.table || col.payload['tableName'],
          visibleIndex: col.visibleIndex
        });
      }
    });

    colsAffectedFromReorder.forEach(col =>
      this.change.emit({
        subject: 'reorder',
        column: col
      })
    );
  }

  /** Column chooser should be closed when a click outside of it appears */
  setColumnChooserOptions(component) {
    const columnChooserView = component.getView('columnChooserView');
    if (!columnChooserView._popupContainer) {
      columnChooserView._initializePopupContainer();
      columnChooserView.render();
      columnChooserView._popupContainer._options.closeOnOutsideClick = true;
    }
  }

  fetchAggregation(type) {
    if (NUMBER_TYPES.includes(type)) {
      this.aggregates = this.isAggregateEligible();
    } else {
      this.aggregates = filter(AGGREGATE_TYPES, t => {
        return t.value === 'count' || t.value.toLowerCase() === 'distinctcount';
      });
    }
  }

  checkFormatDataCondition(type) {
    if (NUMBER_TYPES.includes(type) || DATE_TYPES.includes(type)) {
      return true;
    } else {
      return false;
    }
  }

  aggregateColumn(payload, value) {
    if (value === 'clear') {
      if (isUndefined(payload.aggregate)) {
        return;
      }
      delete payload.aggregate;
    } else {
      payload.aggregate = value === 'distinctcount' ? 'distinctCount' : value;
    }
    this.change.emit({
      subject: 'aggregate',
      column: payload
    });
  }

  removeColumn({ changeColumnProp, payload }: ReportGridField) {
    changeColumnProp('checked', false);
    this.change.emit({
      subject: 'removeColumn',
      column: payload
    });
  }

  /**
   * Renames the column and updates caption of the cell.
   *
   * @param {ReportGridField} { payload, changeColumnProp }
   * @param {number} columnIndex
   * @memberof ReportGridComponent
   */
  renameColumn(
    { payload, changeColumnProp }: ReportGridField,
    columnIndex: number
  ) {
    this.getNewDataThroughDialog(
      AliasRenameDialogComponent,
      { alias: payload.alias || '' },
      alias => {
        changeColumnProp('alias', alias);
        payload.alias = alias;
        this.dataGrid.instance.columnOption(
          columnIndex,
          'caption',
          alias || payload.displayName
        );
        this.change.emit({
          subject: 'alias',
          column: payload
        });
      }
    );
  }

  formatColumn({ type, changeColumnProp, payload }: ReportGridField) {
    let component;
    if (NUMBER_TYPES.includes(type)) {
      component = DataFormatDialogComponent;
    } else if (DATE_TYPES.includes(type)) {
      component = DateFormatDialogComponent;
    }

    this.getNewDataThroughDialog(
      component,
      {
        format: payload.format || payload.dateFormat,
        type,
        availableFormats: DATE_FORMATS
      },
      format => {
        changeColumnProp('format', format);
        this.change.emit({
          subject: 'format',
          column: {
            ...payload,
            ...(payload.type === 'date' ? { dateFormat: format } : { format })
          }
        });
      }
    );
  }

  getNewDataThroughDialog(component, currentData, actionFn: Function) {
    this._dialog
      .open(component, {
        width: 'auto',
        height: 'auto',
        autoFocus: false,
        data: currentData
      } as MatDialogConfig)
      .afterClosed()
      .subscribe(newValue => {
        if (!isUndefined(newValue)) {
          actionFn(newValue);
        }
      });
  }

  artifacts2Columns(artifacts: ArtifactDSL[]): ReportGridField[] {
    return fpPipe(
      fpFlatMap(
        (artifact: Artifact | ArtifactDSL) =>
          (<ArtifactDSL>artifact).fields ||
          (<Artifact>artifact).columns || [artifact]
      ),
      fpMap((column: ArtifactColumnReport) => {
        let isNumberType = NUMBER_TYPES.includes(column.type);

        const aggregate =
          AGGREGATE_TYPES_OBJ[
            column.aggregate && column.aggregate.toLowerCase()
          ];
        let type = column.type;
        if (
          aggregate &&
          ['count', 'distinctcount'].includes(
            aggregate.value && aggregate.value.toLowerCase()
          )
        ) {
          type = aggregate.type || column.type;
          isNumberType = true;
        }

        const preprocessedFormat = this.preprocessFormatIfNeeded(
          column.format,
          type,
          column.aggregate
        );

        const format = isNumberType
          ? { formatter: getFormatter(preprocessedFormat) }
          : this.getDateFormat(column.format || column.dateFormat);

        const dataType = isNumberType ? 'number' : type;
        const field: ReportGridField = {
          caption: column.alias || column.displayName,
          dataField: this.getDataField(column),
          dataType,
          type,
          visibleIndex: column.visibleIndex,
          visible: isUndefined(column.visible) ? true : column.visible,
          payload: column,
          format,
          headerCellTemplate: 'headerCellTemplate',
          changeColumnProp: (prop, value) => {
            column[prop] = value;
          },
          ...this.getSortingPart(column)
        };

        if (
          DATE_TYPES.includes(column.type) &&
          isUndefined(column.format) &&
          isUndefined(column.dateFormat) &&
          !aggregate
        ) {
          field.format = 'yyyy-MM-dd';
        }
        return field;
      })
    )(artifacts);
  }

  getDateFormat(format) {
    if (!format) {
      return format;
    }
    const { momentValue, momentFormatFrombackend } = DATE_FORMATS_OBJ[format];
    return {
      formatter: value => {
        const formatted = moment
          .utc(value)
          .format(momentFormatFrombackend || momentValue);
        return formatted;
      }
    };
  }

  preprocessFormatIfNeeded(format, type, aggregate) {
    const isPercentage = aggregate === 'percentage';
    const isAVG = aggregate === 'avg';
    if ((!isPercentage && !isAVG) || !NUMBER_TYPES.includes(type)) {
      return format;
    }

    return {
      ...format,
      precision: DEFAULT_PRECISION,
      percentage: isPercentage
    };
  }

  getDataField(column: ArtifactColumnReport) {
    return column.alias && this.analysis.type === 'report'
      ? column.alias
      : split(column.columnName, '.')[0];
  }

  queryColumns2Columns(queryColumns): ReportGridField[] {
    return [];
  }

  getSortingPart(column: ArtifactColumnReport) {
    const sort = this.sorts[column.columnName];
    if (sort) {
      return {
        sortIndex: sort.index,
        sortOrder: sort.order
      };
    }
    return {};
  }

  fetchColumsUponCheck() {
    const artifacts = this.analysis.sipQuery
      ? this.analysis.sipQuery.artifacts
      : this.analysis.artifacts;
    return map(artifacts, artifact => {
      if (this.analysis.sipQuery) {
        return artifact;
      }

      const columns = filter(artifact.columns, 'checked');
      return {
        ...artifact,
        columns
      };
    });
  }
}

function transformDateFields(data, fields: ReportGridField[]) {
  const columnNames = fpPipe(
    fpFilter(field => DATE_TYPES.includes(field.type)),
    fpMap(field => field.dataField)
  )(fields);

  if (isEmpty(columnNames)) {
    return data;
  }

  forEach(data, dataPoint => {
    forEach(columnNames, columnName => {
      const date = moment.utc(dataPoint[columnName], 'YYYY-MM-DD hh:mm:ss');
      dataPoint[columnName] = date;
    });
  });
  return data;
}
