import { TestBed, ComponentFixture } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { Store } from '@ngxs/store';
import { MaterialModule } from 'src/app/material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import {
  DesignerSettingsMultiTableComponent,
  refactorJoins
} from './designer-settings-multi-table.component';

const leftArtifactsName = 'left-tableName';
const rightArtifactsName = 'right-tableName';
const leftColumnName = 'left-colName';
const rightColumnName = 'right-colName';
const joinType = 'join type';

const oldJoins = [
  {
    type: null,
    join: joinType,
    criteria: [
      {
        joinCondition: {
          left: {
            artifactsName: leftArtifactsName,
            columnName: leftColumnName
          },
          right: {
            artifactsName: rightArtifactsName,
            columnName: rightColumnName
          }
        }
      }
    ]
  }
];

const newJoins = [
  {
    type: joinType,
    criteria: [
      {
        tableName: leftArtifactsName,
        columnName: leftColumnName,
        side: 'left'
      },
      {
        tableName: rightArtifactsName,
        columnName: rightColumnName,
        side: 'right'
      }
    ]
  }
];

describe('Designer Settings Multi Table', () => {
  let fixture: ComponentFixture<DesignerSettingsMultiTableComponent>;
  let component: DesignerSettingsMultiTableComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MaterialModule, NoopAnimationsModule],
      declarations: [DesignerSettingsMultiTableComponent],
      providers: [
        {
          provide: Store,
          useValue: {}
        }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DesignerSettingsMultiTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should exist', () => {
    expect(component).toBeTruthy();
  });

  it('should refactor old joins to new joins', () => {
    const joins = refactorJoins(oldJoins, []);
    expect(joins).toEqual(newJoins);
  });
});
