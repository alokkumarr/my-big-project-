import { expect } from 'chai';
import { UIRouter } from '@uirouter/angular';

import { configureTests } from '../../../../../../test/javascript/helpers/configureTests';
import { TestBed, inject } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { ObserveService } from './observe.service';
import { JwtService } from '../../../../login/services/jwt.service';
import { MenuService } from '../../../common/services/menu.service';
import { Observable } from 'rxjs/Observable';

configureTests();

const mockService = {};

describe('Observe Service', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        ObserveService,
        { provide: JwtService, useValue: mockService },
        { provide: MenuService, useValue: mockService },
        { provide: UIRouter, useValue: mockService }
      ]
    }).compileComponents();
  });

  it(
    'getSubcategoryCount works for empty array',
    inject([ObserveService], (observe: ObserveService) => {
      expect(observe.getSubcategoryCount([])).to.equal(0);
    })
  );

  it(
    'executeKPI should exist and return an Observable',
    inject([ObserveService], (observe: ObserveService) => {
      expect(observe.executeKPI({}) instanceof Observable).to.equal(true);
    })
  );
});
