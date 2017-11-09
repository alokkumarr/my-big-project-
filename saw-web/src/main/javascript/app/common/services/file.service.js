import {saveAs} from 'file-saver';
import * as Blob from 'blob';

export function fileService() {

  return {
    exportCSV
  };

  function exportCSV(str, fileName) {
    const blob = new Blob([str], {type: 'text/csv;charset=utf-8'});
    saveAs(blob, `${fileName || 'export'}.csv`);
  }
}
