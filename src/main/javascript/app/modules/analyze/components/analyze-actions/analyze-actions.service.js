import defaultsDeep from 'lodash/defaultsDeep';

import {AnalyseTypes} from '../../consts';

export function AnalyzeActionsService($mdDialog, $rootScope, AnalyzeService, toastMessage, FilterService, $log) {
  'ngInject';
  return {
    execute,
    fork,
    edit,
    publish,
    exportAnalysis,
    print,
    deleteAnalysis
  };

  function execute(analysis) {
    return FilterService.getRuntimeFilterValues(analysis).then(model => {
      AnalyzeService.executeAnalysis(model);
      return model;
    });
  }

  function fork(analysis) {
    analysis.name += ' Copy';
    openEditModal(analysis, 'fork');
  }

  function edit(analysis) {
    openEditModal(analysis, 'edit');
  }

  function publish(analysis) {
    openPublishModal(analysis);
  }

  function print() {
  }

  function exportAnalysis() {
  }

  function deleteAnalysis(analysis) {
    return openDeleteModal(analysis);
  }

  function openPublishModal(analysis) {
    const template = '<analyze-publish-dialog model="model" on-publish="onPublish(model)"></analyze-publish-dialog>';

    showDialog({
      template,
      controller: scope => {
        scope.model = analysis;
        scope.onPublish = doPublish;
      }
    });
  }

  function doPublish(analysis) {
    $rootScope.showProgress = true;
    AnalyzeService.publishAnalysis(analysis).then(() => {
      $rootScope.showProgress = false;
    }, () => {
      $rootScope.showProgress = false;
    });
  }

  function openEditModal(analysis, mode) {
    const openModal = template => {
      showDialog({
        template,
        controller: scope => {
          scope.model = analysis;
        },
        multiple: true
      });
    };

    switch (analysis.type) {
      case AnalyseTypes.Report:
        openModal(`<analyze-report model="model" mode="${mode}"></analyze-report>`);
        break;
      case AnalyseTypes.Chart:
        openModal(`<analyze-chart model="model" mode="${mode}"></analyze-chart>`);
        break;
      case AnalyseTypes.Pivot:
        openModal(`<analyze-pivot model="model" mode="${mode}"></analyze-pivot>`);
        break;
      default:
    }
  }

  function openDeleteModal(analysis) {
    const confirm = $mdDialog.confirm()
          .title('Are you sure you want to delete this analysis?')
          .textContent('Any published analyses will also be deleted.')
      .ok('Delete')
      .cancel('Cancel');

    return $mdDialog.show(confirm).then(() => {
      return removeAnalysis(analysis);
    }, err => {
      if (err) {
        $log.error(err);
      }
    });
  }

  function removeAnalysis(analysis) {
    $rootScope.showProgress = true;
    return AnalyzeService.deleteAnalysis(analysis).then(() => {
      $rootScope.showProgress = false;
      toastMessage.info('Analysis deleted.');
      return analysis;
    }, err => {
      $rootScope.showProgress = false;
      toastMessage.error(err.message || 'Analysis not deleted.');
    });
  }

  function showDialog(config) {
    config = defaultsDeep(config, {
      controllerAs: '$ctrl',
      multiple: false,
      autoWrap: false,
      focusOnOpen: false,
      clickOutsideToClose: true,
      fullscreen: false
    });

    return $mdDialog.show(config);
  }
}
