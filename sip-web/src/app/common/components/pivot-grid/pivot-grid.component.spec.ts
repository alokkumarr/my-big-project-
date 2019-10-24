import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA, Component, Input } from '@angular/core';
import { PivotGridComponent } from './pivot-grid.component';

@Component({
  // tslint:disable-next-line
  selector: 'pivot-grid',
  template: 'PivotGridComponent'
})
class PivotStubComponent {
  @Input() public updater;
  @Input() public mode;
  @Input() public showFieldDetails;
  @Input() public sorts;
  @Input() public artifactColumns;
  @Input() public data;
}

describe('Pivot grid Component', () => {
  let component: PivotGridComponent;
  let fixture: ComponentFixture<PivotGridComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [],
      declarations: [PivotGridComponent, PivotStubComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PivotGridComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeDefined();
  });

  it('should sort pivot grid data', () => {
    const format = component.getFormattedDataValue(
      '2017-01-01',
      'day',
      'YYYY-MM-DD'
    );
    expect(format).toEqual('2017-01-01');
  });

  it('should apply aggregates to display names for data fields', () => {
    const column = {
      type: 'double',
      aggregate: 'sum',
      area: 'data',
      dataField: 'sum@@double',
      displayName: 'Double',
      columnName: 'double'
    };
    const result = component.artifactColumn2PivotField()([column])[0];
    expect(result.caption).toEqual('SUM(Double)');
  });
});
