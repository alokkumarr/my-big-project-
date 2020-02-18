import { TestBed } from '@angular/core/testing';
import { string2CookiesArray } from './cookies.service';

const cookiesString = 'cookie1=cookie1value; cookie2=cookie2value';
const cookiesArray = [
  ['cookie1', 'cookie1value'],
  ['cookie2', 'cookie2value']
];

describe('Cookies Service', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: []
    });
  });

  it('should transform cookiesString into array form', () => {
    const transformedCookies: string[][] = string2CookiesArray(cookiesString);
    expect(transformedCookies).toEqual(cookiesArray);
  });
});
