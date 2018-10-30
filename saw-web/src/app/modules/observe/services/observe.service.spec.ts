import { Router, ActivatedRoute } from "@angular/router";

import { TestBed, inject } from "@angular/core/testing";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { By } from "@angular/platform-browser";
import { DebugElement } from "@angular/core";

import { ObserveService } from "./observe.service";
import { JwtService, MenuService } from "../../../common/services";
import { Observable } from "rxjs/Observable";

const mockService = {};

describe("Observe Service", () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        ObserveService,
        { provide: JwtService, useValue: mockService },
        { provide: MenuService, useValue: mockService },
        { provide: Router, useValue: mockService },
        { provide: ActivatedRoute, useValue: mockService }
      ]
    }).compileComponents();
  });

  it("getSubcategoryCount works for empty array", inject(
    [ObserveService],
    (observe: ObserveService) => {
      expect(observe.getSubcategoryCount([])).toEqual(0);
    }
  ));

  it("executeKPI should exist and return an Observable", inject(
    [ObserveService],
    (observe: ObserveService) => {
      expect(observe.executeKPI({}) instanceof Observable).toEqual(true);
    }
  ));
});
