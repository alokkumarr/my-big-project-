import { Component, Input } from '@angular/core';
import {dxDataGridService} from '../../../../common/services/dxDataGrid.service';

const template = require('./field-attribute-view.component.html');
require('./field-attribute-view.component.scss');

@Component({
  selector: 'field-attribute-view',
  template
})
export class FieldAttributeViewComponent {
  config: any;
  @Input() groupSelected;
  constructor(
    private _dxDataGridService: dxDataGridService
  ) {}

  ngOnInit() {
    this.config = this.getConfig();
  }

  ngOnChanges() {
    console.log(this.groupSelected); // logs undefined
  }

  getConfig() {
    const columns = [{
      caption: 'Field Name',
      //dataField: 'analysis.name',
      allowSorting: true,
      alignment: 'left',
      width: '20%'
    }, {
      caption: 'Field Value',
      //dataField: 'analysis.name',
      allowSorting: true,
      alignment: 'left',
      width: '20%'
    }, {
      caption: 'Created By',
      //dataField: 'analysis.name',
      allowSorting: true,
      alignment: 'left',
      width: '20%'
    }, {
      caption: 'Created Date',
      //dataField: 'analysis.name',
      allowSorting: true,
      alignment: 'left',
      width: '20%'
    }, {
      caption: '',
      //dataField: 'analysis.name',
      allowSorting: true,
      alignment: 'left',
      width: '10%',
      cellTemplate: 'actionCellTemplate'
    }];
    return this._dxDataGridService.mergeWithDefaultConfig({
      columns,
      width: '100%',
      height: '100%',
      paging: {
        pageSize: 10
      },
      pager: {
        showPageSizeSelector: true,
        showInfo: true
      }
    });
  }
}
