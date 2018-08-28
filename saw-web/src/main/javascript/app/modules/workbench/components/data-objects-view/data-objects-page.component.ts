import { Component, Input, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { UIRouter } from '@uirouter/angular';
import { DatePipe } from '@angular/common';
import { MatDialog } from '@angular/material';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';
import * as get from 'lodash/get';

import { HeaderProgressService } from '../../../../common/services/header-progress.service';
import { LocalSearchService } from '../../../../common/services/local-search.service';
import { WorkbenchService } from '../../services/workbench.service';
import { ToastService } from '../../../../common/services/toastMessage.service';
import { SearchBoxComponent } from '../../../../common/components/search-box';

const template = require('./data-objects-page.component.html');
require('./data-objects-page.component.scss');

@Component({
  selector: 'data-objects-page',
  template,
  styles: [],
  providers: [DatePipe]
})
export class DataobjectsComponent implements OnInit, OnDestroy {
  private availableSets: Array<any> = [];
  private availableDP: Array<any> = [];
  private viewState: string = 'card';
  private states = {
    searchTerm: '',
    searchTermValue: ''
  };
  private updater = new BehaviorSubject([]);
  private dpUpdater = new BehaviorSubject([]);
  private dataView: string = 'sets';
  private contentHeight: number;
  private timer;
  private timerSubscription;
  private poll: boolean = false;
  private interval = 20000;

  @ViewChild(SearchBoxComponent)
  searchBox: SearchBoxComponent;

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
    this.getDatasets();
  }

  ngOnDestroy() {
    if (this.poll) {
      this.stopPolling();
    }
  }

  startPolling() {
    /**
     * Calls list datasets/datapods api every 10 seconds or whatever set interval
     *
     * @memberof DatasetsComponent
     */
    this.timer = Observable.timer(0, this.interval);
    this.timerSubscription = this.timer.subscribe(() => {
      this.getDatasets();
    });
    this.poll = true;
  }

  stopPolling() {
    this.timerSubscription && this.timerSubscription.unsubscribe();
    this.poll = false;
  }

  getDatasets(): void {
    this.headerProgress.show();
    this.workBench.getDatasets().subscribe((data: any[]) => {
      this.availableSets = data;
      this.updateData(this.availableSets);
    });
  }

  getDatapods(): void {
    this.headerProgress.show();
    this.workBench.getListOfSemantic().subscribe((data: any[]) => {
      this.availableDP = get(data, 'contents[0].ANALYZE');
      this.updateData(this.availableDP);
      this.headerProgress.hide();
    });
  }

  updateData(data): void {
    setTimeout(() => {
      this.dataView === 'sets'
        ? this.updater.next(data)
        : this.dpUpdater.next(data);
    });
    setTimeout(() => {
      this.contentHeight = window.innerHeight - 170;
    });
  }

  /**
   * Toggling from Card and list views
   *
   * @memberof DataobjectsComponent
   */
  onViewChange(): void {
    if (this.states.searchTerm !== '') {
      this.applySearchFilter(this.states.searchTerm);
    } else {
      this.dataView === 'sets'
        ? this.updateData(this.availableSets)
        : this.updateData(this.availableDP);
    }
  }

  applySearchFilter(value): void {
    this.states.searchTerm = value;

    const DS_SEARCH_CONFIG = [
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

    const DP_SEARCH_CONFIG = [
      {
        keyword: 'Datapod Name',
        fieldName: 'metricName'
      },
      {
        keyword: 'Created by',
        fieldName: 'createdBy'
      },
      {
        keyword: 'Last Updated',
        fieldName: 'createdAt',
        accessor: createdAt =>
          this.datePipe.transform(createdAt * 1000, 'short')
      },
      {
        keyword: 'Description',
        fieldName: 'description'
      }
    ];
    let SEARCH_CONFIG = [];
    let DATA = [];
    if (this.dataView === 'sets') {
      SEARCH_CONFIG = DS_SEARCH_CONFIG;
      DATA = this.availableSets;
    } else {
      SEARCH_CONFIG = DP_SEARCH_CONFIG;
      DATA = this.availableDP;
    }

    const searchCriteria = this.LocalSearch.parseSearchTerm(
      this.states.searchTerm
    );
    this.states.searchTermValue = searchCriteria.trimmedTerm;

    this.LocalSearch.doSearch(searchCriteria, DATA, SEARCH_CONFIG).then(
      data => {
        this.updateData(data);
      },
      err => {
        this._toastMessage.error(err.message);
      }
    );
  }

  addDataSet(): void {
    this.router.stateService.go('workbench.add');
  }

  /**
   * Toggling view from Datasets and Datapods
   *
   * @memberof DataobjectsComponent
   */
  onDataObjectViewChange() {
    this.states.searchTerm === '';

    // Have to directly interact with search component to clear and close it while switching views
    this.searchBox.onClose();
    this.dataView === 'pods' ? this.getDatapods() : this.getDatasets();
  }

  onResize(event) {
    this.contentHeight = event.target.innerHeight - 165;
  }

  togglePoll() {
    this.poll === true ? this.stopPolling() : this.startPolling();
  }
}
