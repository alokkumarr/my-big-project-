import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { Select, Store } from '@ngxs/store';
import { Observable } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { combineLatest } from 'rxjs';
import * as find from 'lodash/find';
import * as isEmpty from 'lodash/isEmpty';

import { LoadJobLogs, LoadJobs } from '../../../state/workbench.actions';
import { JobLog, Job } from '../../../models/workbench.interface';

@Component({
  selector: 'job-logs-page',
  templateUrl: 'job-logs-page.component.html',
  styleUrls: ['job-logs-page.component.scss']
})
export class JobLogsPageComponent implements OnInit {
  @Select(state => state.workbench.jobLogs)
  jobLogs$: Observable<JobLog[]>;

  @Select(state => state.workbench.jobs) jobs$: Observable<Job[]>;

  public job: Job;

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

  constructor(
    private _store: Store,
    private _route: ActivatedRoute,
    private _location: Location
  ) {}

  ngOnInit() {
    this.loadJobsIfNeeded();

    this._route.params.subscribe(({ jobId }) => {
      this._store.dispatch(new LoadJobLogs(jobId));
    });

    combineLatest(this._route.params, this.jobs$).subscribe(
      ([params, jobs]) => {
        const jobId = parseInt(params.jobId, 10);
        this.job = find(jobs, job => job.jobId === jobId);
      }
    );
  }

  loadJobsIfNeeded() {
    const jobs = this._store.selectSnapshot(state => state.workbench.jobs);
    if (isEmpty(jobs)) {
      this._store.dispatch(new LoadJobs());
    }
  }

  goBack() {
    this._location.back();
  }
}
