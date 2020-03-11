import { TestBed, ComponentFixture } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { Store } from '@ngxs/store';
import { MaterialModule } from 'src/app/material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { DesignerDataOptionFieldComponent } from './designer-data-option-field.component';
import { IsAnalysisTypePipe } from 'src/app/common/pipes/is-analysis-type.pipe';
import * as take from 'lodash/take';
import * as cloneDeep from 'lodash/cloneDeep';
import * as set from 'lodash/set';
import { CHART_COLORS } from 'src/app/common/consts';

const seriesColorChange = {
  subject: 'seriesColorChange',
  data: { artifact: [] }
};

const colorEvent = {
  data: '#123456'
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

  it('should set the predefined colors', () => {
    const chartColor = cloneDeep(CHART_COLORS);
    const presetColors = take(chartColor, 10);
    set(component.colorPickerConfig, 'presetColors', presetColors);
    expect(component.colorPickerConfig['presetColors']).toBeTruthy();
  });

  it('should set the custom style if required', () => {
    const isRequired = true;
    set(component.colorPickerConfig, 'iscustomStyleNeeded', isRequired);
    expect(component.colorPickerConfig['iscustomStyleNeeded']).toBeTruthy();
  });

  it('should set series color in artifact column', () => {
    component.selectedColor(colorEvent);
    expect(component.artifactColumn.seriesColor).toEqual(colorEvent.data);
    component.change.subscribe(result => {
      expect(result).toEqual(seriesColorChange);
    });

    component.artifactColumn.seriesColor = 'white';
    component.selectedColor({});
    expect(component.artifactColumn.seriesColor).toEqual('white');
  });
});
