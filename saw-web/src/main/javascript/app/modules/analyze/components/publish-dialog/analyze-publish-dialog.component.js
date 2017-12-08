import * as map from 'lodash/map';
import * as get from 'lodash/get';
import * as forEach from 'lodash/forEach';
import * as reduce from 'lodash/reduce';
import * as find from 'lodash/find';
import * as isEmpty from 'lodash/isEmpty';
import * as first from 'lodash/first';

import * as template from './analyze-publish-dialog.component.html';
import style from './analyze-publish-dialog.component.scss';

import {PRIVILEGES} from '../../consts';

const F2B_DICTIONARY = {
  WEEKS: 'weekly',
  DAYS: 'daily'
};

const B2F_DICTIONARY = {
  weekly: 'WEEKS',
  daily: 'DAYS'
};

export const AnalyzePublishDialogComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<',
    onPublish: '&'
  },
  controller: class AnalyzePublishDialogController {
    constructor($mdDialog, AnalyzeService, $mdConstant) {
      'ngInject';

      this._$mdDialog = $mdDialog;
      this._AnalyzeService = AnalyzeService;
      this.dataHolder = [];
      this.dateFormat = 'mm/dd/yyyy';
      this.hasSchedule = false;
      this.regexOfEmail = /^[_a-z0-9]+(\.[_a-z0-9]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,4})$/;
      const semicolon = 186;
      this.separatorKeys = [$mdConstant.KEY_CODE.ENTER, $mdConstant.KEY_CODE.COMMA, semicolon];
      if (this.model.isScheduled === 'true') {
        this.emails = get(this.model.schedule, 'emails') || [];
      } else {
        this.emails = [];
      }
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
    }

    $onInit() {
      this.populateSchedule();
      this._AnalyzeService.getCategories(PRIVILEGES.PUBLISH)
        .then(response => {
          this.dataHolder = response;
          this.setDefaultCategory();
        });
    }

    populateSchedule() {
      if (isEmpty(this.model.schedule)) {
        return;
      }

      this.hasSchedule = true;
      this.repeatOrdinal = this.model.schedule.repeatInterval;
      this.repeatInterval = B2F_DICTIONARY[this.model.schedule.repeatUnit];
      forEach(this.repeatOnDaysOfWeek, day => {
        day.checked = Boolean(get(this.model, `schedule.repeatOnDaysOfWeek.${day.keyword.toLowerCase()}`));
      });
    }

    generateSchedulePayload() {
      if (!this.hasSchedule) {
        this.model.schedule = null;
        this.model.scheduleHuman = '';
        return {execute: true, payload: this.model};
      }

      this.model.schedule = {
        emails: this.emails,
        repeatUnit: F2B_DICTIONARY[this.repeatInterval],
        repeatInterval: this.repeatOrdinal,
        repeatOnDaysOfWeek: reduce(this.repeatOnDaysOfWeek, (result, day) => {
          result[day.keyword.toLowerCase()] = day.checked;
          return result;
        }, {})
      };

      this.model.scheduleHuman = this._AnalyzeService.scheduleToString(this.model.schedule);

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

    publish() {
      if (!this.validateEmails(this.emails)) {
        this.emailValidateFlag = true;
        return;
      }
      const {payload, execute} = this.generateSchedulePayload();
      const promise = this.onPublish({model: payload, execute});
      this._$mdDialog.hide(promise);
    }

    validateEmails(emails) {
      const emailsList = emails;
      const flag = 0;
      forEach(emailsList, email => {
        if (!this.regexOfEmail.test(email.toLowerCase())) {
          flag = 1;
        }
      });
      if (flag === 0) {
        return true;
      } else {
        return false;
      }
    }

    validateThisEmail(oneEmail) {
      return this.regexOfEmail.test(oneEmail.toLowerCase());
    }
  }
};
