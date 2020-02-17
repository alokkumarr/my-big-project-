import { Injectable } from '@angular/core';
import * as split from 'lodash/split';
import * as find from 'lodash/find';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpSplit from 'lodash/fp/split';
import * as fpMap from 'lodash/fp/map';

export const string2CookiesArray: (cookies: string) => [][] = cookies =>
  fpPipe(
    fpSplit('; '),
    fpMap(cookieString => split(cookieString, '='))
  )(cookies);

@Injectable()
export class CookiesService {
  public get(targetKey) {
    const cookiesArray = string2CookiesArray(document.cookie);
    const targetCookie = find(cookiesArray, ([key]) => targetKey === key);
    return targetCookie ? targetCookie[1] : null;
  }

  public clear(targetKey) {
    document.cookie = `${targetKey}=; path=/; domain=${document.domain}; expires=Thu, 01 Jan 1970 00:00:01 GMT;`;
  }
}
