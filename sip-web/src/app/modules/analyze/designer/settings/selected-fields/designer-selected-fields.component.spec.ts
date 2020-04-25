import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { DesignerSelectedFieldsComponent } from './designer-selected-fields.component';
import { MaterialModule } from 'src/app/material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { DndPubsubService } from 'src/app/common/services';
import { Store } from '@ngxs/store';
import { AnalyzeService } from '../../../services/analyze.service';
import { DskFiltersService }  from '../../../../../common/services/dsk-filters.service';
import { of } from 'rxjs';

const DndPubsubServiceStub = {
  subscribe: () => ({
    unsubscribe: () => {}
  }),
  emit: () => {}
};

const StoreStub = {
  dispatch: () => {},
  select: () => of({ artifacts: [] })
};

const AnalyzeServiceStub = {
  calcNameMap: () => ({})
};

describe('Designer Component', () => {
  let component: DesignerSelectedFieldsComponent;
  let fixture: ComponentFixture<DesignerSelectedFieldsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: DndPubsubService,
          useValue: DndPubsubServiceStub
        },
        {
          provide: Store,
          useValue: StoreStub
        },
        {
          provide: AnalyzeService,
          useValue: AnalyzeServiceStub
        },
        { provide: DskFiltersService, useValue: {} }
      ],
      imports: [MaterialModule, NoopAnimationsModule],
      declarations: [DesignerSelectedFieldsComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DesignerSelectedFieldsComponent);
    component = fixture.componentInstance;
    component.filters = [];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeDefined();
  });

  it('should remove column from group', () => {
    const column = { columnName: 'abc', dataField: 'abc' };
    const store = TestBed.get(Store);
    const spy = spyOn(store, 'dispatch').and.returnValue(null);
    component.groupAdapters = [];
    component.removeFromGroup(
      column as any,
      { artifactColumns: [column] } as any
    );
    expect(spy).toHaveBeenCalled();
  });

  it('should notify of drag start', () => {
    const spy = spyOn(TestBed.get(DndPubsubService), 'emit').and.returnValue(
      {}
    );
    component.dragStarted();
    expect(spy).toHaveBeenCalledWith('dragStart');
  });
});
