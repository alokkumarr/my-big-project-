import * as defaultsDeep from 'lodash/defaultsDeep';
import * as cloneDeep from 'lodash/cloneDeep';

export default class AbstractDesignerComponentController {
  constructor($injector) {
    this._$mdDialog = $injector.get('$mdDialog');
    this._$log = $injector.get('$log');

    this.draftMode = false;
    this.showProgress = false;
    this.didAnalysisChange = false;
    this.filters = [];
    this.sorts = [];
  }

  analysisUnSynched() {
    this.didAnalysisChange = true;
  }

  analysisSynched() {
    this.didAnalysisChange = false;
  }

  startDraftMode() {
    this.draftMode = true;
  }

  endDraftMode() {
    this.draftMode = false;
  }

  startProgress() {
    this.showProgress = true;
  }

  endProgress() {
    this.showProgress = false;
  }

  // Filters
  onClearAllFilters() {
    this.filters = [];
    this.didAnalysisChange = true;
    this.startDraftMode();
  }

  clearFilters() {
    this.onClearAllFilters();
  }

  onFilterRemoved(/* index */) {
    this.didAnalysisChange = true;
    this.startDraftMode();
  }

  onApplyFilters({filters, filterBooleanCriteria} = {}) {
    if (filters) {
      this.filters = filters;
      this.didAnalysisChange = true;
      this.startDraftMode();
      this.onRefreshData();
    }
    if (filterBooleanCriteria) {
      this.model.sqlBuilder.booleanCriteria = filterBooleanCriteria;
    }
  }
  // Filters END

  goBack() {
    if (!this.draftMode) {
      this.$dialog.hide();
      return;
    }

    const confirm = this._$mdDialog.confirm()
      .title('There are unsaved changes')
      .textContent('Do you want to discard unsaved changes and go back?')
      .multiple(true)
      .ok('Discard')
      .cancel('Cancel');

    this._$mdDialog.show(confirm).then(() => {
      this.$dialog.hide();
    }, err => {
      if (err) {
        this._$log.error(err);
      }
    });
  }

  openSortModal(ev, model) {
    const tpl = '<analyze-sort-dialog model="model"></analyze-sort-dialog>';

    return this.showModal({
      template: tpl,
      controller: scope => {
        scope.model = model;
      }
    }, ev);
  }

  openPreviewModal(template, ev, model) {

    this.showModal({
      template,
      controller: scope => {
        scope.model = model;
      }
    }, ev);
  }

  openDescriptionModal(ev, model) {
    const tpl = '<analyze-description-dialog model="model" on-save="onSave($data)"></analyze-description-dialog>';

    this.showModal({
      template: tpl,
      controller: scope => {
        scope.model = {
          description: model.description
        };

        scope.onSave = data => {
          this.startDraftMode();
          model.description = data.description;
        };
      }
    }, ev);
  }

  openSaveModal(ev, payload) {
    const tpl = '<analyze-save-dialog model="model" on-save="onSave($data)"></analyze-save-dialog>';

    this.showModal({
      template: tpl,
      controller: scope => {
        scope.model = payload;

        scope.onSave = data => {
          this.model.id = data.id;
          this.model.name = data.name;
          this.model.description = data.description;
          this.model.category = data.categoryId;
        };
      }
    }, ev)
      .then(successfullySaved => {
        if (successfullySaved) {
          this.endDraftMode();
          this.$dialog.hide(successfullySaved);
        }
      });
  }

  openFiltersModal(ev) {
    const tpl = '<analyze-filter-modal options="options" filters="filters" artifacts="artifacts" filter-boolean-criteria="booleanCriteria"></analyze-filter-modal>';

    this.showModal({
      template: tpl,
      controller: scope => {
        scope.options = {
          type: this.model.type
        };
        scope.filters = cloneDeep(this.filters);
        scope.artifacts = this.model.artifacts;
        scope.booleanCriteria = this.model.sqlBuilder.booleanCriteria;
      }
    }, ev).then(this.onApplyFilters.bind(this));
  }

  showModal(config, ev) {
    config = defaultsDeep(config, {
      multiple: true,
      autoWrap: false,
      focusOnOpen: false,
      fullscreen: true,
      targetEvent: ev,
      clickOutsideToClose: true
    });

    return this._$mdDialog.show(config);
  }
}
