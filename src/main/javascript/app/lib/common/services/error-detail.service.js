import get from 'lodash/get';
import truncate from 'lodash/truncate';

const ERROR_TITLE_LENGTH = 30;

export function errorDetailService() {
  'ngInject';

  return {
    getTitle,
    getDetail
  };

  function getTitle(error, defaultMessage = 'Error') {
    const title = get(error, 'data.error.message') ||
      get(error, 'data.message', defaultMessage);
    return truncate(title, ERROR_TITLE_LENGTH);
  }

  function getDetail(error) {
    return error;
  }
}
