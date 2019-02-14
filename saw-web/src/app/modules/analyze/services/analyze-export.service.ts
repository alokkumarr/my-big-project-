import { Injectable } from '@angular/core';
import * as fpPick from 'lodash/fp/pick';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpMap from 'lodash/fp/map';
import * as map from 'lodash/map';
import * as flatMap from 'lodash/flatMap';
import * as replace from 'lodash/replace';
import * as indexOf from 'lodash/indexOf';
import * as slice from 'lodash/slice';
import { json2csv } from 'json-2-csv';
import * as keys from 'lodash/keys';
import * as forEach from 'lodash/forEach';
import * as isUndefined from 'lodash/isUndefined';
import { saveAs } from 'file-saver';
import * as Blob from 'blob';
import * as get from 'lodash/get';

import { AnalyzeActionsService } from '../actions';
import { ToastService } from '../../../common/services/toastMessage.service';

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
        const exportData = get(data, 'data');
        let fields = this.getCheckedFieldsForExport(analysis, exportData);
        fields = this.checkColumnName(fields);
        const columnNames = map(fields, 'columnName');
        const exportOptions = {
          trimHeaderFields: false,
          emptyFieldValue: '',
          checkSchemaDifferences: false,
          delimiter: {
            wrap: '"',
            eol: '\r\n'
          },
          columnNames
        };
        json2csv(
          exportData,
          (err, csv) => {
            if (err) {
              this._toastMessage.error(
                'There was an error while exporting, please try again witha different dataset.'
              );
            }
            const csvWithDisplayNames = this.replaceCSVHeader(csv, fields, analysis);
            this.exportCSV(csvWithDisplayNames, analysis.name);
          },
          exportOptions
        );
      });
  }

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
          return columnName;
        }
        if (field.aggregate === 'distinctCount' && analysis.type === 'report') {
          return `distinctCount(${field.aliasName || field.displayName})`;
        }
        return field.aliasName || field.displayName;
      })
      .join(',');
    return replace(csv, firstRow, displayNames);
  }

  getCheckedFieldsForExport(analysis, data) {
    /* If report was using designer mode, find checked columns */
    if (!analysis.edit) {
      return flatMap(analysis.sqlBuilder.dataFields, artifact =>
        fpPipe(fpMap(fpPick(['columnName', 'aliasName', 'displayName', 'aggregate'])))(
          artifact.columns
        )
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

  checkColumnName(columns) {
    forEach(columns, column => {
      column.columnName = this.getColumnName(column.columnName);
    });
    return columns;
  }

  getColumnName(columnName) {
    // take out the .keyword form the columnName
    // if there is one
    if (!isUndefined(columnName)) {
      const split = columnName.split('.');
      if (split[1]) {
        return split[0];
      }
      return columnName;
    }
  }

  exportCSV(str, fileName) {
    const blob = new Blob([str], { type: 'text/csv;charset=utf-8' });
    saveAs(blob, `${fileName || 'export'}.csv`);
  }
}
