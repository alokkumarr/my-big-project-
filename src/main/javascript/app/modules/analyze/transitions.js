import pick from 'lodash/pick';

export function transitions($transitions, localStorageService, $state, $timeout) {
  'ngInject';
  const key = 'lastAnalysesListId';

  let firstPageLoad = false;

  $transitions.onEnter({entering: 'analyze'}, onEnterAnalyze);
  $transitions.onEnter({entering: 'analyze.view'}, onEnterAnalyzeView);
  $transitions.onEnter({entering: state => state.name.match(/analyze.\w*/)}, setFirstPageLoadFalse);

  function onEnterAnalyze(transition, state) {
    firstPageLoad = true;
    $timeout(() => {
      if (firstPageLoad) {
        const id = localStorageService.get(key);
        if (id) {
          // $state.go('analyze.view', {id});
          console.log('go to: ', id);
        }
      }
    });
  }

  function onEnterAnalyzeView(transition, state) {

    const analysesId = transition.params().id;
    if (analysesId) {
      console.log('save');
      localStorageService.set(key, analysesId);
    }
  }

  function setFirstPageLoadFalse(transition, state) {
    firstPageLoad = false;
  }
}
