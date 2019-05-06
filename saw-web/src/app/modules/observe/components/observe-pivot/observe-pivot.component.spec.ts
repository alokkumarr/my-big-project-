import { Component } from '@angular/core';
import { TestBed, ComponentFixture } from '@angular/core/testing';
import 'hammerjs';
import { ObservePivotComponent } from './observe-pivot.component';
import { AnalyzeService } from '../../../analyze/services/analyze.service';

/* Stubs */
const AnalyzeServiceStub = {
  getDataBySettings: () => {
    return new Promise(res => res({ data: {} }));
  }
};

const analysisStub = {
  artifacts: [{ columns: [] }],
  sipQuery: {
    artifacts: [{ artifactsName: 'abc', fields: [] }],
    sorts: [],
    filters: []
  }
};

@Component({
  selector: 'pivot-grid',
  template: '<h1> Pivot </h1>',
  inputs: ['artifactColumns', 'sorts', 'updater', 'mode', 'data']
})
class PivotGridStubComponent {}

describe('Observe Pivot Component', () => {
  let fixture: ComponentFixture<ObservePivotComponent>;
  beforeEach(done => {
    TestBed.configureTestingModule({
      imports: [],
      declarations: [ObservePivotComponent, PivotGridStubComponent],
      providers: [{ provide: AnalyzeService, useValue: AnalyzeServiceStub }]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(ObservePivotComponent);
        const comp = fixture.componentInstance;
        comp.analysis = analysisStub;

        fixture.detectChanges();
        done();
      });
  });

  it('should exist', () => {
    expect(fixture.componentInstance).not.toBeNull();
  });
});
