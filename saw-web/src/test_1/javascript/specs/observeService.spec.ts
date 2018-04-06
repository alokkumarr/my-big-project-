import { expect } from 'chai';
import { UIRouter } from '@uirouter/angular'

import { configureTests } from '../helpers/configureTests';
import { TestBed, inject } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { ObserveService } from '../../../main/javascript/app/modules/observe/services/observe.service';
import { JwtService } from '../../../main/javascript/login/services/jwt.service';
import { MenuService } from '../../../main/javascript/app/common/services/menu.service';

configureTests();

const mockService =  {
};

describe('Observe Service', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ],
      providers: [
        ObserveService,
        {provide: JwtService, useValue: mockService},
        {provide: MenuService, useValue: mockService},
        {provide: UIRouter, useValue: mockService}
      ]
    }).compileComponents();
  });

  it('getSubcategoryCount works for empty array', inject([ObserveService], (observe: ObserveService) => {
    expect(observe.getSubcategoryCount([])).to.equal(0);
  }));
});
