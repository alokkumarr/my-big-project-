
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { MatDialog } from '@angular/material';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';

import { LocalSearchService } from '../../../../common/services/local-search.service';
import { WorkbenchService } from '../../services/workbench.service';

const template = require('./datasets-page.component.html');
const style = require('./datasets-page.component.scss');

@Component({
  selector: 'datasets-page',
  template,
  styles: [style]
  providers: [DatePipe]
})

export class DatasetsComponent implements OnInit, OnDestroy {
  private availableSets: Array<any> = [];
  private viewState = 'card';
  private states = {
    searchTerm: '',
    searchTermValue: ''
  };
  private updater = new BehaviorSubject([]);
  private dataView = 'sets';
  private contentHeight: number;
  private timer;
  private timerSubscription;
  private poll = true;
  private interval = 20000;

  constructor(
    private router: Router,
    public dialog: MatDialog,
    private LocalSearch: LocalSearchService,
    private workBench: WorkbenchService,
    private datePipe: DatePipe
  ) { }

  ngOnInit() {
    this.startPolling();
  }

  ngOnDestroy() {
    if (this.poll) {
      this.stopPolling();
    }
  }

  startPolling() {
    /**
        * Calls list datasets api onInit and every 10 seconds or whatever set interval
        *
        * @memberof DatasetsComponent
        */
    this.timer = Observable.timer(0, this.interval);
    this.timerSubscription = this.timer.subscribe(() => {
      this.getPageData();
    });
    this.poll = true;
  }

  stopPolling() {
    this.timerSubscription && this.timerSubscription.unsubscribe();
    this.poll = false;
  }

  getPageData(): void {
    this.workBench.getDatasets().subscribe((data: any[]) => {
      this.availableSets = data;
      this.loadSets(this.availableSets);
    });
  }

  loadSets(data): void {
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
      this.contentHeight = window.innerHeight - 170;
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
    this.router.navigate(['workbench' , 'add']);
  }

  onDataViewChange() {

  }

  onResize(event) {
    this.contentHeight = event.target.innerHeight - 165;
  }

  togglePoll() {
    this.poll === true ? this.stopPolling() : this.startPolling();
  }
}
