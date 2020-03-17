import { UChartModule } from './';
import { async, TestBed, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ChartComponent } from './chart.component';

describe('Chart Component', () => {
  let fixture: ComponentFixture<ChartComponent>;
  let component: ChartComponent;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [UChartModule],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChartComponent);
    component = fixture.componentInstance;
  });

  it('should be initialised', () => {
    expect(component).toBeTruthy();
  });

  describe('enableExporting', () => {
    it('should add exporting config', () => {
      const config = {};
      component.enableExporting(config);
      expect(Object.keys(config).includes('exporting')).toBeTruthy();
    });
  });

  describe('saveComparisonConfig', () => {
    it('should cache categories', () => {
      expect(component.comparisonConfig.categories).toBeNull();

      component.chartType = 'comparison';
      component.saveComparisonConfig({
        path: 'xAxis.categories',
        data: ['Abc']
      });

      expect(component.comparisonConfig.categories.length).toEqual(1);
    });

    it('should cache series', () => {
      expect(component.comparisonConfig.series).toBeNull();
      component.chartType = 'comparison';
      component.saveComparisonConfig({
        path: 'series',
        data: [{ name: 'Series 1', data: [] }]
      });

      expect(component.comparisonConfig.series.length).toEqual(1);
    });
  });

  describe('getChartSettingsType', () => {
    it('should return correct chart settings type', () => {
      expect(component.getChartSettingsType('tsSpline')).toEqual('highStock');
      expect(component.getChartSettingsType('bullet')).toEqual('bullet');
      expect(component.getChartSettingsType('column')).toEqual('default');
    });
  });

  describe('onCategoryToggle', () => {
    it('should turn off category', () => {
      let categories = ['Abc'];
      const series = [
        {
          name: 'Series 1',
          data: [{ x: 0, y: 123 }]
        }
      ];
      component.chartType = 'comparison';
      component.comparisonConfig.categories = [{ name: 'Abc', checked: true }];
      component.comparisonConfig.series = series;

      component.chart = {
        xAxis: [
          {
            setCategories: cat => {
              categories = cat;
            }
          }
        ],
        series: [
          {
            setData: data => {
              series[0].data = data;
            }
          }
        ]
      };

      component.onCategoryToggle({ checked: false } as any, 0);
      expect(categories.length).toEqual(0);
      expect(series[0].data.length).toEqual(0);
    });
  });
});
