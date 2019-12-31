import { Component, OnDestroy } from '@angular/core';
import * as cloneDeep from 'lodash/cloneDeep';
import * as map from 'lodash/map';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { UserEditDialogComponent, UserService } from '../user';
import { RoleEditDialogComponent } from '../role';
import { PrivilegeEditDialogComponent } from '../privilege';
import {
  CategoryEditDialogComponent,
  CategoryDeleteDialogComponent
} from '../category';
import { RoleService } from '../role/role.service';
import { CategoryService } from '../category/category.service';
import { PrivilegeService } from '../privilege/privilege.service';
import { DataSecurityService } from '../datasecurity/datasecurity.service';
import { JwtService } from '../../../common/services';
import { ToastService } from '../../../common/services/toastMessage.service';
import { LocalSearchService } from '../../../common/services/local-search.service';
import { ConfirmDialogComponent } from '../../../common/components/confirm-dialog';
import { ConfirmDialogData } from '../../../common/types';

const USER_SEARCH_CONFIG = [
  { keyword: 'LOGIN ID', fieldName: 'masterLoginId' },
  { keyword: 'ROLE', fieldName: 'roleName' },
  { keyword: 'FIRST NAME', fieldName: 'firstName' },
  { keyword: 'LAST NAME', fieldName: 'lastName' },
  { keyword: 'EMAIL', fieldName: 'email' },
  { keyword: 'STATUS', fieldName: 'activeStatusInd' }
];

const ROLE_SEARCH_CONFIG = [
  { keyword: 'ROLE NAME', fieldName: 'roleName' },
  { keyword: 'ROLE TYPE', fieldName: 'roleType' },
  { keyword: 'STATUS', fieldName: 'activeStatusInd' },
  { keyword: 'ROLE DESCRIPTION', fieldName: 'roleDesc' }
];

const CATEGORY_SEARCH_CONFIG = [
  { keyword: 'PRODUCT', fieldName: 'productName' },
  { keyword: 'MODULE', fieldName: 'moduleName' },
  { keyword: 'CATEGORY', fieldName: 'categoryName' },
  {
    keyword: 'SUB CATEGORIES',
    fieldName: 'subCategories',
    accessor: input => map(input, sc => sc.subCategoryName)
  }
];

const PRIVILEGE_SEARCH_CONFIG = [
  { keyword: 'PRODUCT', fieldName: 'productName' },
  { keyword: 'MODULE', fieldName: 'moduleName' },
  { keyword: 'CATEGORY', fieldName: 'categoryName' },
  { keyword: 'ROLE', fieldName: 'roleName' },
  { keyword: 'PRIVILEGE DESC', fieldName: 'privilegeDesc' },
  { keyword: 'SUB CATEGORY', fieldName: 'subCategoryName' }
];

const deleteConfirmation = (section, identifier, identifierValue) => ({
  title: `Are you sure you want to delete this ${section}?`,
  content: `${identifier}: ${identifierValue}`,
  positiveActionLabel: 'Delete',
  negativeActionLabel: 'Cancel'
});

@Component({
  selector: 'admin-main-view',
  templateUrl: './admin-main-view.component.html',
  styleUrls: ['./admin-main-view.component.scss']
})
export class AdminMainViewComponent implements OnDestroy {
  columns: any[] = [];
  section: 'user' | 'role' | 'privilege' | 'category' | 'user assignments';
  data$: Promise<any[]>;
  roles$: any;
  data: any[];
  filteredData: any[];
  listeners: Subscription[] = [];
  customer: string;
  filterObj = {
    searchTerm: '',
    searchTermValue: ''
  };

  ticket: { custID: string; custCode: string; masterLoginId?: string };

  showAddButton: boolean;

  constructor(
    public _privilegeService: PrivilegeService,
    public _categoryService: CategoryService,
    public _userService: UserService,
    public _roleService: RoleService,
    public _jwtService: JwtService,
    public _localSearch: LocalSearchService,
    private _userassignmentsService: DataSecurityService,
    public _toastMessage: ToastService,
    public _dialog: MatDialog,
    public _router: Router,
    public _route: ActivatedRoute
  ) {
    const dataListener = this._route.data.subscribe((data: any) =>
      this.onDataChange(data)
    );
    const navigationListener = this._router.events.subscribe((e: any) => {
      if (e instanceof NavigationEnd) {
        this.initialise();
      }
    });
    this.listeners.push(dataListener);
    this.listeners.push(navigationListener);
  }

  initialise() {
    this.showAddButton = true;
    const token = this._jwtService.getTokenObj();
    this.ticket = token.ticket;
    const customerId = parseInt(this.ticket.custID, 10);
    this.data$ = this.getListData(customerId);
    this.data$.then(data => {
      if (this.section === 'privilege') {
        const { role } = this._route.snapshot.queryParams;
        if (role) {
          this.filterObj.searchTerm = `role:"${role}"`;
        }
      }
      this.setData(data);
    });
  }

  ngOnDestroy() {
    this.listeners.forEach(l => l.unsubscribe());
  }

  onDataChange({ columns, section }) {
    this.columns = columns;
    this.section = section;
  }

  applySearchFilter(value) {
    this.filterObj.searchTerm = value;
    const searchCriteria = this._localSearch.parseSearchTerm(
      this.filterObj.searchTerm
    ) as any;
    this.filterObj.searchTermValue = searchCriteria.trimmedTerm;
    this._localSearch
      .doSearch(searchCriteria, this.data, this.getSearchConfig())
      .then(
        (data: any[]) => {
          this.filteredData = data;
        },
        err => {
          this._toastMessage.error(err.message);
        }
      );
  }

  getSearchConfig() {
    /* prettier-ignore */
    switch (this.section) {
    case 'user':
      return USER_SEARCH_CONFIG;
    case 'role':
      return ROLE_SEARCH_CONFIG;
    case 'category':
      return CATEGORY_SEARCH_CONFIG;
    case 'privilege':
      return PRIVILEGE_SEARCH_CONFIG;
    case 'user assignments':
      return USER_SEARCH_CONFIG;
    }
  }

  onRowClick(row) {
    /* prettier-ignore */
    switch (this.section) {
    case 'role':
      this._router.navigate(['/admin/privilege'], {
        queryParams: { role: row.roleName }
      });
      break;
    default:
      break;
    }
  }

  setData(data) {
    setTimeout(() => {
      this.data = data;
      if (this.filterObj.searchTerm) {
        this.applySearchFilter(this.filterObj.searchTerm);
      } else {
        this.filteredData = data;
      }
    }, 100);
  }

  getService() {
    /* prettier-ignore */
    switch (this.section) {
    case 'user':
      return this._userService;
    case 'role':
      return this._roleService;
    case 'category':
      return this._categoryService;
    case 'privilege':
      return this._privilegeService;
    case 'user assignments':
      this.showAddButton = false;
      return this._userassignmentsService;
    default:
      break;
    }
  }

  getFormDeps() {
    const customerId = parseInt(this.ticket.custID, 10);
    const { masterLoginId } = this.ticket as any;
    /* prettier-ignore */
    switch (this.section) {
    case 'user':
      return { roles$: this._userService.getUserRoles(customerId) };
    case 'role':
      return { roleTypes$: this._roleService.getRoleTypes(customerId) };
    case 'category':
      return { customerId };
    case 'privilege':
      return { customerId, masterLoginId };
    default:
      break;
    }
  }

  getListData(customerId) {
    const service = this.getService() as any;
    return service.getList(customerId);
  }

  removeListItem(row) {
    const service = this.getService() as any;
    return service.remove(row).then(rows => {
      /* prettier-ignore */
      switch (this.section) {
      case 'privilege':
        // for some reason, the backend doesn't return the new array of privileges
        // so we have to delete it manually
        const index = this.filteredData.indexOf(row);
        this.filteredData.splice(index, 1);
        break;
      default:
        if (rows) {
          this.setData(rows);
        }
        break;
      }
    });
  }

  deleteRow(row) {
    const modifiedRow = this.modifyRowForDeletion(row);

    const confirmation = this.getConfirmation(modifiedRow);

    let dialogAction;
    /* prettier-ignore */
    switch (this.section) {
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
    /* prettier-ignore */
    switch (this.section) {
    case 'user':
      return deleteConfirmation('user', 'User ID', row.masterLoginId);
    case 'role':
      return deleteConfirmation('role', 'Role Name', row.roleName);
    case 'category':
      return deleteConfirmation(
        'category',
        'Category Name',
        row.categoryName
      );
    case 'privilege':
      const identifiervalue = `${row.productName} --> ${row.moduleName} --> ${
        row.categoryName
      } --> ${row.subCategoryName} --> ${row.roleName}.`;
      return deleteConfirmation(
        'privilege',
        'Privilege Details',
        identifiervalue
      );
    }
  }

  modifyRowForDeletion(row) {
    /* prettier-ignore */
    switch (this.section) {
    case 'role':
      const customerId = parseInt(this.ticket.custID, 10);
      const { masterLoginId } = this.ticket as any;
      return {
        ...row,
        roleId: row.roleSysId,
        customerId,
        masterLoginId
      };
    default:
      return row;
    }
  }

  editRow(row) {
    this.openRowModal(cloneDeep(row), 'edit')
      .afterClosed()
      .subscribe(rows => {
        if (rows) {
          this.setData(rows);
        }
      });
  }

  createRow() {
    const newRow = this.getNewRow();
    this.openRowModal(newRow, 'create')
      .afterClosed()
      .subscribe(rows => {
        if (rows) {
          this.setData(rows);
        }
      });
  }

  getNewRow() {
    const custId = parseInt(this.ticket.custID, 10);
    const { custCode, masterLoginId } = this.ticket as any;
    /* prettier-ignore */
    switch (this.section) {
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
      };
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
    const component = this.getModalComponent() as any;
    return this._dialog.open(component, {
      width: 'auto',
      height: 'auto',
      autoFocus: false,
      data
    } as MatDialogConfig);
  }

  openCategoryDeletionDialog(category) {
    const customerId = parseInt(this.ticket.custID, 10);
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
    /* prettier-ignore */
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
