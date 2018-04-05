import { expect } from 'chai';

import { configureTests } from '../../../../../../../test/javascript/helpers/configureTests';
import {
  TestBed,
  inject,
  ComponentFixture,
  fakeAsync,
  tick
} from '@angular/core/testing';

import { MaterialModule } from '../../../../material.module';
import { EditWidgetComponent } from './edit-widget.component';
import { AddWidgetModule } from '../add-widget/add-widget.module';
import { ObserveService } from '../../services/observe.service';

const ObserveServiceStub: Partial<ObserveService> = {};

configureTests();

describe('Edit Widget', () => {
  let fixture: ComponentFixture<EditWidgetComponent>, el: HTMLElement;
  beforeEach(
    fakeAsync(() => {
      TestBed.configureTestingModule({
        imports: [MaterialModule, AddWidgetModule],
        declarations: [EditWidgetComponent],
        providers: [{ provide: ObserveService, useValue: ObserveServiceStub }]
      }).compileComponents();

      fixture = TestBed.createComponent(EditWidgetComponent);

      el = fixture.nativeElement;

      fixture.detectChanges();
      tick();
    })
  );

  it(
    'should exist',
    fakeAsync(() => {
      tick();
      expect(typeof fixture.componentInstance.prepareKPI).to.equal('function');
    })
  );

  it(
    'should not show kpi widget without input',
    fakeAsync(() => {
      tick();
      expect(fixture.componentInstance.editItem).to.be.undefined;

      expect(el.querySelector('widget-kpi')).to.be.null;
    })
  );

  it(
    'should show kpi widget if editItem present',
    fakeAsync(() => {
      fixture.componentInstance.editItem = {
        kpi: {},
        column: {},
        metric: { dateColumns: [{}] }
      };
      fixture.detectChanges();
      tick();
      expect(el.querySelector('widget-kpi')).to.not.be.null;
    })
  );
});
