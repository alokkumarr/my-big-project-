import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import * as map from 'lodash/map';
import * as find from 'lodash/find';
import * as isEmpty from 'lodash/isEmpty';
import * as first from 'lodash/first';
import * as fpMap from 'lodash/fp/map';
import * as fpPipe from 'lodash/fp/pipe';
import * as moment from 'moment';
import * as get from 'lodash/get';

import { AnalyzeService } from '../../../services/analyze.service';
import { JwtService } from '../../../../../common/services';
import { Analysis } from '../../../types';
import { PRIVILEGES } from '../../../consts';
import { isDSLAnalysis } from '../../../designer/types';

@Component({
  selector: 'analyze-schedule-dialog',
  templateUrl: './analyze-schedule-dialog.component.html',
  styleUrls: ['./analyze-schedule-dialog.component.scss']
})
export class AnalyzeScheduleDialogComponent implements OnInit {
  categories: any[] = [];
  dateFormat = 'mm/dd/yyyy';
  hasSchedule = false;
  cronValidateField = false;
  emails: string[] = [];
  repeatIntervals = ['DAYS', 'WEEKS'];
  repeatInterval = this.repeatIntervals[0];
  repeatOrdinals = [1, 2, 3, 4, 5, 6, 7];
  repeatOrdinal = this.repeatOrdinals[0];
  daysOfWeek = [
    'SUNDAY',
    'MONDAY',
    'TUESDAY',
    'WEDNESDAY',
    'THURSDAY',
    'FRIDAY',
    'SATURDAY'
  ];
  repeatOnDaysOfWeek = map(this.daysOfWeek, dayString => ({
    keyword: dayString,
    checked: false
  }));
  crondetails: any = {};
  endCriteria = {
    never: {
      keyword: 'NEVER'
    },
    after: {
      keyword: 'AFTER',
      occurenceCount: 1
    },
    on: {
      keyword: 'ON',
      endDate: null
    }
  };
  endCriterion = this.endCriteria.never.keyword;
  loadCronLayout = false;
  ftp = [];
  locations = [];
  s3Bucket = [];
  s3Locations = [];
  scheduleState: 'new' | 'exist' | 'delete';
  token: any;
  errorFlagMsg = false;
  loadCron = false;
  emailValidateFlag = false;
  disableSchedule: boolean = false;
  isEligibleToZip: boolean;
  fileType: string;
  startDateCorrectFlag = true;
  zipFormatFlag = false;
  constructor(
    public _dialogRef: MatDialogRef<AnalyzeScheduleDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      analysis: Analysis;
    },
    public _analyzeService: AnalyzeService,
    public _jwt: JwtService
  ) {}

  ngOnInit() {
    this.disableSchedule = this.checkStatusOfAnalysis();
    this.scheduleState = 'new';
    this.token = this._jwt.getTokenObj();
    this._analyzeService.getCategories(PRIVILEGES.PUBLISH).then(response => {
      this.categories = response;
      this.setDefaultCategory();
      this.fetchCronDetails();
    });

    this.isEligibleToZip = ['report', 'esReport', 'pivot'].includes(
      get(this.data.analysis, 'type')
    );

    this.fileType =
      get(this.data.analysis, 'type') === 'pivot' ? 'xlsx' : 'csv';
  }

  checkStatusOfAnalysis() {
    if (get(this.data.analysis, 'designerEdit')) {
      const runTimeFiltersInQueryCount = get(this.data.analysis, 'sipQuery.query').replace(/[^?]/g, "");
      if (runTimeFiltersInQueryCount.length === 0) {
        return false;
      }
    }
    return !get(this.data.analysis, 'designerEdit') ? false : true;
  }

  onCategorySelected(value) {
    if (isDSLAnalysis(this.data.analysis)) {
      this.data.analysis.category = value;
    } else {
      this.data.analysis.categoryId = value;
    }
  }

  setDefaultCategory() {
    const analysis = this.data.analysis;
    const categoryPresent = isDSLAnalysis(analysis)
      ? !!analysis.category
      : !!analysis.categoryId;
    if (!categoryPresent) {
      const defaultCategory = find(
        this.categories,
        category => category.children.length > 0
      );

      if (defaultCategory) {
        analysis[isDSLAnalysis(analysis) ? 'category' : 'categoryId'] = first(
          defaultCategory.children
        ).id;
      }
    }
  }

  fetchCronDetails() {
    const { type, id } = this.data.analysis;
    if (type !== 'chart') {
      this.getFTPLocations();
      this.getS3Locations();
    }
    const requestCron = {
      jobName: id,
      categoryId: isDSLAnalysis(this.data.analysis)
        ? this.data.analysis.category
        : this.data.analysis.categoryId,
      groupName: this.token.ticket.custCode
    };
    this._analyzeService.getCronDetails(requestCron).then(
      (response: any) => {
        this.loadCron = true;
        if (response.statusCode === 200) {
          this.loadCronLayout = true;
          const jobDetails = response.data.jobDetails;
          if (jobDetails) {
            const {
              cronExpression,
              activeTab,
              activeRadio,
              jobScheduleTime,
              endDate,
              timezone,
              analysisID,
              emailList,
              fileType,
              ftp,
              zip,
              s3
            } = jobDetails;
            this.crondetails = {
              cronexp: cronExpression,
              startDate: jobScheduleTime,
              activeTab,
              activeRadio,
              endDate,
              timezone
            };
            if (analysisID) {
              this.scheduleState = 'exist';
            }
            this.emails = emailList;
            this.hasSchedule = true;
            if (type !== 'chart') {
              this.ftp = ftp;
              this.s3Bucket = s3;
            }
            this.hasSchedule = true;
            this.fileType = fileType;
            this.zipFormatFlag = zip;
          }
        }
      },
      () => {
        this.loadCronLayout = true;
      }
    );
  }

  getFTPLocations() {
    const request = {
      jobGroup: this.token.ticket.custCode
    };
    this._analyzeService.getlistFTP(request).then((response: any) => {
      this.locations = response.ftp;
    });
  }

  getS3Locations() {
    const request = {
      jobGroup: this.token.ticket.custCode
    };
    this._analyzeService.getlistS3Buckets(request).then((response: any) => {
      this.s3Locations = response.S3;
    });
  }

  onCronChanged(cronexpression) {
    this.crondetails = cronexpression;
  }

  alphanumericUnique() {
    return Math.random()
      .toString(36)
      .substring(7);
  }

  removeSchedule() {
    const analysis = this.data.analysis;
    this.scheduleState = 'delete';
    const schedule = {
      categoryId: isDSLAnalysis(analysis)
        ? +analysis.category
        : analysis.categoryId,
      groupName: this.token.ticket.custCode,
      jobName: analysis.id,
      scheduleState: this.scheduleState,
      zip: this.zipFormatFlag,
      userId: this.token.ticket.masterLoginId
    };
    this._dialogRef.close(schedule);
  }

  publish() {
    const analysis = this.data.analysis;
    if (this.validateForm()) {
      let cronJobName = analysis.id;
      const crondetails = this.crondetails;
      if (crondetails.activeTab === 'immediate') {
        this.scheduleState = 'new';
        cronJobName = cronJobName + '-' + this.alphanumericUnique();
        crondetails.cronexp = '';
        crondetails.startDate = moment()
          .local()
          .format();
      }

      const schedule = {
        scheduleState: this.scheduleState,
        activeRadio: crondetails.activeRadio,
        activeTab: crondetails.activeTab,
        analysisID: analysis.id,
        analysisName: analysis.name,
        cronExpression: crondetails.cronexp,
        description: '',
        emailList: this.emails,
        ftp: this.ftp,
        s3: this.s3Bucket,
        zip: this.zipFormatFlag,
        fileType: this.fileType,
        jobName: cronJobName,
        endDate: crondetails.endDate,
        metricName: analysis.metricName,
        type: analysis.type,
        userFullName: analysis.userFullName || analysis.createdBy,
        userId: this.token.ticket.masterLoginId,
        jobScheduleTime: crondetails.startDate,
        timezone: crondetails.timezone,
        categoryID: isDSLAnalysis(analysis)
          ? analysis.category
          : analysis.categoryId,
        jobGroup: this.token.ticket.custCode
      };
      this._dialogRef.close(schedule);
    }
  }

  validateForm() {
    this.errorFlagMsg = false;
    this.emailValidateFlag = false;
    this.cronValidateField = false;
    let validationCheck = true;

    this.startDateCorrectFlag =
      moment(this.crondetails.startDate) > moment().subtract(2, 'minutes');
    const validateFields = {
      schedule: this.validateSchedule(),
      publish: this.validatePublishSelection(),
      startDate:
        moment(this.crondetails.startDate) > moment().subtract(2, 'minutes')
    };
    fpPipe(
      fpMap(check => {
        if (check === false) {
          validationCheck = false;
        }
      })
    )(validateFields);
    return validationCheck;
  }

  validatePublishSelection() {
    if (
      isEmpty(this.emails) &&
      isEmpty(this.ftp) &&
      isEmpty(this.s3Bucket) &&
      !['chart', 'map'].includes(this.data.analysis.type)
    ) {
      this.errorFlagMsg = true;
      return false;
    }
    return true;
  }

  validateSchedule() {
    if (
      isEmpty(this.crondetails.cronexp) &&
      this.crondetails.activeTab !== 'immediate'
    ) {
      this.cronValidateField = true;
      return false;
    }
    return true;
  }

  onEmailsChange(emails) {
    this.emails = emails;
  }

  onLocationSelected(value) {
    this.ftp = value;
  }

  onS3LocationSelected(value) {
    this.s3Bucket = value;
  }

  close() {
    this._dialogRef.close();
  }
}
