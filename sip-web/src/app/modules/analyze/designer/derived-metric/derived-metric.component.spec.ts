import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DerivedMetricComponent } from './derived-metric.component';

describe('DerivedMetricComponent', () => {
  let component: DerivedMetricComponent;
  let fixture: ComponentFixture<DerivedMetricComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DerivedMetricComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DerivedMetricComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
