import {saveAs} from 'file-saver';
import * as Blob from 'blob';

export function fileService() {

  return {
    exportCSV
  };

  function exportCSV(str) {
    const blob = new Blob([str], {type: 'text/csv;charset=utf-8'});
    saveAs(blob, 'export.csv');
  }
}
