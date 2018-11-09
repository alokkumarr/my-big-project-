import { Injectable } from '@angular/core';
import * as get from 'lodash/get';
import * as truncate from 'lodash/truncate';

const ERROR_TITLE_LENGTH = 50;

@Injectable()
export class ErrorDetailService {
  getDetail(error) {
    return JSON.stringify(error);
  }

  getTitle(error, defaultMessage = 'Error') {
    const title =
      get(error, 'error.error.message') ||
      get(error, 'error.message') ||
      get(error, 'message', '') ||
      get(error, 'error', '');
    /* prettier-ignore */
    return title ? truncate(title, {
      length: ERROR_TITLE_LENGTH
    }) : defaultMessage;
  }
}
