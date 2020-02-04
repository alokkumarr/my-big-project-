import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { DesignerAnalysisOptionsComponent } from './designer-analysis-options.component';
// import * as forEach from 'lodash/forEach';

const sipQuery = {
  artifacts: [
    {
      artifactsName: 'sales',
      fields: [
        {
          area: 'x',
          columnName: 'date',
          alias: '',
          dataField: 'date',
          displayName: 'Date',
          groupInterval: null,
          name: 'date',
          table: 'sales',
          type: 'date'
        },
        {
          area: 'y',
          columnName: 'double',
          alias: '',
          dataField: 'double',
          displayName: 'Double',
          groupInterval: null,
          name: 'double',
          table: 'sales',
          type: 'double'
        }
      ]
    }
  ],
  filters: [],
  sorts: [],
  joins: [],
  store: {
    dataStore: null,
    storageType: null
  },
  semanticId: 'workbench::sample-elasticsearch',
  booleanCriteria: 'AND'
};

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

  it('should set series color to each data options. ', () => {
    component.setArtifacts = sipQuery;
    expect(component.sipQuery).toEqual(sipQuery);
    component.selectedColumns = sipQuery.artifacts[0].fields;
    component.setSeriesColorToEachDataOption();
    expect(component.selectedColumns).toBeTruthy();
  });
});
