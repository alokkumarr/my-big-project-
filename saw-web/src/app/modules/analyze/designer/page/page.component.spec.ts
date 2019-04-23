import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Location } from '@angular/common';
import { MatDialog } from '@angular/material';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngxs/store';
import { AnalyzeService } from '../../services/analyze.service';
import { ExecuteService } from '../../services/execute.service';
import { JwtService } from '../../../../common/services';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { DesignerPageComponent } from './page.component';

class LocationStub {}
class AnalysisStubService {
  getArtifactsForDataSet() {
    return Promise.resolve([]);
  }
  readAnalysis() {
    return Promise.resolve({
      id: '124',
      category: '6',
      categoryId: '6',
      sipQuery: {}
    });
  }
}
class ActivatedRouteStub {
  snapshot = { queryParams: {} };
}

class RouterStub {}

class ExecuteServiceStub {
  executeAnalysis() {}
}

class JwtServiceStub {
  get userAnalysisCategoryId() {
    return 1;
  }
}

class MatDialogStub {}

class StoreStub {
  dispatch() {
    return {
      toPromise: () => Promise.resolve(true)
    };
  }

  selectSnapshot() {
    return { artifacts: [] };
  }
}

describe('DesignerPageComponent', () => {
  let component: DesignerPageComponent;
  let fixture: ComponentFixture<DesignerPageComponent>;
  let readAnalysisSpy;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DesignerPageComponent],
      providers: [
        { provide: Location, useValue: LocationStub },
        { provide: AnalyzeService, useValue: new AnalysisStubService() },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: Router, useValue: new RouterStub() },
        { provide: ExecuteService, useValue: new ExecuteServiceStub() },
        { provide: JwtService, useValue: new JwtServiceStub() },
        { provide: MatDialog, useValue: new MatDialogStub() },
        { provide: Store, useValue: new StoreStub() }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    const promise = new Promise(succ => {
      succ({});
    });
    readAnalysisSpy = spyOn(
      TestBed.get(AnalyzeService),
      'readAnalysis'
    ).and.returnValue(promise);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DesignerPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should read analysis if mode is not new', () => {
    expect(readAnalysisSpy).toHaveBeenCalled();
  });
});
