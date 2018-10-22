import { nonEmpty } from "./non-empty.validator";
import {
  FormGroup,
  FormControl,
  FormBuilder,
  ReactiveFormsModule
} from "@angular/forms";

import { TestBed, inject } from "@angular/core/testing";

describe("Non Empty Validator", () => {
  let form: FormGroup;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [],
      providers: []
    });
    const fb: FormBuilder = TestBed.get(FormBuilder);
    form = fb.group({
      field1: ["", nonEmpty()]
    });
  });

  it("should be invalid if control empty", () => {
    const expectedObj = { nonEmpty: { value: "" } };
    form.get("field1").setValue("");
    expect(form.invalid).toBeTruthy();
    expect(form.get("field1").errors).toEqual(
      jasmine.objectContaining(expectedObj)
    );
  });

  it("should be valid if control empty", () => {
    form.get("field1").setValue("      a     b");

    expect(form.invalid).toBeFalsy();
  });

  it("should be invalid if control only has spaces", () => {
    const expectedObj = { nonEmpty: { value: "         " } };
    form.get("field1").setValue("         ");
    expect(form.invalid).toBeTruthy();
    expect(form.get("field1").errors).toEqual(
      jasmine.objectContaining(expectedObj)
    );
  });
});
