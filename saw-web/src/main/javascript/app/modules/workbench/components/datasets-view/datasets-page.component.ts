import { Component, Input, OnInit, Inject, OnDestroy } from '@angular/core';
import { UIRouter } from '@uirouter/angular';
import { DatePipe } from '@angular/common';
import { MatDialog } from '@angular/material';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';
import * as map from 'lodash/map';

import { HeaderProgressService } from '../../../../common/services/header-progress.service';
import { LocalSearchService } from '../../../../common/services/local-search.service';
import { WorkbenchService } from '../../services/workbench.service';
import { CreateDatasetsComponent } from '../create-datasets/create-datasets.component';
import { ToastService } from '../../../../common/services/toastMessage.service';

const template = require('./datasets-page.component.html');
require('./datasets-page.component.scss');

@Component({
  selector: 'datasets-page',
  template,
  styles: [],
  providers: [DatePipe]
})
export class DatasetsComponent implements OnInit, OnDestroy {
  private availableSets: Array<any> = [];
  private viewState: string = 'card';
  private states = {
    searchTerm: '',
    searchTermValue: ''
  };
  private updater = new BehaviorSubject([]);
  private dataView: string = 'sets';
  private contentHeight: number;
  private timer;
  private timerSubscription;
  private poll: boolean = false;
  private interval = 20000;

  constructor(
    private router: UIRouter,
    public dialog: MatDialog,
    private headerProgress: HeaderProgressService,
    private LocalSearch: LocalSearchService,
    private workBench: WorkbenchService,
    private datePipe: DatePipe,
    private _toastMessage: ToastService
  ) {}

  ngOnInit() {
    this.getPageData();
  }

  ngOnDestroy() {
    if (this.poll) {
      this.stopPolling();
    }
    this.headerProgress.hide();
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
    this.headerProgress.show();
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
      this.applySearchFilter(this.states.searchTerm);
    } else {
      this.loadSets(this.availableSets);
    }
  }

  applySearchFilter(value): void {
    this.states.searchTerm = value;

    const SEARCH_CONFIG = [
      {
        keyword: 'Data Set Name',
        fieldName: 'system',
        accessor: system => system.name
      },
      {
        keyword: 'Added By',
        fieldName: 'system',
        accessor: system => system.createdBy
      },
      {
        keyword: 'Last Updated',
        fieldName: 'system',
        accessor: system =>
          this.datePipe.transform(system.modifiedTime * 1000, 'short')
      },
      {
        keyword: 'Description',
        fieldName: 'system',
        accessor: system => system.description
      }
    ];
    const searchCriteria = this.LocalSearch.parseSearchTerm(
      this.states.searchTerm
    );
    this.states.searchTermValue = searchCriteria.trimmedTerm;

    this.LocalSearch.doSearch(
      searchCriteria,
      this.availableSets,
      SEARCH_CONFIG
    ).then(
      data => {
        this.loadSets(data);
      },
      err => {
        this._toastMessage.error(err.message);
      }
    );
  }

  addDataSet(): void {
    this.router.stateService.go('workbench.add');
  }

  onDataViewChange() {}

  onResize(event) {
    this.contentHeight = event.target.innerHeight - 165;
  }

  togglePoll() {
    this.poll === true ? this.stopPolling() : this.startPolling();
  }
}
