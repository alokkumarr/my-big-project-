import { Component, Input } from '@angular/core';
import {Observable} from 'rxjs/Observable';
import { MatDialog, MatDialogConfig } from '@angular/material';
import {
  UserEditDialogComponent,
  UserService
} from '../user';
import {
  RoleEditDialogComponent
} from '../role';
import { RoleService } from '../role/role.service';
import { JwtService } from '../../../../login/services/jwt.service';
import { ToastService } from '../../../common/services/toastMessage.service';
import { LocalSearchService } from '../../../common/services/local-search.service';
import { ConfirmDialogComponent } from '../../../common/components/confirm-dialog';
import { SidenavMenuService } from '../../../common/components/sidenav';
import { ConfirmDialogData } from '../../../common/types';
import { IAdminDataService } from '../admin-data-service.interface';
import { StateService } from '@uirouter/angular';
import { AdminMenuData } from '../consts';

const template = require('./admin-main-view.component.html');
require('./admin-main-view.component.scss');

const SEARCH_CONFIG = [
  {keyword: 'LOGIN ID', fieldName: 'masterLoginId'},
  {keyword: 'ROLE', fieldName: 'roleName'},
  {keyword: 'FIRST NAME', fieldName: 'firstName'},
  {keyword: 'LAST NAME', fieldName: 'lastName'},
  {keyword: 'EMAIL', fieldName: 'email'},
  {keyword: 'STATUS', fieldName: 'activeStatusInd'}
];

const deleteConfirmation = (section, identifier, identifierValue) => ({
  title: `Are you sure you want to delete this ${section}?`,
  content: `${identifier}: ${identifierValue}`,
  positiveActionLabel: 'Delete',
  negativeActionLabel: 'Cancel'
});

@Component({
  selector: 'admin-main-view',
  template
})
export class AdminMainViewComponent {

  @Input() columns: any[];
  @Input() section: 'user' | 'role' | 'privilege' | 'categories';
  data$: Observable<any[]>;
  roles$: any;
  data: any[];
  filteredData: any[];
  customer: string;
  filterObj = {
    searchTerm: '',
    searchTermValue: ''
  };

  ticket: {custID: string, custCode: string};

  constructor(
    private _userService: UserService,
    private _roleService: RoleService,
    private _jwtService: JwtService,
    private _localSearch: LocalSearchService,
    private _toastMessage: ToastService,
    private _dialog: MatDialog,
    private _sidenav: SidenavMenuService,
    private _state: StateService
  ) { }

  ngOnInit() {
    const token = this._jwtService.getTokenObj();
    this.ticket = token.ticket;
    const customerId = this.ticket.custID;
    this.data$ = this.getListData(customerId);
    this.data$.subscribe(data => {
      this.setData(data);
    });

    this._sidenav.updateMenu(AdminMenuData, 'ADMIN');
  }

  applySearchFilter(value) {
    this.filterObj.searchTerm = value;
    const searchCriteria = this._localSearch.parseSearchTerm(this.filterObj.searchTerm);
    this.filterObj.searchTermValue = searchCriteria.trimmedTerm;
    this._localSearch.doSearch(searchCriteria, this.data, SEARCH_CONFIG).then(data => {
      this.filteredData = data;
    }, err => {
      this._toastMessage.error(err.message);
    });
  }

  onRowClick(row) {
    switch (this.section) {
    case 'role':
      this._state.go('admin.privilege', {
        role: row.roleName
      });
      break;
    default:
      break;
    }
  }

  setData(data) {
    this.data = data;
    if (this.filterObj.searchTerm) {
      this.applySearchFilter(this.filterObj.searchTerm);
    } else {
      this.filteredData = data;
    }
  }

  getService() {
    switch (this.section) {
    case 'user':
      return this._userService;
    case 'role':
      return this._roleService;
    default:
      break;
    }
  }

  getFormDeps() {
    const customerId = this.ticket.custID;
    switch (this.section) {
    case 'user':
      return {roles$: this._userService.getUserRoles(customerId)};
    case 'role':
      return {roleTypes$: this._roleService.getRoleTypes(customerId)};
    default:
      break;
    }
  }

  getListData(customerId) {
    const service = this.getService();
    return service.getList(customerId);
  }

  removeListItem(row) {
    const service = this.getService();
    return service.remove(row).then(rows => {
      if (rows) {
        this.setData(rows);
      }
    });
  }

  deleteRow(row) {
    const modifiedRow = this.modifyRowForDeletion(row);

    const confirmation = this.getConfirmation(modifiedRow);
    this.openConfirmationDialog(confirmation).afterClosed().subscribe(canDelete => {
      if (canDelete) {
        this.removeListItem(modifiedRow);
      }
    });
  }

  getConfirmation(row) {
    switch(this.section) {
    case 'user':
      return deleteConfirmation('user', 'User ID', row.masterLoginId);
    case 'role':
      return deleteConfirmation('role', 'Role Name', row.roleName);
    }
  }

  modifyRowForDeletion(row) {
    switch(this.section) {
    case 'role':
      const custId = parseInt(this.ticket.custID, 10);
      const userId = this.ticket.masterLoginId;
      return {
        ...row,
        roleId: row.roleSysId,
        customerId: custId,
        masterLoginId: userId
      }
    default:
      return row;
    }
  }

  editRow(row) {
    this.openRowModal(row, 'edit').afterClosed().subscribe(rows => {
      if (rows) {
        this.setData(rows);
      }
    });
  }

  createRow() {
    const newRow = this.getNewRow();
    this.openRowModal(newRow, 'create').afterClosed().subscribe(rows => {
      if (rows) {
        this.setData(rows);
      }
    });
  }

  getNewRow() {
    switch(this.section) {
    case 'user':
      return {
        customerId: this.ticket.custID
      };
    case 'role':
      const custId = parseInt(this.ticket.custID, 10);
      const custCode = this.ticket.custCode;
      const userId = this.ticket.masterLoginId;
      return {
        custSysId: custId,
        customerCode: custCode,
        masterLoginId: userId,
        myAnalysis: true
      }
    }
  }

  openRowModal(row, mode: 'edit' | 'create') {
    const formDeps = this.getFormDeps();
    const data = {
      model: row,
      formDeps,
      mode
    };
    const component = this.getModalComponent();
    return this._dialog.open(component, {
      width: 'auto',
      height: 'auto',
      data
    } as MatDialogConfig);
  }

  getModalComponent() {
    switch (this.section) {
    case 'user':
      return UserEditDialogComponent;
    case 'role':
      return RoleEditDialogComponent;
    }
  }

  openConfirmationDialog(data: ConfirmDialogData) {
    return this._dialog.open(ConfirmDialogComponent, {
      width: 'auto',
      height: 'auto',
      data
    } as MatDialogConfig);
  }

}
