import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { Select, Store } from '@ngxs/store';
import { Observable } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { combineLatest } from 'rxjs';
import * as find from 'lodash/find';
import * as isEmpty from 'lodash/isEmpty';
import CustomStore from 'devextreme/data/custom_store';

import { SetJobLogs, LoadJobByJobId } from '../../../state/workbench.actions';
import { JobLog, Job } from '../../../models/workbench.interface';
import { DatasourceService } from '../../../services/datasource.service';

const DEFAULT_PAGE_SIZE = 25;

@Component({
  selector: 'job-logs-page',
  templateUrl: 'job-logs-page.component.html',
  styleUrls: ['job-logs-page.component.scss']
})
export class JobLogsPageComponent implements OnInit {
  @Select(state => state.workbench.jobLogs)
  jobLogs$: Observable<JobLog[]>;

  @Select(state => state.workbench.jobs) jobs$: Observable<Job[]>;

  public paging = { enabled: true, pageSize: DEFAULT_PAGE_SIZE, pageIndex: 0 };
  public remoteOperations = { paging: true };
  public job: Job;
  public pagingEnabled = false;
  public DEFAULT_PAGE_SIZE = DEFAULT_PAGE_SIZE;
  public jobDetails = [
    [
      { label: 'Job Name', prop: 'jobName' },
      { label: 'Job Status', prop: 'jobStatus' }
    ],
    [
      { label: 'Start Time', prop: 'startTime', isDateField: true },
      { label: 'End Time', prop: 'endTime', isDateField: true }
    ],
    [
      { label: 'Total Count', prop: 'totalCount' },
      { label: 'Success Count', prop: 'successCount' }
    ],
    [
      { label: 'File Pattern', prop: 'filePattern' },
      { label: 'Job Type', prop: 'jobType' }
    ],
    [
      { label: 'Created at', prop: 'createdDate', isDateField: true },
      { label: 'Created by', prop: 'createdBy' }
    ],
    [
      { label: 'Updated at', prop: 'updatedDate', isDateField: true },
      { label: 'Updated by', prop: 'updatedBy' }
    ]
  ];
  public data;

  constructor(
    private _store: Store,
    private _route: ActivatedRoute,
    private _location: Location,
    private _datasourceService: DatasourceService
  ) {}

  ngOnInit() {
    this.loadJobIfNeeded();

    this._route.params.subscribe(({ jobId }) => {
      this.data = new CustomStore({
        load: ({ skip, take }) => {
          const offset = Math.ceil(skip / take);
          const pagination = `offset=${offset}&size=${take}`;
          return this._datasourceService
            .getJobLogs(jobId, pagination)
            .toPromise()
            .then(({ bisFileLogs, totalRows }) => {
              this._store.dispatch(new SetJobLogs(bisFileLogs));
              this.pagingEnabled = totalRows > DEFAULT_PAGE_SIZE;
              return { data: bisFileLogs, totalCount: totalRows };
            });
        }
      });
    });

    combineLatest(this._route.params, this.jobs$).subscribe(
      ([params, jobs]) => {
        const jobId = parseInt(params.jobId, 10);
        this.job = find(jobs, job => job.jobId === jobId);
      }
    );
  }

  loadJobIfNeeded() {
    const jobs = this._store.selectSnapshot(state => state.workbench.jobs);
    const { jobId } = this._route.snapshot.params;

    if (isEmpty(jobs)) {
      this._store.dispatch(new LoadJobByJobId(jobId));
    }
  }

  goBack() {
    this._location.back();
  }
}
