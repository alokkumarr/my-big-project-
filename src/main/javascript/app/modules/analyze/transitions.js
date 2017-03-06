import pick from 'lodash/pick';
import {LAST_ANALYSES_CATEGORY_ID} from './consts';

export function transitions($transitions, localStorageService) {
  'ngInject';
  $transitions.onEnter({entering: 'analyze.view'}, onEnterAnalyzeView);

  function onEnterAnalyzeView(transition, state) {
    const analysesId = transition.params().id;

    if (analysesId) {
      localStorageService.set(LAST_ANALYSES_CATEGORY_ID, analysesId);
    }
  }
}
