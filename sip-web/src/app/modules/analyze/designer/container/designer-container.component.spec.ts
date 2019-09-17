import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA, Component, Input } from '@angular/core';
import { DesignerContainerComponent } from './designer-container.component';
import { DesignerService } from '../designer.service';
import { AnalyzeDialogService } from '../../services/analyze-dialog.service';
import { ChartService } from '../../../../common/services/chart.service';
import { AnalyzeService } from '../../services/analyze.service';
import { JwtService } from '../../../../common/services';
import { Store } from '@ngxs/store';

@Component({
  // tslint:disable-next-line
  selector: 'designer-container',
  template: 'DesignerContainer'
})

class DesignerStubComponent {
  @Input() public analysisStarter;
  @Input() public analysis;
  @Input() public designerMode;
}

describe('Designer Component', () => {
  let component: DesignerContainerComponent;
  let fixture: ComponentFixture<DesignerContainerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: DesignerService, useValue: {} },
        { provide: AnalyzeDialogService, useValue: {} },
        { provide: ChartService, useValue: {} },
        { provide: AnalyzeService, useValue: {} },
        { provide: JwtService, useValue: {} },
        { provide: Store, useValue: { dispatch: () => {} } }
      ],
      declarations: [DesignerContainerComponent, DesignerStubComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DesignerContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeDefined();
  });

  it('should construct filters for DSL format', () => {
    const filters = [
      {
        type: 'date',
        artifactsName: 'SALES',
        isOptional: false,
        columnName: 'date',
        isRuntimeFilter: false,
        isGlobalFilter: false,
        model: {
          operator: 'BTW',
          value: '01-01-2017',
          otherValue: '01-31-2017'
        }
      }
    ];

    const output = [
      {
        type: 'date',
        artifactsName: 'SALES',
        isOptional: false,
        columnName: 'date',
        isRuntimeFilter: false,
        isGlobalFilter: false,
        model: {
          operator: 'BTW',
          value: '01-01-2017',
          otherValue: '01-31-2017',
          gte: '2017-01-01',
          lte: '2017-01-31',
          preset: 'NA'
        }
      }
    ];
    const DSLFilters = component.generateDSLDateFilters(filters);
    expect(DSLFilters).toEqual(output);
  });
});
