import { Injectable } from '@angular/core';
import * as get from 'lodash/get';
import * as truncate from 'lodash/truncate';

const ERROR_TITLE_LENGTH = 50;

@Injectable()
export class ErrorDetailService {
  getTitle(error, defaultMessage = 'Error') {
    const title = get(error, 'data.error.message') ||
          get(error, 'data.message', '');
    return title ? truncate(title, {
      length: ERROR_TITLE_LENGTH
    }) : defaultMessage;
  }

  getDetail(error) {
    return JSON.stringify(error);
  }
}
