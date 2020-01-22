import { TestBed, async } from '@angular/core/testing';
import { FilterChipsComponent } from './filter-chips.component';
import { AnalyzeService } from '../../../services/analyze.service';
import { NO_ERRORS_SCHEMA, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('Filter Chips Component', () => {
  let component: FilterChipsComponent;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
      declarations: [FilterChipsComponent],
      providers: [{ provide: AnalyzeService, useValue: {} }]
    }).compileComponents();
  }));

  beforeEach(() => {
    component = TestBed.createComponent(FilterChipsComponent).componentInstance;
  });

  it('should be initialised', () => {
    expect(component).not.toBeNull();
  });

  describe('getDisplayName', () => {
    it('should contain aggregate and correct text for aggregated filters', () => {
      component.nameMap = { table: { ABC: 'Abc' } };
      const text = component.getDisplayName({
        isAggregationFilter: true,
        isRuntimeFilter: false,
        isOptional: false,
        columnName: 'ABC',
        aggregate: 'count',
        type: 'string',
        tableName: 'table',
        model: {
          operator: 'EQ',
          value: 123
        }
      });

      expect(text).toEqual('CNT(Abc): Equal to 123');
    });
  });
});
