import * as map from 'lodash/map';
import * as forEach from 'lodash/forEach';
import * as find from 'lodash/find';
import * as isEmpty from 'lodash/isEmpty';
import * as first from 'lodash/first';
import * as moment from 'moment';
import * as fpMap from 'lodash/fp/map';
import * as fpPipe from 'lodash/fp/pipe';

import * as template from './analyze-publish-dialog.component.html';
import style from './analyze-publish-dialog.component.scss';

import {PRIVILEGES} from '../../consts';

export const AnalyzePublishDialogComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<',
    onPublish: '&'
  },
  controller: class AnalyzePublishDialogController {
    constructor($mdDialog, AnalyzeService, $mdConstant, JwtService, $rootScope) {
      'ngInject';

      this._$mdDialog = $mdDialog;
      this._AnalyzeService = AnalyzeService;
      this.dataHolder = [];
      this.dateFormat = 'mm/dd/yyyy';
      this.hasSchedule = false;
      this.cronValidateField = false;
      this._JwtService = JwtService;
      this.resp = this._JwtService.getTokenObj();
      this.regexOfEmail = /^[_a-z0-9]+(\.[_a-z0-9]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,4})$/;
      const semicolon = 186;
      this.separatorKeys = [$mdConstant.KEY_CODE.ENTER, $mdConstant.KEY_CODE.COMMA, semicolon];
      this.emails = [];
      this.repeatIntervals = ['DAYS', 'WEEKS'];
      this.repeatInterval = this.repeatIntervals[0];
      this.repeatOrdinals = [1, 2, 3, 4, 5, 6, 7];
      this.repeatOrdinal = this.repeatOrdinals[0];
      this.daysOfWeek = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
      this.repeatOnDaysOfWeek = map(this.daysOfWeek, dayString => {
        return {
          keyword: dayString,
          checked: false
        };
      });
      this.crondetails = {};
      this.endCriteria = {
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
      this.endCriterion = this.endCriteria.never.keyword;
      this.loadCronLayout = false;
      this._$rootScope = $rootScope;
      this.ftp = [];
      this.locations = [];
    }

    $onInit() {
      this.scheduleState = 'new';
      this._AnalyzeService.getCategories(PRIVILEGES.PUBLISH)
        .then(response => {
          this.dataHolder = response;
          this.setDefaultCategory();
          this.fetchCronDetails();
        });
    }

    getFTPLocations() {
      this._AnalyzeService.getlistFTP(this.resp.ticket.custCode).then(response => {
        this.locations = response.data.data.ftps;
        //this.locations = ['alias1', 'alias2', 'alias3', 'alias4', 'alias5'];
      });
    }

    fetchCronDetails() {
      this.$dialog.showLoader();
      if (this.model.type !== 'chart') {
        this.getFTPLocations();
      }
      this.requestCron = {
        jobName: this.model.id,
        categoryId: this.model.categoryId,
        groupName: this.resp.ticket.custCode
      };
      this._$rootScope.showProgress = true;
      this._AnalyzeService.getCronDetails(this.requestCron).then(response => {
        this._$rootScope.showProgress = false;
        this.loadCronLayout = true;
        this.$dialog.hideLoader();
        if (response.data.data.jobDetails) {
          this.crondetails = {
            cronexp: response.data.data.jobDetails.cronExpression,
            activeTab: response.data.data.jobDetails.activeTab,
            activeRadio: response.data.data.jobDetails.activeRadio
          };
          if (response.data.data.jobDetails.analysisID) {
            this.scheduleState = 'exist';
          }
          if (this.model.type !== 'chart') {
            this.ftp = response.data.data.jobDetails.ftp;
          }
          this.emails = response.data.data.jobDetails.emailList;
          this.hasSchedule = true;
        }
      });
    }

    generateSchedulePayload() {
      if (!this.hasSchedule) {
        return {execute: true, payload: this.model};
      }

      return {execute: true, payload: this.model};
    }

    setDefaultCategory() {
      if (!this.model.categoryId) {
        const defaultCategory = find(this.dataHolder, category => category.children.length > 0);

        if (defaultCategory) {
          this.model.categoryId = first(defaultCategory.children).id;
        }
      }
    }

    cancel() {
      this._$mdDialog.cancel();
    }

    onCronChanged(cronexpression) {
      this.crondetails = cronexpression;
    }

    publish() {
      if (this.hasSchedule === false) {
        this.scheduleState = 'delete';
        this.model.schedule = {
          categoryId: this.model.categoryId,
          groupName: this.resp.ticket.custCode,
          jobName: this.model.id,
          scheduleState: this.scheduleState
        };
        this.triggerSchedule();
      } else if (this.validateForm()) {
        this.model.schedule = {
          scheduleState: this.scheduleState,
          activeRadio: this.crondetails.activeRadio,
          activeTab: this.crondetails.activeTab,
          analysisID: this.model.id,
          analysisName: this.model.name,
          cronExpression: this.crondetails.cronexp,
          description: this.description,
          emailList: this.emails,
          ftp: this.ftp,
          fileType: 'csv',
          jobName: this.model.id,
          metricName: this.model.metricName,
          type: this.model.type,
          userFullName: this.model.userFullName,
          jobScheduleTime: moment().format(),
          categoryID: this.model.categoryId,
          jobGroup: this.resp.ticket.custCode
        };
        this.triggerSchedule();
      }
    }

    triggerSchedule() {
      const {payload, execute} = this.generateSchedulePayload();
      const promise = this.onPublish({model: payload, execute});
      this._$mdDialog.hide(promise);
    }

    validateForm() {
      this.errorFlagMsg = false;
      this.emailValidateFlag = false;
      this.cronValidateField = false;
      let validationCheck = true;

      const validateFields = {
        emails: this.validateEmails(this.emails),
        schedule: this.validateSchedule(),
        publish: this.validatePublishSelection()
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
      if (isEmpty(this.emails) && isEmpty(this.ftp) && this.model.type !== 'chart') {
        this.errorFlagMsg = true;
        return false;
      }
      return true;
    }

    validateSchedule() {
      if (isEmpty(this.crondetails.cronexp)) {
        this.cronValidateField = true;
        return false;
      }
    }

    validateEmails(emails) {
      const emailsList = emails;
      let emailsAreValid = true;
      if (isEmpty(emailsList) && this.model.type === 'chart') {
        emailsAreValid = false;
        this.emailValidateFlag = true;
      }
      forEach(emailsList, email => {
        const isEmailvalid = this.regexOfEmail.test(email.toLowerCase());
        if (!isEmailvalid) {
          emailsAreValid = false;
          // cancel forEach
          this.emailValidateFlag = true;
        }
      });
      return emailsAreValid;
    }

    validateThisEmail(oneEmail) {
      return this.regexOfEmail.test(oneEmail.toLowerCase());
    }
  }
};
