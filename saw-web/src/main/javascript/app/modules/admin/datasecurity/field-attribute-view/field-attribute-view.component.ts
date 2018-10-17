import { Component, Input } from '@angular/core';
import {dxDataGridService} from '../../../../common/services/dxDataGrid.service';
import { UserAssignmentService } from './../userassignment.service';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { AddAttributeDialogComponent } from './../add-attribute-dialog/add-attribute-dialog.component';
import { DeleteDialogComponent } from './../delete-dialog/delete-dialog.component';
import * as isEmpty from 'lodash/isEmpty';

const template = require('./field-attribute-view.component.html');
require('./field-attribute-view.component.scss');

@Component({
  selector: 'field-attribute-view',
  template
})
export class FieldAttributeViewComponent {
  config: any;
  data: any;
  @Input() groupSelected;
  constructor(
    private _dxDataGridService: dxDataGridService,
    private _userAssignmentService: UserAssignmentService,
    private _dialog: MatDialog
  ) {}

  ngOnInit() {
    this.config = this.getConfig();
  }

  ngOnChanges() {
    console.log(this.groupSelected); // logs undefined
    if(!isEmpty(this.groupSelected)) {
      this.loadAttributesGrid();
    }
  }

  loadAttributesGrid() {
    const request = {
      SecurityGroupName: this.groupSelected
    }
    this._userAssignmentService.getSecurityAttributes(request).then(response => {
      console.log(response);
      //this.data = response;
      this.data = [
        {
          attributeName: "sampleOne",
          securityGroupName: this.groupSelected,
          created_by: "string",
          created_date: "string",
          value: 1
        }, {
          attributeName: "samplene",
          securityGroupName: this.groupSelected,
          created_by: "string",
          created_date: "string",
          value: 2
        }, {
          attributeName: "sampletne",
          securityGroupName: this.groupSelected,
          created_by: "string",
          created_date: "string",
          value: 3
        }
      ];
      console.log(this.data);
      //this.groupSelected = this.data[0].securityGroupName;
    });
  }

  editAttribute(cell) {
    console.log(cell);
    let mode = 'edit';
    const data = {
      mode,
      attributeName: cell.data.attributeName,
      groupSelected: this.groupSelected,
      value: cell.data.value
    };
    let component = AddAttributeDialogComponent;
    return this._dialog.open(component, {
      width: 'auto',
      height: 'auto',
      autoFocus: false,
      data
    } as MatDialogConfig)
    .afterClosed().subscribe((result) => {
      if (result) {
        this.loadAttributesGrid();
      }
    });
  }

  deleteAtttribute(cellData) {
    const data = {
      title: `Are you sure you want to delete this attribute?`,
      content: `Attribute Name: ${cellData.attributeName}`,
      positiveActionLabel: 'Delete',
      negativeActionLabel: 'Cancel'
    }
    console.log(data);
    return this._dialog.open(DeleteDialogComponent, {
      width: 'auto',
      height: 'auto',
      autoFocus: false,
      data
    } as MatDialogConfig)
  }

  getConfig() {
    const columns = [{
      caption: 'Field Name',
      dataField: 'attributeName',
      allowSorting: true,
      alignment: 'left',
      width: '20%'
    }, {
      caption: 'Field Value',
      dataField: 'value',
      allowSorting: true,
      alignment: 'left',
      width: '20%'
    }, {
      caption: 'Created By',
      dataField: 'created_by',
      allowSorting: true,
      alignment: 'left',
      width: '20%'
    }, {
      caption: 'Created Date',
      dataField: 'created_date',
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
