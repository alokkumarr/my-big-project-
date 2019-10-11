import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { JsPlumbCanvasComponent } from '.';
import { NgxsModule, Store } from '@ngxs/store';
import { DesignerState } from 'src/app/modules/analyze/designer/state/designer.state';
import { DesignerService } from 'src/app/modules/analyze/designer/designer.module';
import { AnalyzeService } from 'src/app/modules/analyze/services/analyze.service';

describe('JS Plumb Canvas Component', () => {
  let fixture: ComponentFixture<JsPlumbCanvasComponent>;
  let component: JsPlumbCanvasComponent;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [JsPlumbCanvasComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [NgxsModule.forRoot([DesignerState], { developmentMode: true })],
      providers: [
        { provide: DesignerService, useValue: {} },
        { provide: AnalyzeService, useValue: {} }
      ]
    })
      .compileComponents()
      .then(() => {
        const store: Store = TestBed.get(Store);
        store.reset({ designerState: { analysis: { artifacts: [] } } });

        fixture = TestBed.createComponent(JsPlumbCanvasComponent);
        component = fixture.componentInstance;
        component.artifacts = [];
        fixture.detectChanges();
      });
  }));

  it('exist', () => {
    expect(component).toBeTruthy();
  });
});
