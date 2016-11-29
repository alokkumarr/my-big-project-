import angular from 'angular';

import template from './analyze-page.component.html';
import newAnalysisTemplate from './new-analysis.html';
import {newAnalysisController} from './new-analysis.controller';

export const AnalyzePageComponent = {
  template,
  controller: class AnalyzePageController {
    /** @ngInject */
    constructor($log, $mdDialog, $document, $mdSidenav) {
      this.$mdSidenav = $mdSidenav;
      this.$log = $log;
      this.$mdDialog = $mdDialog;
      this.$document = $document;

      this.menu = [{
        name: 'My Analyses',
        children: [{
          name: 'Order Fulfillment'
        }, {
          name: 'Category 2'
        }, {
          name: 'Category 3'
        }, {
          name: 'Category 4'
        }, {
          name: 'Category 5'
        }]
      }, {
        name: 'Folder 2'
      }, {
        name: 'Folder 3'
      }];
    }

    openNewAnalysisModal(ev) {
      this.$mdDialog.show({
        controller: newAnalysisController,
        controllerAs: '$ctrl',
        template: newAnalysisTemplate,
        parent: angular.element(this.$document.body),
        targetEvent: ev,
        clickOutsideToClose: true,
        fullscreen: true // Only for -xs, -sm breakpoints.
      })
        .then(answer => {
          this.$log.info(`You created the analysis: "${answer}".`);
        }, () => {
          this.$log.info('You cancelled new Analysis modal.');
        });
    }
  }
};
