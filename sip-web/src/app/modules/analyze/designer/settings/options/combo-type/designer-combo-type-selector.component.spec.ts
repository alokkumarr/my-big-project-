import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { DesignerComboTypeSelectorComponent } from './designer-combo-type-selector.component';
import { Store } from '@ngxs/store';
import { MaterialModule } from 'src/app/material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

const StoreStub = {
  dispatch: () => {}
};

const artifactColumnStub = {
  dataField: "AVAILABLE_MB",
  area: "y",
  alias: "",
  columnName: "AVAILABLE_MB",
  displayName: "Available MB",
  type: "double",
  aggregate: "sum",
  displayType: "column",
  areaIndex: 0,
  colorSetFromPicker: false,
  name: "AVAILABLE_MB",
  seriesColor: "#00c9e8",
  table: "mct_test"
};

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
    component.artifactColumn = artifactColumnStub as any;
    fixture.detectChanges();
  });

  it('should exist', () => {
    expect(component).toBeTruthy();
  });

  it('should update combotype in artifactcolumn', () => {
    const comboType = 'area';
    component.onComboTypeChange(comboType);
    expect((<any>component.artifactColumn).displayType).toEqual(comboType);
  });
});
