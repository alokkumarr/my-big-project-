import {
  ReactiveFormsModule,
  FormControl,
  FormGroup,
  FormsModule
} from '@angular/forms';
import { TestBed, ComponentFixture } from '@angular/core/testing';
import 'hammerjs';
import { MaterialModule } from '../../../../../material.module';
import { WidgetKPIComponent } from './widget-kpi.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('KPI Form Widget', () => {
  let fixture: ComponentFixture<WidgetKPIComponent>;
  beforeEach(() => {
    return TestBed.configureTestingModule({
      imports: [
        NoopAnimationsModule,
        MaterialModule,
        ReactiveFormsModule,
        FormsModule
      ],
      declarations: [WidgetKPIComponent]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(WidgetKPIComponent);

        fixture.detectChanges();
      });
  });

  it('should exist', () => {
    expect(typeof fixture.componentInstance.applyKPI).toEqual('function');
  });

  it('should disable secondary aggregation if it is selected in primary', done => {
    const pAggr = fixture.componentInstance.kpiForm.get(
      'primAggregate'
    ) as FormControl;
    pAggr.setValue('avg');

    fixture.detectChanges();
    fixture
      .whenStable()
      .then(() => {
        const sAggrForm = fixture.componentInstance.kpiForm.get(
          'secAggregates'
        ) as FormGroup;
        const avgControl = sAggrForm.get('avg') as FormControl;

        expect(avgControl.disabled).toBe(true);
        done();
      })
      .catch(() => {
        done();
      });
  });

  it('should uncheck secondary aggregation if it is selected in primary', done => {
    const sAggrForm = fixture.componentInstance.kpiForm.get(
      'secAggregates'
    ) as FormGroup;
    const avgControl = sAggrForm.get('avg') as FormControl;
    expect(avgControl.disabled).toBe(false);

    avgControl.setValue(true);

    const pAggr = fixture.componentInstance.kpiForm.get(
      'primAggregate'
    ) as FormControl;
    pAggr.setValue('avg');

    fixture.detectChanges();
    fixture
      .whenStable()
      .then(() => {
        expect(avgControl.disabled).toBe(true);
        expect(avgControl.value).toBe(false);
        done();
      })
      .catch(() => {
        done();
      });
  });
});
