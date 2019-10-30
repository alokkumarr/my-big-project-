import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { DesignerRegionSelectorComponent } from './designer-region-selector.component';
import { Store } from '@ngxs/store';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from 'src/app/material.module';
import { ReactiveFormsModule } from '@angular/forms';

const StoreStub = {};

describe('Designer Combo Type Selector', () => {
  let fixture: ComponentFixture<DesignerRegionSelectorComponent>;
  let component: DesignerRegionSelectorComponent;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, MaterialModule, NoopAnimationsModule],
      declarations: [DesignerRegionSelectorComponent],
      providers: [{ provide: Store, useValue: StoreStub }]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DesignerRegionSelectorComponent);
    component = fixture.componentInstance;
    component.artifactColumn = {} as any;
    fixture.detectChanges();
  });

  it('should exist', () => {
    expect(component).toBeTruthy();
  });
});
