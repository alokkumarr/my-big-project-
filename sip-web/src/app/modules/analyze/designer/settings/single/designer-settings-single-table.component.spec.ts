import { TestBed, ComponentFixture } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { DesignerService } from '../../designer.service';
import { DesignerSettingsSingleTableComponent } from './designer-settings-single-table.component';
import { DndPubsubService } from '../../../../../common/services';
import { Store } from '@ngxs/store';
import { MatDialog } from '@angular/material';
import { MaterialModule } from '../../../../../material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('Designer Single Table Settings', () => {
  let fixture: ComponentFixture<DesignerSettingsSingleTableComponent>;
  let component: DesignerSettingsSingleTableComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MaterialModule, NoopAnimationsModule],
      declarations: [DesignerSettingsSingleTableComponent],
      providers: [
        {
          provide: DesignerService,
          useValue: {}
        },
        {
          provide: DndPubsubService,
          useValue: {}
        },
        {
          provide: Store,
          useValue: {}
        },
        {
          provide: MatDialog,
          useValue: {}
        }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DesignerSettingsSingleTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should exist', () => {
    expect(component).toBeTruthy();
  });
});
