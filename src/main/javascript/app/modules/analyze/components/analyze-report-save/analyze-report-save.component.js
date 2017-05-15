import first from 'lodash/first';

import template from './analyze-report-save.component.html';
import style from './analyze-report-save.component.scss';

import {Events} from '../../consts';

export const AnalyzeReportSaveComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<',
    onSave: '&'
  },
  controller: class AnalyzeReportSaveController {
    constructor($mdDialog, $timeout, $eventEmitter, AnalyzeService) {
      'ngInject';
      this._$mdDialog = $mdDialog;
      this._$timeout = $timeout;
      this._$eventEmitter = $eventEmitter;
      this._AnalyzeService = AnalyzeService;

      this.dataHolder = {
        categories: []
      };
    }

    $onInit() {
      this._AnalyzeService.getCategories()
        .then(response => {
          this.dataHolder.categories = response;
          this.setDefaultCategory();
        });
    }

    setDefaultCategory() {
      if (!this.model.category) {
        const defaultCategory = first(this.dataHolder.categories);

        if (defaultCategory) {
          this.model.category = defaultCategory.id;
        }
      }
    }

    hide() {
      this.$dialog.hide();
    }

    save() {
      this.$dialog.showLoader();

      this._$timeout(() => {
        const payload = this.model;

        this._AnalyzeService.saveReport(payload)
          .then(response => {
            payload.id = response.id;

            this.onSave({
              $data: payload
            });

            this._$eventEmitter.emit(Events.AnalysesRefresh, payload);

            // if saved successfully
            this.$dialog.hide(true);
            // use this.$dialog.cancel(); on error
          })
          .finally(() => {
            this.$dialog.hideLoader();
          });
      }, 1000);
    }
  }
};
