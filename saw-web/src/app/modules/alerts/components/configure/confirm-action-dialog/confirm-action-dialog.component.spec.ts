import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MaterialModule } from '../../../../../material.module';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { ConfirmActionDialogComponent } from './confirm-action-dialog.component';

class MatDialogRefStub {
  close(param: boolean) {}
}

describe('ConfirmActionDialogComponent', () => {
  let component: ConfirmActionDialogComponent;
  let fixture: ComponentFixture<ConfirmActionDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MaterialModule],
      providers: [
        // workaround: Can't inject MatDialogRef in the unit test?
        {
          provide: MatDialogRef,
          useClass: MatDialogRefStub
        },
        {
          provide: MAT_DIALOG_DATA,
          useValue: {}
        }
      ],
      declarations: [ConfirmActionDialogComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmActionDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should close on NO clicked', () => {
    component.onNoClick(false);
  });

  it('should close on YES clicked', () => {
    component.onYesClick(true);
  });
});
