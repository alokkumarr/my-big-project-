import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material';
import { DesignerChartOptionsComponent } from './designer-chart-options.component';
import { ChartService } from '../../../../../../common/services/chart.service';

class ChartStubService {}

describe('Designer Map Chart Component', () => {
  let fixture: ComponentFixture<DesignerChartOptionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MatButtonModule, FormsModule],
      providers: [{ provide: ChartService, useValue: ChartStubService }],
      declarations: [DesignerChartOptionsComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(DesignerChartOptionsComponent);
      });
  }));

  it('should not show invert chart option for pie chart', () => {
    const component = fixture.componentInstance;
    component.chartType = 'pie';
    expect(component.showInversion).not.toBeTruthy();
  });

  it('should show label options for pie chart', () => {
    const component = fixture.componentInstance;
    component.chartType = 'pie';
    expect(component.showLabelOpts).not.toBeTruthy();
  });

  it('should not show label options for pie chart', () => {
    const component = fixture.componentInstance;
    component.chartType = 'pie';
    expect(component.showLegendOpts).not.toBeTruthy();
  });
});
