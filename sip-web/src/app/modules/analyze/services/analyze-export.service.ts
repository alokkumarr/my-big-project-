import { Injectable } from '@angular/core';
import * as fpPick from 'lodash/fp/pick';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpMap from 'lodash/fp/map';
import * as sortBy from 'lodash/sortBy';
import * as map from 'lodash/map';
import * as flatMap from 'lodash/flatMap';
import * as replace from 'lodash/replace';
import * as indexOf from 'lodash/indexOf';
import * as slice from 'lodash/slice';
import { json2csv } from 'json-2-csv';
import * as keys from 'lodash/keys';
import * as forEach from 'lodash/forEach';
import { saveAs } from 'file-saver';
import * as Blob from 'blob';
import * as get from 'lodash/get';

import { AnalyzeActionsService } from '../actions';
import { ToastService } from '../../../common/services/toastMessage.service';
import { wrapFieldValues } from './../../../common/utils/dataFlattener';
import { alterDateInData } from './../../../common/utils/dataFlattener';
import { isDSLAnalysis } from '../designer/types';

@Injectable()
export class AnalyzeExportService {
  constructor(
    public _analyzeActionsService: AnalyzeActionsService,
    public _toastMessage: ToastService
  ) {}

  export(analysis, executionId, executionType = 'normal') {
    const analysisId = analysis.id;
    const analysisType = analysis.type;
    this._analyzeActionsService
      .exportAnalysis(analysisId, executionId, analysisType, executionType)
      .then(data => {
        let exportData = get(data, 'data');
        let fields = this.getCheckedFieldsForExport(analysis, exportData);
        fields = this.cleanColumnNames(fields);
        const columnNames = map(fields, 'columnName');
        const exportOptions = {
          trimHeaderFields: false,
          emptyFieldValue: 'null',
          checkSchemaDifferences: false,
          delimiter: {
            wrap: '',
            eol: '\r\n'
          },
          keys: columnNames
        };

        exportData = wrapFieldValues(exportData);
        exportData = ['report', 'esReport'].includes(analysisType)
          ? alterDateInData(exportData, analysis.sipQuery)
          : exportData;

        json2csv(
          exportData,
          (err, csv) => {
            if (err) {
              this._toastMessage.error(
                'There was an error while exporting, please try again witha different dataset.'
              );
            }
            const csvWithDisplayNames = this.replaceCSVHeader(
              csv,
              fields,
              analysis
            );
            // const csvWithUnwrappedNulls = this.unwrapNulls(csvWithDisplayNames);
            this.exportCSV(csvWithDisplayNames, analysis.name);
          },
          exportOptions
        );
      });
  }

  unwrapNulls(csv) {
    return replace(csv, /"null"/g, 'null');
  }

  /**
   * Replaces columnName with displayName or alias in column headers to make it
   * more user friendly.
   *
   * @param {*} csv
   * @param {*} fields
   * @param {*} analysis
   * @returns
   * @memberof AnalyzeExportService
   */
  replaceCSVHeader(csv, fields, analysis) {
    const firstNewLine = indexOf(csv, '\n');
    const firstRow = slice(csv, 0, firstNewLine).join('');
    const firstRowColumns = firstRow
      .split(',')
      .map(columnName => columnName.replace(/"/g, '').trim());

    /* Following logic replaces column names in CSV header row with their
       display names or aliases, while preserving the order they appear in
      */
    const displayNames = firstRowColumns
      .map(columnName => {
        const field = fields.find(f => f.columnName === columnName);
        if (!field) {
          return `"${columnName}`;
        }
        if (field.aggregate === 'distinctCount' && analysis.type === 'report') {
          return `"distinctCount(${field.alias || field.displayName})"`;
        }
        return `"${field.alias || field.displayName}"`;
      })
      .join(',');
    return replace(csv, firstRow, displayNames);
  }

  /**
   * Returns all the fields that are selected by user.
   *
   * @param {*} analysis
   * @param {*} data
   * @returns
   * @memberof AnalyzeExportService
   */
  getCheckedFieldsForExport(analysis, data) {
    /* If report was using designer mode, find checked columns */
    if (!analysis.designerEdit && isDSLAnalysis(analysis)) {
      return sortBy(
        flatMap(analysis.sipQuery.artifacts, artifact =>
          fpMap(
            fpPick([
              'columnName',
              'alias',
              'displayName',
              'aggregate',
              'visibleIndex'
            ]),
            artifact.fields
          )
        ),
        ['visibleIndex']
      );
    } else if (!analysis.designerEdit) {
      return flatMap(analysis.sqlBuilder.dataFields, artifact =>
        fpPipe(
          fpMap(fpPick(['columnName', 'alias', 'displayName', 'aggregate']))
        )(artifact.columns)
      );
    }
    /* If report was using sql mode, we don't really have any info
       about columns. Keys from individual data nodes are used as
       column names */
    if (data.length > 0) {
      return map(keys(data[0]), col => ({
        label: col,
        columnName: col,
        displayName: col,
        type: 'string'
      }));
    }
  }

  /**
   * Returns the column name without the .keyword in it.
   *
   * @param {*} columns
   * @returns
   * @memberof AnalyzeExportService
   */
  cleanColumnNames(columns) {
    forEach(columns, column => {
      column.columnName = (column.columnName || '').replace('.keyword', '');
    });
    return columns;
  }

  exportCSV(str, fileName) {
    const blob = new Blob([str], { type: 'text/csv;charset=utf-8' });
    saveAs(blob, `${fileName || 'export'}.csv`);
  }
}
