import { TestBed, ComponentFixture } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { DesignerService } from '../../designer.service';
import { SingleTableDesignerLayoutComponent } from './single-table-designer-layout.component';
import { IsAnalysisTypePipe } from '../../../../../common/pipes/is-analysis-type.pipe';

const dummyEvent = {
  subject: 'seriesColorChange',
  data: '#123456'
};

describe('Single Table Layout Designer', () => {
  let fixture: ComponentFixture<SingleTableDesignerLayoutComponent>;
  let component: SingleTableDesignerLayoutComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      declarations: [SingleTableDesignerLayoutComponent, IsAnalysisTypePipe],
      providers: [
        {
          provide: DesignerService,
          useValue: {}
        }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SingleTableDesignerLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should exist', () => {
    expect(component).toBeTruthy();
  });

  it('should change data option when series color is changed', () => {
    component.changeDataOptions(dummyEvent);
    component.change.subscribe(result => {
      expect(result).toEqual(dummyEvent);
    });
  });
});
