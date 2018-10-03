import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { MatDialog } from '@angular/material';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/timer';

import { LocalSearchService } from '../../../../common/services/local-search.service';
import { WorkbenchService } from '../../services/workbench.service';

@Component({
  selector: 'datasets-page',
  templateUrl: './datasets-page.component.html',
  styleUrls: ['./datasets-page.component.scss'],
  providers: [DatePipe]
})
export class DatasetsComponent implements OnInit, OnDestroy {
  public availableSets: Array<any> = [];
  public viewState = 'card';
  public states = {
    searchTerm: '',
    searchTermValue: ''
  };
  public updater = new BehaviorSubject([]);
  public dataView = 'sets';
  public contentHeight: number;
  public timer;
  public timerSubscription;
  public poll = true;
  public interval = 20000;

  constructor(
    public router: Router,
    public dialog: MatDialog,
    public LocalSearch: LocalSearchService,
    public workBench: WorkbenchService,
    public datePipe: DatePipe
  ) {}

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
      {
        keyword: 'Added By',
        fieldName: 'meta',
        accessor: input => input.addedBy
      },
      {
        keyword: 'Data Pods',
        fieldName: 'meta',
        accessor: input => input.numFiles
      },
      {
        keyword: 'Last Updated',
        fieldName: 'meta',
        accessor: input =>
          this.datePipe.transform(input.lastUpdated, 'MM/dd/yy @ HH:mm')
      },
      {
        keyword: 'Updated By',
        fieldName: 'meta',
        accessor: input => input.updatedBy
      },
      { keyword: 'Data Source', fieldName: 'src' }
    ];
    const searchCriteria = this.LocalSearch.parseSearchTerm(
      this.states.searchTerm
    );
    this.states.searchTermValue = searchCriteria.trimmedTerm;
    this.LocalSearch.doSearch(
      searchCriteria,
      this.availableSets,
      SEARCH_CONFIG
    ).then(data => {
      this.loadSets(data);
    });
  }

  addDataSet(): void {
    this.router.navigate(['workbench', 'add']);
  }

  onDataViewChange() {}

  onResize(event) {
    this.contentHeight = event.target.innerHeight - 165;
  }

  togglePoll() {
    this.poll === true ? this.stopPolling() : this.startPolling();
  }
}
