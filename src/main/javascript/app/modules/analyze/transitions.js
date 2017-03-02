import pick from 'lodash/pick';

export function transitions($transitions, localStorageService, $state, $timeout) {
  'ngInject';
  const key = 'lastAnalysesListId';

  let firstPageLoad = true;

  $transitions.onEnter({entering: 'analyze'}, onEnterAnalyze);
  $transitions.onEnter({entering: 'analyze.view'}, onEnterAnalyzeView);
  $transitions.onEnter({entering: state => state.name.match(/analyze.\w*/)}, setFirstPageLoadFalse);

  function onEnterAnalyze(transition, state) {
    firstPageLoad = true;
    // we have to use timeout, because this listener fires when we go to a child as well
    $timeout(() => {
      if (firstPageLoad) {
        const id = localStorageService.get(key);
        if (id) {
          $state.go('analyze.view', {id});
        }
      }
    });
  }

  function onEnterAnalyzeView(transition, state) {

    const analysesId = transition.params().id;
    if (analysesId) {
      localStorageService.set(key, analysesId);
    }
  }

  function setFirstPageLoadFalse(transition, state) {
    firstPageLoad = false;
  }
}
