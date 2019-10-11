import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { MaterialModule } from 'src/app/material.module';

import { DesignerSortComponent } from './designer-sort.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { DndModule } from 'src/app/common/dnd';

describe('DesignerFilterRowComponent', () => {
  let component: DesignerSortComponent;
  let fixture: ComponentFixture<DesignerSortComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MaterialModule, NoopAnimationsModule, DndModule],
      declarations: [DesignerSortComponent],
      providers: [],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DesignerSortComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
