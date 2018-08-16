import * as defaultsDeep from 'lodash/defaultsDeep';
import * as clone from 'lodash/clone';
import * as cloneDeep from 'lodash/cloneDeep';

import {AnalyseTypes} from '../../consts';

import 'rxjs/add/operator/first';
import 'rxjs/add/operator/toPromise';

import {EXECUTION_MODES, EXECUTION_DATA_MODES} from '../../services/analyze.service';

export function AnalyzeActionsService($mdDialog, $eventEmitter, $rootScope, AnalyzeService, toastMessage, $log, $injector) {
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

  function execute(analysis, mode = EXECUTION_MODES.LIVE) {
    return $injector.get('FilterService').getRuntimeFilterValues(analysis).then(model => {
      if (model) {
        AnalyzeService.executeAnalysis(model, mode);
      }
      return model;
    });
  }

  function fork(analysis) {
    const model = cloneDeep(analysis);
    model.name += ' Copy';
    return openEditModal(model, 'fork');
  }

  function edit(analysis) {
    return openEditModal(cloneDeep(analysis), 'edit');
  }

  function publish(analysis) {
    return openPublishModal(clone(analysis));
  }

  function print() {
  }

  function exportAnalysis(analysisId, executionId, analysisType, executionType = EXECUTION_DATA_MODES.NORMAL) {
    return AnalyzeService.getExportData(analysisId, executionId, analysisType, executionType);
  }

  function deleteAnalysis(analysis) {
    return openDeleteModal(analysis);
  }

  function openPublishModal(analysis) {
    const template = '<analyze-publish-dialog model="model" on-publish="onPublish(model)"></analyze-publish-dialog>';

    return showDialog({
      template,
      controller: scope => {
        scope.model = analysis;
        scope.onPublish = doPublish;
      }
    });
  }

  function doPublish(analysis) {
    $rootScope.showProgress = true;
    return AnalyzeService.publishAnalysis(analysis, execute).then(updatedAnalysis => {
      $rootScope.showProgress = false;
      toastMessage.info(execute ?
        'Analysis has been updated.' :
        'Analysis schedule changes have been updated.');
      return updatedAnalysis;
    }, () => {
      $rootScope.showProgress = false;
    });
  }

  function openEditModal(analysis, mode) {
    /* Delayed injection of service to battle issues with downgradeModule */
    const AnalyzeDialogService = $injector.get('AnalyzeDialogService');

    switch (analysis.type) {
    case AnalyseTypes.Chart:
    case AnalyseTypes.ESReport:
    case AnalyseTypes.Report:
    case AnalyseTypes.Pivot:
      return AnalyzeDialogService.openEditAnalysisDialog(analysis, mode)
        .afterClosed().first().toPromise().then(status => {
          if (!status) {
            return {};
          }
          return status;
        });
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
