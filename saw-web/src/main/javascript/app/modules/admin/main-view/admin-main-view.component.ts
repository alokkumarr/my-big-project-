import { Component, Input } from '@angular/core';
import {Observable} from 'rxjs/Observable';
import { UserService } from '../user';
import {JwtService} from '../../../../login/services/jwt.service';
import { ToastService } from '../../../common/services/toastMessage.service';
import { LocalSearchService } from '../../../common/services/local-search.service';

const template = require('./admin-main-view.component.html');

const SEARCH_CONFIG = [
  {keyword: 'LOGIN ID', fieldName: 'masterLoginId'},
  {keyword: 'ROLE', fieldName: 'roleName'},
  {keyword: 'FIRST NAME', fieldName: 'firstName'},
  {keyword: 'LAST NAME', fieldName: 'lastName'},
  {keyword: 'EMAIL', fieldName: 'email'},
  {keyword: 'STATUS', fieldName: 'activeStatusInd'}
];

@Component({
  selector: 'admin-main-view',
  template
})
export class AdminMainViewComponent {

  @Input() columns: any[];
  @Input() section: 'user' | 'role' | 'privilege' | 'categories';
  data$: Observable<any[]>;
  data: any[];
  filteredData: any[];
  customer: string;
  filterObj = {
    searchTerm: '',
    searchTermValue: ''
  };

  constructor(
    private _userService: UserService,
    private _jwtService: JwtService,
    private _localSearch: LocalSearchService,
    private _toastMessage: ToastService
  ) { }

  ngOnInit() {
    const token = this._jwtService.getTokenObj();
    const customerId = token.ticket.custID;
    this.customer = token.ticket.custCode;
    this.data$ = this._userService.getActiveUsersList(customerId);
    this.data$.subscribe(data => {
      this.data = data;
      this.filteredData = this.data;
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

  openNewModal() {

  }

}
