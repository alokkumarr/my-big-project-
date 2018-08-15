import { Component, Input } from '@angular/core';
import {Observable} from 'rxjs/Observable';
import * as cloneDeep from 'lodash/cloneDeep';
import * as map from 'lodash/map';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { Transition } from '@uirouter/angular';
import {
  UserEditDialogComponent,
  UserService
} from '../user';
import {
  RoleEditDialogComponent
} from '../role';
import {
  PrivilegeEditDialogComponent
} from '../privilege';
import {
  CategoryEditDialogComponent,
  CategoryDeleteDialogComponent
} from '../category';
import { RoleService } from '../role/role.service';
import { CategoryService } from '../category/category.service';
import { PrivilegeService } from '../privilege/privilege.service';
import { JwtService } from '../../../../login/services/jwt.service';
import { ToastService } from '../../../common/services/toastMessage.service';
import { LocalSearchService } from '../../../common/services/local-search.service';
import { ConfirmDialogComponent } from '../../../common/components/confirm-dialog';
import { SidenavMenuService } from '../../../common/components/sidenav';
import { ConfirmDialogData } from '../../../common/types';
import { StateService } from '@uirouter/angular';
import { AdminMenuData } from '../consts';

const template = require('./admin-main-view.component.html');
require('./admin-main-view.component.scss');

const USER_SEARCH_CONFIG = [
  {keyword: 'LOGIN ID', fieldName: 'masterLoginId'},
  {keyword: 'ROLE', fieldName: 'roleName'},
  {keyword: 'FIRST NAME', fieldName: 'firstName'},
  {keyword: 'LAST NAME', fieldName: 'lastName'},
  {keyword: 'EMAIL', fieldName: 'email'},
  {keyword: 'STATUS', fieldName: 'activeStatusInd'}
];

const ROLE_SEARCH_CONFIG = [
  {keyword: 'ROLE NAME', fieldName: 'roleName'},
  {keyword: 'ROLE TYPE', fieldName: 'roleType'},
  {keyword: 'STATUS', fieldName: 'activeStatusInd'},
  {keyword: 'ROLE DESCRIPTION', fieldName: 'roleDesc'}
];

const CATEGORY_SEARCH_CONFIG = [
  {keyword: 'PRODUCT', fieldName: 'productName'},
  {keyword: 'MODULE', fieldName: 'moduleName'},
  {keyword: 'CATEGORY', fieldName: 'categoryName'},
  {keyword: 'SUB CATEGORIES', fieldName: 'subCategories', accessor: input => map(input, sc => sc.subCategoryName)}
];

const PRIVILEGE_SEARCH_CONFIG = [
  {keyword: 'PRODUCT', fieldName: 'productName'},
  {keyword: 'MODULE', fieldName: 'moduleName'},
  {keyword: 'CATEGORY', fieldName: 'categoryName'},
  {keyword: 'ROLE', fieldName: 'roleName'},
  {keyword: 'PRIVILEGE DESC', fieldName: 'privilegeDesc'},
  {keyword: 'SUB CATEGORY', fieldName: 'subCategoryName'}
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
  @Input() section: 'user' | 'role' | 'privilege' | 'category';
  data$: Promise<any[]>;
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
    private _privilegeService: PrivilegeService,
    private _categoryService: CategoryService,
    private _userService: UserService,
    private _roleService: RoleService,
    private _jwtService: JwtService,
    private _localSearch: LocalSearchService,
    private _toastMessage: ToastService,
    private _dialog: MatDialog,
    private _sidenav: SidenavMenuService,
    private _state: StateService,
    private _transition: Transition
  ) { }

  ngOnInit() {
    const token = this._jwtService.getTokenObj();
    this.ticket = token.ticket;
    const customerId = this.ticket.custID;
    this.data$ = this.getListData(customerId);
    this.data$.then(data => {
      if (this.section === 'privilege') {
        const { role } = this._transition.params();
        if (role) {
          this.filterObj.searchTerm = role;
        }
      }
      this.setData(data);
    });

    this._sidenav.updateMenu(AdminMenuData, 'ADMIN');


  }

  applySearchFilter(value) {
    this.filterObj.searchTerm = value;
    const searchCriteria = this._localSearch.parseSearchTerm(this.filterObj.searchTerm);
    this.filterObj.searchTermValue = searchCriteria.trimmedTerm;
    this._localSearch.doSearch(searchCriteria, this.data, this.getSearchConfig()).then(data => {
      this.filteredData = data;
    }, err => {
      this._toastMessage.error(err.message);
    });
  }

  getSearchConfig() {
    switch(this.section) {
    case 'user':
      return USER_SEARCH_CONFIG;
    case 'role':
      return ROLE_SEARCH_CONFIG;
    case 'category':
      return CATEGORY_SEARCH_CONFIG;
    case 'privilege':
      return PRIVILEGE_SEARCH_CONFIG;
    }
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
    case 'category':
      return this._categoryService;
    case 'privilege':
      return this._privilegeService;
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
    case 'category':
      return {customerId};
    case 'privilege':
      return {customerId};
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

    let dialogAction;
    switch(this.section) {
    case 'category':
      dialogAction = this.openCategoryDeletionDialog(row);
      break;
    default:
      dialogAction = this.openConfirmationDialog(confirmation);
    }

    dialogAction.afterClosed().subscribe(canDelete => {
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
    case 'category':
      return deleteConfirmation('category', 'Category Name', row.categoryName);
    case 'privilege':
      const identifiervalue = `${row.productName} --> ${row.moduleName} --> ${row.categoryName} --> ${row.subCategoryName} --> ${row.roleName}.`;
      return deleteConfirmation('privilege', 'Privilege Details', identifiervalue);
    }
  }

  modifyRowForDeletion(row) {
    switch(this.section) {
    case 'role':
      const custId = parseInt(this.ticket.custID, 10);
      const { masterLoginId } = this.ticket;
      return {
        ...row,
        roleId: row.roleSysId,
        customerId: custId,
        masterLoginId
      }
    default:
      return row;
    }
  }

  editRow(row) {
    this.openRowModal(cloneDeep(row), 'edit').afterClosed().subscribe(rows => {
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
    const custId = parseInt(this.ticket.custID, 10);
    const {custCode, masterLoginId} = this.ticket
    switch(this.section) {
    case 'user':
      return {
        customerId: custId
      };
    case 'role':
      return {
        custSysId: custId,
        customerCode: custCode,
        masterLoginId,
        myAnalysis: true
      }
    case 'category':
      return {
        customerId: custId,
        masterLoginId
      };
    case 'privilege':
      return {
        customerId: custId,
        masterLoginId
      };
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

  openCategoryDeletionDialog(category) {
    const customerId = this.ticket.custID;
    const masterLoginId = this.ticket.masterLoginId;
    const data = {
      category,
      customerId,
      masterLoginId
    };
    return this._dialog.open(CategoryDeleteDialogComponent, {
      width: 'auto',
      height: 'auto',
      autoFocus: false,
      data
    } as MatDialogConfig);
  }

  getModalComponent() {
    switch (this.section) {
    case 'user':
      return UserEditDialogComponent;
    case 'role':
      return RoleEditDialogComponent;
    case 'category':
      return CategoryEditDialogComponent;
    case 'privilege':
      return PrivilegeEditDialogComponent;
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
