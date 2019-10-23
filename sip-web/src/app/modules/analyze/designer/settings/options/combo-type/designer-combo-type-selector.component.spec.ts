import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { DesignerComboTypeSelectorComponent } from './designer-combo-type-selector.component';
import { Store } from '@ngxs/store';
import { MaterialModule } from 'src/app/material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

const StoreStub = {};

describe('Designer Combo Type Selector', () => {
  let fixture: ComponentFixture<DesignerComboTypeSelectorComponent>;
  let component: DesignerComboTypeSelectorComponent;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MaterialModule, NoopAnimationsModule],
      declarations: [DesignerComboTypeSelectorComponent],
      providers: [{ provide: Store, useValue: StoreStub }]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DesignerComboTypeSelectorComponent);
    component = fixture.componentInstance;
  });

  it('should exist', () => {
    expect(component).toBeTruthy();
  });
});
