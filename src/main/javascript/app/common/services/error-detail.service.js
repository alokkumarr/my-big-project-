import * as get from 'lodash/get';
import * as truncate from 'lodash/truncate';

const ERROR_TITLE_LENGTH = 50;

export function errorDetailService() {
  'ngInject';

  return {
    getTitle,
    getDetail
  };

  function getTitle(error, defaultMessage = 'Error') {
    const title = get(error, 'data.error.message') ||
          get(error, 'data.message', '');
    return title ? truncate(title, {
      length: ERROR_TITLE_LENGTH
    }) : defaultMessage;
  }

  function getDetail(error) {
    return error;
  }
}
