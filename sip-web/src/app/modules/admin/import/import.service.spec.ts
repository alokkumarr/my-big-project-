import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA, Component } from '@angular/core';
import { ImportService } from './import.service';
import { AdminService } from '../main-view/admin.service';
import { JwtService } from 'src/app/common/services';
import { AnalyzeService } from '../../analyze/services/analyze.service';
import * as keys from 'lodash/keys';

const adminStub = {};
const jwtStub = {};
const analyzeStub = {};

@Component({
  selector: 'test-component',
  template: '<h1>Test</h1>'
})
class TestComponent {
  constructor(public service: ImportService) {
    this.service;
  }
}

describe('CategorySelectComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TestComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      providers: [
        ImportService,
        { provide: AdminService, useValue: adminStub },
        { provide: JwtService, useValue: jwtStub },
        { provide: AnalyzeService, useValue: analyzeStub }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('create reference map', () => {
    it('should create a correct reference map', () => {
      const analyses = [
        { name: 'analysis1', type: 'report', semanticId: 'metric1' }
      ] as any;
      const metrics = { metric1: { metricName: 'metric1', id: 'metric1' } };
      const map1 = component.service.createReferenceMapFor(analyses, metrics);
      expect(keys(map1).length).toBeGreaterThan(0);

      analyses[0].semanticId = 'metric2';
      const map2 = component.service.createReferenceMapFor(analyses, metrics);
      expect(keys(map2).length).toBe(0);
    });
  });
});
