import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { MaterialModule } from 'src/app/material.module';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { RouterTestingModule } from '@angular/router/testing';

import { DesignerFilterDialogComponent } from './designer-filter-dialog.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('DesignerFilterRowComponent', () => {
  let component: DesignerFilterDialogComponent;
  let fixture: ComponentFixture<DesignerFilterDialogComponent>;

  const mockDialogRef = {
    close: jasmine.createSpy('close')
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MaterialModule, NoopAnimationsModule, RouterTestingModule],
      declarations: [DesignerFilterDialogComponent],
      providers: [
        {
          provide: MatDialogRef,
          useValue: mockDialogRef
        },
        {
          provide: MAT_DIALOG_DATA,
          useValue: {
            query: 'select integer, string from sales where integer = ? and string = ?;'
          }
        }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DesignerFilterDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return aggregated filters', () => {
    component.groupedFilters = {
      table: [{ isAggregationFilter: true }, { isAggregationFilter: false }]
    };
    expect(component.aggregatedFiltersFor('table').length).toEqual(1);
  });

  it('should remove aggregated filters correctly', () => {
    component.groupedFilters = {
      table: [
        { isAggregationFilter: false, id: 1 },
        { isAggregationFilter: true, id: 1 },
        { isAggregationFilter: true, id: 2 }
      ]
    };

    /* Delete the first aggregation filter */
    component.removeFilter(0, 'table', true);

    expect(component.groupedFilters.table[0].isAggregationFilter).toEqual(
      false
    );
    expect(component.groupedFilters.table[1].id).toEqual(2);
  });

  it('should add classes to query string so keywords and run time markers can be identified.', () => {
    const sqlQueryWithClasses = "<span class='sql-keyword'>select&nbsp;</span><span class='other'>integer,&nbsp;</span><span class='other'>string&nbsp;</span><span class='sql-keyword'>from&nbsp;</span><span class='other'>sales&nbsp;</span><span class='sql-keyword'>where&nbsp;</span><span class='other'>integer&nbsp;</span><span class='other'>=&nbsp;</span><span class='runtime-indicator'>?&nbsp;</span><span class='other'>and&nbsp;</span><span class='other'>string&nbsp;</span><span class='other'>=&nbsp;</span><span class='other'>?;&nbsp;</span>"
    expect(component.loadQueryWithClasses()).toEqual(sqlQueryWithClasses);
  });
});
