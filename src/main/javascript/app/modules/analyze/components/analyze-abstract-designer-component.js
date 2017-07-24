import defaultsDeep from 'lodash/defaultsDeep';
import cloneDeep from 'lodash/cloneDeep';

export default class AbstractDesignerComponentController {
  constructor($mdDialog) {
    this._$mdDialog = $mdDialog;

    this.draftMode = false;
    this.showProgress = false;
    this.analysisChanged = false;
    this.filters = [];
  }

  $onInit() {
  }

  $onDestroy() {
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

  clearFilters() {
    this.filters = [];
    this.analysisChanged = true;
    this.startDraftMode();
  }

  onFilterRemoved(index) {
    this.filters.splice(index, 1);
    this.analysisChanged = true;
    this.startDraftMode();
  }

  onApplyFilters({filters, filterBooleanCriteria} = {}) {
    if (filters) {
      this.filters = filters;
      this.analysisChanged = true;
      this.startDraftMode();
    }
    if (filterBooleanCriteria) {
      this.model.sqlBuilder.booleanCriteria = filterBooleanCriteria;
    }
  }

  openPreviewModal(template, ev, model) {

    this.showModal({
      template,
      controller: scope => {
        scope.model = model;
      },
      fullscreen: true
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
      fullscreen: true,
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
    const tpl = '<analyze-filter-modal filters="filters" artifacts="artifacts" filter-boolean-criteria="booleanCriteria"></analyze-filter-modal>';
    this._$mdDialog.show({
      template: tpl,
      controller: scope => {
        scope.filters = cloneDeep(this.filters);
        scope.artifacts = this.model.artifacts;
        scope.booleanCriteria = this.model.sqlBuilder.booleanCriteria;
      },
      targetEvent: ev,
      fullscreen: true,
      autoWrap: false,
      multiple: true
    }).then(this.onApplyFilters.bind(this));
  }

  showModal(config, ev) {
    config = defaultsDeep(config, {
      multiple: true,
      autoWrap: false,
      focusOnOpen: false,
      targetEvent: ev,
      clickOutsideToClose: true
    });

    return this._$mdDialog.show(config);
  }
}
