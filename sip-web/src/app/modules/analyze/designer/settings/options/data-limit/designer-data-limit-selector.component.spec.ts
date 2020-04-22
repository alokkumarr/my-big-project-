import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { DesignerDataLimitSelectorComponent } from './designer-data-limit-selector.component';
import { Store } from '@ngxs/store';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from 'src/app/material.module';
import { of } from 'rxjs';
import { BreakpointObserver } from '@angular/cdk/layout';

const StoreStub = {
  dispatch: () => {}
};
const BreakpointObserverStub = {
  observe: () => of({ matches: [] })
};

describe('Designer Combo Type Selector', () => {
  let fixture: ComponentFixture<DesignerDataLimitSelectorComponent>;
  let component: DesignerDataLimitSelectorComponent;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MaterialModule, NoopAnimationsModule],
      declarations: [DesignerDataLimitSelectorComponent],
      providers: [
        { provide: Store, useValue: StoreStub },
        { provide: BreakpointObserver, useValue: BreakpointObserverStub }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DesignerDataLimitSelectorComponent);
    component = fixture.componentInstance;
    component.artifactColumn = { limitType: 'top', limitValue: 10 } as any;
    fixture.detectChanges();
  });

  it('should exist', () => {
    expect(component).toBeTruthy();
  });

  it('should reset value to zero if number is negative', () => {
    component.resetInvalidValueIfNeeded(-1);
    expect(component.limitValue).toEqual(0);
  });

  it('should reset value to 99 if number is more than 99', () => {
    component.resetInvalidValueIfNeeded(100);
    expect(component.limitValue).toEqual(99);
  });

  it('should remove the limit value and type when reset', () => {
    component.limitType = 'top';
    component.limitValue = 2;
    component.onToggleClicked('top');
    expect(component.limitType).toBeNull();
    expect(component.limitValue).toBeNull();
  });
});
