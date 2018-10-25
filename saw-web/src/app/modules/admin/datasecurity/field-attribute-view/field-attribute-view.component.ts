import { Component, Input } from '@angular/core';
import { DxDataGridService } from '../../../../common/services/dxDataGrid.service';
import { UserAssignmentService } from './../userassignment.service';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { AddAttributeDialogComponent } from './../add-attribute-dialog/add-attribute-dialog.component';
import { DeleteDialogComponent } from './../delete-dialog/delete-dialog.component';
import * as isEmpty from 'lodash/isEmpty';

@Component({
  selector: 'field-attribute-view',
  templateUrl: './field-attribute-view.component.html',
  styleUrls: ['./field-attribute-view.component.scss']
})
export class FieldAttributeViewComponent {
  config: any;
  data: {};
  emptyState: boolean;

  @Input() groupSelected;
  constructor(
    private _dxDataGridService: DxDataGridService,
    private _userAssignmentService: UserAssignmentService,
    private _dialog: MatDialog
  ) {}

  ngOnInit() {
    this.config = this.getConfig();
    this.emptyState = true;
  }

  ngOnChanges() {
    this.loadAttributesGrid();
  }

  loadAttributesGrid() {
    this._userAssignmentService.getSecurityAttributes(this.groupSelected).then(response => {
      this.data = response;
      this.emptyState = isEmpty(this.data) ? true : false;
    });
  }

  editAttribute(cell) {
    const mode = 'edit';
    const data = {
      mode,
      attributeName: cell.data.attributeName,
      groupSelected: this.groupSelected,
      value: cell.data.value
    };
    const component = AddAttributeDialogComponent;
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
      title: `Are you sure you want to delete this attribute for group ${this.groupSelected.securityGroupName}?`,
      content: `Attribute Name: ${cellData.attributeName}`,
      positiveActionLabel: 'Delete',
      negativeActionLabel: 'Cancel'
    };
    return this._dialog.open(DeleteDialogComponent, {
      width: 'auto',
      height: 'auto',
      autoFocus: false,
      data
    } as MatDialogConfig)
    .afterClosed().subscribe((result) => {
      if (result) {
        const path = `auth/admin/security-groups/${this.groupSelected.secGroupSysId}/dsk-attributes/${cellData.attributeName}`;
        this._userAssignmentService.deleteGroupOrAttribute(path).then(response => {
          this.loadAttributesGrid();
        });
      }
    });
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
