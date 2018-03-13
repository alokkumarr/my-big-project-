declare function require(string): string;

import { Component, Input, OnInit, Inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { MatDialog } from '@angular/material';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import * as map from 'lodash/map';

import { HeaderProgressService } from '../../../../common/services/header-progress.service';
import { dxDataGridService } from '../../../../common/services/dxDataGrid.service';
import { LocalSearchService } from '../../../../common/services/local-search.service';
import { WorkbenchService } from '../../services/workbench.service';
import { CreateDatasetsComponent } from '../create-datasets/create-datasets.component';

const template = require('./datasets-page.component.html');
require('./datasets-page.component.scss');

@Component({
  selector: 'datasets-page',
  template,
  styles: [],
  providers: [DatePipe]
})

export class DatasetsComponent implements OnInit {
  private availableSets: Array<any> = [];
  private viewState: string = 'card';
  private states = {
    searchTerm: '',
    searchTermValue: ''
  };
  private updater = new BehaviorSubject([]);
  private dataView: string = 'sets';
  private contentHeight: number;

  constructor(
    public dialog: MatDialog,
    private headerProgress: HeaderProgressService,
    private dataGrid: dxDataGridService,
    private LocalSearch: LocalSearchService,
    private workBench: WorkbenchService,
    private datePipe: DatePipe
  ) { }

  ngOnInit() {
    this.getPageData();
  }

  getPageData(): void {
    this.headerProgress.show();
    this.workBench.getDatasets().subscribe((data: any[]) => {
      this.availableSets = data;
      this.loadSets(this.availableSets);
    });
  }

  loadSets(data): void {
    this.headerProgress.show();
    if (this.viewState === 'list') {
      setTimeout(() => {
        this.updater.next(data);
      });
    } else {
      setTimeout(() => {
        this.updater.next(data);
      });
    }
    setTimeout(() => {
      this.contentHeight = window.innerHeight-165;
    });
  }

  onViewChange(): void {
    if (this.states.searchTerm !== '') {
      this.applySearchFilter();
    } else {
      this.loadSets(this.availableSets);
    }
  }

  applySearchFilter(): void {
    const SEARCH_CONFIG = [
      { keyword: 'Data Set Name', fieldName: 'set' },
      { keyword: 'Added By', fieldName: 'meta', accessor: input => input.addedBy },
      { keyword: 'Data Pods', fieldName: 'meta', accessor: input => input.numFiles },
      {
        keyword: 'Last Updated',
        fieldName: 'meta',
        accessor: input => this.datePipe.transform(input.lastUpdated, 'MM/dd/yy @ HH:mm')
      },
      { keyword: 'Updated By', fieldName: 'meta', accessor: input => input.updatedBy },
      { keyword: 'Data Source', fieldName: 'src' }
    ];
    const searchCriteria = this.LocalSearch.parseSearchTerm(this.states.searchTerm);
    this.states.searchTermValue = searchCriteria.trimmedTerm;
    this.LocalSearch.doSearch(searchCriteria, this.availableSets, SEARCH_CONFIG).then(data => {
      this.loadSets(data);
    });
  }

  addDataSet(): void {
    const detailsDialogRef = this.dialog.open(CreateDatasetsComponent, {
      panelClass: 'full-screen-dialog',
      autoFocus: false
    });
    detailsDialogRef
      .afterClosed()
      .subscribe(() => {
        this.getPageData();
      });
  }

  onDataViewChange() {
    
  }

  onResize(event) {
    this.contentHeight = event.target.innerHeight-165;
  }
}