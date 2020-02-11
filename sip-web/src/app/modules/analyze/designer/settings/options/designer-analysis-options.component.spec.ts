import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { DesignerAnalysisOptionsComponent } from './designer-analysis-options.component';

const column = {
  columnName: 'duumyColumn'
};

describe('Designer Analysis Options Component', () => {
  let fixture: ComponentFixture<DesignerAnalysisOptionsComponent>;
  let component;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [],
      declarations: [DesignerAnalysisOptionsComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(DesignerAnalysisOptionsComponent);
        component = fixture.componentInstance;
      });
  }));

  it('should return column name ', () => {
    const name = component.selectedColsTrackByFn('', column);
    expect(name).toEqual(column.columnName);
  });
});
