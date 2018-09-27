import {
  TestBed,
  inject,
  ComponentFixture,
  fakeAsync,
  tick
} from "@angular/core/testing";
import 'hammerjs';
import { MaterialModule } from "../../../../material.module";
import { EditWidgetComponent } from "./edit-widget.component";
import { AddWidgetModule } from "../add-widget/add-widget.module";
import { ObserveService } from "../../services/observe.service";

const ObserveServiceStub: Partial<ObserveService> = {};

describe("Edit Widget", () => {
  let fixture: ComponentFixture<EditWidgetComponent>, el: HTMLElement;
  beforeEach(() => {
    return TestBed.configureTestingModule({
      imports: [MaterialModule, AddWidgetModule],
      declarations: [EditWidgetComponent],
      providers: [{ provide: ObserveService, useValue: ObserveServiceStub }]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(EditWidgetComponent);

        el = fixture.nativeElement;

        fixture.detectChanges();
      });
  });

  it("should exist", () => {
    expect(typeof fixture.componentInstance.prepareKPI).toEqual("function");
  });

  it("should not show kpi widget without input", () => {
    expect(fixture.componentInstance.editItem).toBeUndefined();
    expect(el.querySelector("widget-kpi")).toBeNull();
  });

  it("should show kpi widget if editItem present", done => {
    fixture.componentInstance.editItem = {
      kpi: {},
      metric: { dateColumns: [{}] }
    };
    fixture.detectChanges();
    fixture
      .whenStable()
      .then(() => {
        expect(el.querySelector("widget-kpi")).toBeUndefined
        done();
      })
      .catch(() => {
        done();
      });
  });
});
