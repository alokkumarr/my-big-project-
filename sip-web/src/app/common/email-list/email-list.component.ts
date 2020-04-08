import {
  Component,
  Input,
  Output,
  EventEmitter,
  forwardRef,
  ViewChild,
  ElementRef
} from '@angular/core';
import { ENTER, COMMA } from '@angular/cdk/keycodes';
import {
  FormControl,
  Validators,
  ControlValueAccessor,
  NG_VALUE_ACCESSOR
} from '@angular/forms';
import * as reject from 'lodash/reject';
import * as invoke from 'lodash/invoke';
import { EMAIL_REGEX } from '../consts';
import { MatAutocompleteTrigger } from '@angular/material';
import { Observable, merge, of } from 'rxjs';
import { map } from 'rxjs/operators';

const SEMICOLON = 186;

@Component({
  selector: 'email-list',
  templateUrl: 'email-list.component.html',
  styleUrls: ['email-list.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => EmailListComponent),
      multi: true
    }
  ]
})
export class EmailListComponent implements ControlValueAccessor {
  @Output() emailsChange = new EventEmitter<string[]>();
  @Input() emails: string[];
  @Input() autoCompleteSuggestions = [];
  @Input() allowCustomInput = true;

  filteredSuggestions: Observable<string[]>;

  @ViewChild('emailInput', { static: false }) emailInput: ElementRef<
    HTMLInputElement
  >;
  @ViewChild(MatAutocompleteTrigger, { static: false })
  autoCompleteTrigger: MatAutocompleteTrigger;

  public propagateChange: (emails: string[]) => void;
  public emailField = new FormControl('', Validators.pattern(EMAIL_REGEX));
  separatorKeys = [ENTER, COMMA, SEMICOLON];

  constructor() {
    this.filteredSuggestions = merge(
      of(this.filter(null)),
      this.emailField.valueChanges.pipe(
        map((email: string) => this.filter(email))
      )
    );
  }

  private filter(email: string) {
    if (!email) {
      return this.autoCompleteSuggestions.filter(
        suggestion => !this.emails.includes(suggestion)
      );
    }
    const filterValue = email.toLowerCase();

    return this.autoCompleteSuggestions.filter(
      suggestion =>
        suggestion.toLowerCase().indexOf(filterValue) === 0 &&
        !this.emails.includes(suggestion)
    );
  }

  writeValue(emails: string[]) {
    this.emails = emails;
  }

  registerOnChange(fn) {
    this.propagateChange = fn;
  }

  registerOnTouched() {}

  resetInput() {
    if (this.emailInput) {
      this.emailInput.nativeElement.value = '';
    }
    this.emailField.setValue('');
  }

  addEmail(email) {
    const trimmed = (email || '').trim();
    // Reset the input value
    this.resetInput();
    const newEmails = [...this.emails, trimmed];
    invoke(this, 'propagateChange', newEmails);
    this.emailsChange.emit(newEmails);
  }

  addEmailIfCorrect(email) {
    const isInputNonEmpty = Boolean(this.emailField.value);
    const isValidEmail = !this.emailField.hasError('pattern');
    if (isInputNonEmpty && isValidEmail) {
      this.addEmail(email);
    }
  }

  openAutocomplete() {
    if (this.autoCompleteSuggestions && this.autoCompleteSuggestions.length) {
      this.autoCompleteTrigger.openPanel();
    }
  }

  onTextEntered() {
    if (!this.allowCustomInput) {
      this.resetInput();
    }
  }

  removeEmail(targetIndex) {
    if (targetIndex >= 0) {
      const newEmails = reject(
        this.emails,
        (_, index) => index === targetIndex
      );
      invoke(this, 'propagateChange', newEmails);
      this.emailsChange.emit(newEmails);
    }
  }

  trackByValue(index, value) {
    return value;
  }
}
