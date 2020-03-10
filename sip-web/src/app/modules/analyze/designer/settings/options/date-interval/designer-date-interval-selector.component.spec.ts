import { async, TestBed, ComponentFixture } from '@angular/core/testing';
import { DesignerDateIntervalSelectorComponent } from '../..';
import { Store } from '@ngxs/store';
import { NO_ERRORS_SCHEMA, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

const storeStub = {
  selectSnapshot: () => 'combo'
};

describe('Date Interval Selector', () => {
  let fixture: ComponentFixture<DesignerDateIntervalSelectorComponent>;
  let component: DesignerDateIntervalSelectorComponent;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DesignerDateIntervalSelectorComponent],
      providers: [{ provide: Store, useValue: storeStub }],
      schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DesignerDateIntervalSelectorComponent);
    component = fixture.componentInstance;
  });

  it('should be initialised', () => {
    expect(component).toBeTruthy();
  });

  it('should be initialised with correct date intervals for comparison charts', async(() => {
    spyOn(TestBed.get(Store), 'selectSnapshot').and.returnValue('comparison');
    const intervalComponentFixture = TestBed.createComponent(
      DesignerDateIntervalSelectorComponent
    );
    intervalComponentFixture.componentInstance.artifactColumn = {
      groupInterval: 'month'
    } as any;
    intervalComponentFixture.detectChanges();
    expect(
      intervalComponentFixture.componentInstance.DATE_INTERVALS.length
    ).toEqual(2);
  }));

  it('should be initialised with correct date intervals for all other charts', async(() => {
    spyOn(TestBed.get(Store), 'selectSnapshot').and.returnValue('combo');
    const intervalComponentFixture = TestBed.createComponent(
      DesignerDateIntervalSelectorComponent
    );
    intervalComponentFixture.componentInstance.artifactColumn = {
      groupInterval: 'month'
    } as any;
    intervalComponentFixture.detectChanges();
    expect(
      intervalComponentFixture.componentInstance.DATE_INTERVALS.length
    ).toBeGreaterThan(2);
  }));
});
