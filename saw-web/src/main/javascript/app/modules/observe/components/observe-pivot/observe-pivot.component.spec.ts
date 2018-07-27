import { expect } from 'chai';

import { configureTests } from '../../../../../../../test/javascript/helpers/configureTests';
import { Component } from '@angular/core';
import { TestBed, ComponentFixture } from '@angular/core/testing';

import { MaterialModule } from '../../../../material.module';
import { ObservePivotComponent } from './observe-pivot.component';
import { AnalyzeService } from '../../../analyze/services/analyze.service';

/* Stubs */
const AnalyzeServiceStub: Partial<AnalyzeService> = {
  getDataBySettings: () => {
    return new Promise(res => res({ data: {} }));
  }
};

const analysisStub = {
  artifacts: [{ columns: [] }],
  sqlBuilder: { sorts: [], filters: [], dataFields: [], nodeFields: [] }
};

@Component({
  selector: 'pivot-grid',
  template: '<h1> Pivot </h1>',
  inputs: ['artifactColumns', 'sorts', 'updater', 'mode', 'data']
})
class PivotGridStub {}

configureTests();

describe('Observe Pivot Component', () => {
  let fixture: ComponentFixture<ObservePivotComponent>, el: HTMLElement;
  beforeEach(done => {
    TestBed.configureTestingModule({
      imports: [],
      declarations: [ObservePivotComponent, PivotGridStub],
      providers: [{ provide: AnalyzeService, useValue: AnalyzeServiceStub }]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(ObservePivotComponent);
        const comp = fixture.componentInstance;
        comp.analysis = analysisStub;

        el = fixture.nativeElement;

        fixture.detectChanges();
        done();
      });
  });

  it('should exist', () => {
    expect(fixture.componentInstance).to.not.be.null;
  });
});
