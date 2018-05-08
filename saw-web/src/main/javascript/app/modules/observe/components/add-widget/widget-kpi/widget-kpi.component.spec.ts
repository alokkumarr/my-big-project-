import { expect } from 'chai';

import { configureTests } from '../../../../../../../../test/javascript/helpers/configureTests';
import { ReactiveFormsModule, FormControl, FormGroup } from '@angular/forms';
import { TestBed, ComponentFixture } from '@angular/core/testing';

import { MaterialModule } from '../../../../../material.module';
import { WidgetKPIComponent } from './widget-kpi.component';

configureTests();

describe('KPI Form Widget', () => {
  let fixture: ComponentFixture<WidgetKPIComponent>, el: HTMLElement;
  beforeEach(done => {
    TestBed.configureTestingModule({
      imports: [MaterialModule, ReactiveFormsModule],
      declarations: [WidgetKPIComponent]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(WidgetKPIComponent);

        el = fixture.nativeElement;

        fixture.detectChanges();
        done();
      });
  });

  it('should exist', () => {
    expect(typeof fixture.componentInstance.applyKPI).to.equal('function');
  });

  it('should disable secondary aggregation if it is selected in primary', done => {
    const pAggr = fixture.componentInstance.kpiForm.get(
      'primAggregate'
    ) as FormControl;
    pAggr.setValue('avg');

    fixture.detectChanges();
    fixture.whenStable().then(() => {
      const sAggrForm = fixture.componentInstance.kpiForm.get(
        'secAggregates'
      ) as FormGroup;
      const avgControl = sAggrForm.get('avg') as FormControl;

      expect(avgControl.disabled).to.be.true;
      done();
    });
  });

  it('should uncheck secondary aggregation if it is selected in primary', done => {
    const sAggrForm = fixture.componentInstance.kpiForm.get(
      'secAggregates'
    ) as FormGroup;
    const avgControl = sAggrForm.get('avg') as FormControl;
    expect(avgControl.disabled).to.be.false;

    avgControl.setValue(true);

    const pAggr = fixture.componentInstance.kpiForm.get(
      'primAggregate'
    ) as FormControl;
    pAggr.setValue('avg');

    fixture.detectChanges();
    fixture.whenStable().then(() => {
      expect(avgControl.disabled).to.be.true;
      expect(avgControl.value).to.be.false;
      done();
    });
  });
});
