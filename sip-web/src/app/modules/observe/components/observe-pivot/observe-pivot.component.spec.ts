import { Component } from '@angular/core';
import { TestBed, ComponentFixture } from '@angular/core/testing';
import { MaterialModule } from '../../../../material.module';
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
  name: 'Untitled Analysis',
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
  inputs: ['artifactColumns', 'sorts', 'updater', 'mode', 'data', 'name']
})
class PivotGridStubComponent {}

describe('Observe Pivot Component', () => {
  let fixture: ComponentFixture<ObservePivotComponent>;
  beforeEach(done => {
    TestBed.configureTestingModule({
      imports: [MaterialModule],
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
