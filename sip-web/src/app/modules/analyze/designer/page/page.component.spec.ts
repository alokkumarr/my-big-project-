import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Store } from '@ngxs/store';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Location } from '@angular/common';
import { MatDialog } from '@angular/material';
import { ActivatedRoute, Router } from '@angular/router';
import { AnalyzeService } from '../../services/analyze.service';
import { ExecuteService } from '../../services/execute.service';
import {
  JwtService,
  ToastService,
  MenuService
} from '../../../../common/services';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { DesignerPageComponent } from './page.component';
import { DesignerService } from '../designer.service';

class LocationStub {
  back() {
    return true;
  }
}

class StoreStub {
  selectSnapshot() {}
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

class MatDialogStub {
  open() {
    return true;
  }
}

describe('DesignerPageComponent', () => {
  let component: DesignerPageComponent;
  let fixture: ComponentFixture<DesignerPageComponent>;
  let readAnalysisSpy;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [DesignerPageComponent],
      providers: [
        { provide: Location, useValue: new LocationStub() },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: Router, useValue: new RouterStub() },
        { provide: ExecuteService, useValue: new ExecuteServiceStub() },
        { provide: JwtService, useValue: new JwtServiceStub() },
        { provide: MatDialog, useValue: new MatDialogStub() },
        { provide: Store, useValue: new StoreStub() },
        { provide: ToastService, useValue: {} },
        { provide: MenuService, useValue: {} },
        AnalyzeService,
        DesignerService
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

  it('should open a dialog on warning user', () => {
    const service = TestBed.get(MatDialog);
    const spy = spyOn(service, 'open').and.returnValue({
      afterClosed: () => ({ subscribe: () => {} })
    });
    component.warnUser();
    expect(spy).toHaveBeenCalled();
  });

  describe('onBack', () => {
    it('should warn user if in draft mode', () => {
      const service = TestBed.get(MatDialog);
      const spy = spyOn(service, 'open').and.returnValue({
        afterClosed: () => ({ subscribe: () => {} })
      });
      component.onBack(true);
      expect(spy).toHaveBeenCalled();
    });

    it('should take the user back if not in draft mode', () => {
      const service = TestBed.get(Location);
      const spy = spyOn(service, 'back').and.returnValue(true);
      component.onBack(false);
      expect(spy).toHaveBeenCalled();
    });
  });

  describe('fixArtifactsForSIPQuery', () => {
    it('should return artifacts if in designer edit', () => {
      const artifacts = component.fixArtifactsForSIPQuery(
        { designerEdit: true } as any,
        1
      );
      expect(artifacts).toEqual(1);
    });

    it('should add derived metrics to artifacts if not in designer edit', () => {
      const service = TestBed.get(DesignerService);
      const spy = spyOn(
        service,
        'addDerivedMetricsToArtifacts'
      ).and.returnValue({});

      component.fixArtifactsForSIPQuery(
        { designerEdit: false, sipQuery: {} } as any,
        1
      );
      expect(spy).toHaveBeenCalled();
    });
  });

  describe('onSave', () => {
    it('should not call execution until requested', () => {
      const spy = spyOn(
        TestBed.get(ExecuteService),
        'executeAnalysis'
      ).and.returnValue({ then: () => {} });
      component.onSave({ analysis: {}, requestExecution: false } as any);
      expect(spy).not.toHaveBeenCalled();
    });
  });
});
