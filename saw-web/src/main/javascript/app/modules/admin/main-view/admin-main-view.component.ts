import { Component, Input } from '@angular/core';
import {Observable} from 'rxjs/Observable';
import { MatDialog, MatDialogConfig } from '@angular/material';
import {
  UserEditDialogComponent,
  UserService
} from '../user';
import { JwtService } from '../../../../login/services/jwt.service';
import { ToastService } from '../../../common/services/toastMessage.service';
import { LocalSearchService } from '../../../common/services/local-search.service';
import { ConfirmDialogComponent } from '../../../common/components/confirm-dialog';
import { ConfirmDialogData } from '../../../common/types';

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

const deleteUserConfirmation = masterLoginId => ({
  title: 'Are you sure you want to delete this user?',
  content: `User ID : ${masterLoginId}`,
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
    private _jwtService: JwtService,
    private _localSearch: LocalSearchService,
    private _toastMessage: ToastService,
    private _dialog: MatDialog
  ) { }

  ngOnInit() {
    const token = this._jwtService.getTokenObj();
    this.ticket = token.ticket;
    const customerId = this.ticket.custID;
    this.data$ = this._userService.getActiveUsersList(customerId);
    this.roles$ = this._userService.getRoles(customerId);
    this.data$.subscribe(data => {
      this.setData(data);
    });
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

  deleteRow(row) {
    const confirmation = deleteUserConfirmation(row.masterLoginId);
    this.openConfirmationDialog(confirmation).afterClosed().subscribe(canDelete => {
      if (canDelete) {
        this._userService.deleteUser(row).then(users => {
          if (users) {
            this.setData(users);
          }
        });
      }
    });
  }

  editRow(row) {
    this.openUserModal(row, 'edit').afterClosed().subscribe(users => {
      if (users) {
        this.setData(users);
      }
    });
  }

  setData(data) {
    this.data = data;
    if (this.filterObj.searchTerm) {
      this.applySearchFilter(this.filterObj.searchTerm);
    } else {
      this.filteredData = data;
    }
  }

  createUser() {
    const newUser = {
      customerId: this.ticket.custID
    };
    this.openUserModal(newUser, 'create').afterClosed().subscribe(users => {
      if (users) {
        this.setData(users);
      }
    });
  }

  openUserModal(user, mode: 'edit' | 'create') {
    const data = {
      user,
      mode,
      roles$: this.roles$
    };
    return this._dialog.open(UserEditDialogComponent, {
      width: 'auto',
      height: 'auto',
      data
    } as MatDialogConfig);
  }

  openConfirmationDialog(data: ConfirmDialogData) {
    return this._dialog.open(ConfirmDialogComponent, {
      width: 'auto',
      height: 'auto',
      data
    } as MatDialogConfig);
  }

}
