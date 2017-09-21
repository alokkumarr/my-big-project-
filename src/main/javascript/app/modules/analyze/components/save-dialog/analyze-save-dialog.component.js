import * as first from 'lodash/first';
import * as find from 'lodash/find';

import * as template from './analyze-save-dialog.component.html';
import style from './analyze-save-dialog.component.scss';

import {Events, PRIVILEGES} from '../../consts';

export const AnalyzeSaveDialogComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<',
    onSave: '&'
  },
  controller: class AnalyzeSaveDialogController {
    constructor($mdDialog, $timeout, $eventEmitter, AnalyzeService) {
      'ngInject';
      this._$mdDialog = $mdDialog;
      this._$timeout = $timeout;
      this._$eventEmitter = $eventEmitter;
      this._AnalyzeService = AnalyzeService;

      this.dataHolder = [];
    }

    $onInit() {
      this._AnalyzeService.getCategories(PRIVILEGES.CREATE)
        .then(response => {
          this.dataHolder = response;
          this.setDefaultCategory();
        });
    }

    setDefaultCategory() {
      if (!this.model.categoryId) {
        const defaultCategory = find(this.dataHolder, category => category.children.length > 0);

        if (defaultCategory) {
          this.model.categoryId = first(defaultCategory.children).id;
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
