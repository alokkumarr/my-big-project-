import { TestBed, ComponentFixture } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { Store } from '@ngxs/store';
import { MaterialModule } from 'src/app/material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { DesignerDataOptionFieldComponent } from './designer-data-option-field.component';
import { IsAnalysisTypePipe } from 'src/app/common/pipes/is-analysis-type.pipe';

const seriesColorChange = {
  subject: 'seriesColorChange',
  data: { artifact: [] }
};
describe('Designer Data Options', () => {
  let fixture: ComponentFixture<DesignerDataOptionFieldComponent>;
  let component: DesignerDataOptionFieldComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MaterialModule, NoopAnimationsModule],
      declarations: [DesignerDataOptionFieldComponent, IsAnalysisTypePipe],
      providers: [
        {
          provide: Store,
          useValue: { dispatch: () => {} }
        }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DesignerDataOptionFieldComponent);
    component = fixture.componentInstance;
    component.artifactColumn = {} as any;
    component.analysisSubtype = 'column';
    fixture.detectChanges();
  });

  it('should exist', () => {
    expect(component).toBeTruthy();
  });

  it('should return percent by row support', () => {
    expect(component.checkChartType()).toEqual(true);
  });

  it('should effect state change on aggregate change', () => {
    const store = TestBed.get(Store);
    const spy = spyOn(store, 'dispatch').and.returnValue(null);
    component.onAggregateChange('sum');
    expect(spy).toHaveBeenCalled();
  });

  it('should generate series color event', () => {
    component.change.subscribe(result => {
      expect(result).toEqual(seriesColorChange);
    });
  });
});
