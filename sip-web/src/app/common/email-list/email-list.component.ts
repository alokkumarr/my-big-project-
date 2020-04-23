import {
  Component,
  Input,
  Output,
  EventEmitter,
  forwardRef,
  ViewChild,
  ElementRef,
  OnDestroy,
  OnInit
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
import { takeUntil } from 'rxjs/operators';
import { ReplaySubject, Subject } from 'rxjs';

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
export class EmailListComponent
  implements ControlValueAccessor, OnInit, OnDestroy {
  @Output() emailsChange = new EventEmitter<string[]>();
  @Input() emails: string[];

  autoCompleteSuggestions = [];
  @Input('autoCompleteSuggestions') set _autoCompleteSuggestions(value) {
    this.autoCompleteSuggestions = value;
    this.filterEmails();
  }

  @Input() allowCustomInput = true;

  @ViewChild('emailInput', { static: false }) emailInput: ElementRef<
    HTMLInputElement
  >;

  public propagateChange: (emails: string[]) => void;
  public emailField = new FormControl('', Validators.pattern(EMAIL_REGEX));

  public multiEmailField = new FormControl();
  public emailSearchCtrl = new FormControl();
  public filteredEmails$: ReplaySubject<string[]> = new ReplaySubject<string[]>(
    1
  );

  private onDestroy = new Subject<void>();
  separatorKeys = [ENTER, COMMA, SEMICOLON];

  constructor() {}

  ngOnInit() {
    this.multiEmailField.setValue(this.emails);
    const initialEmails = this.autoCompleteSuggestions
      ? this.autoCompleteSuggestions.slice()
      : [];
    this.filteredEmails$.next(initialEmails);
    this.emailSearchCtrl.valueChanges
      .pipe(takeUntil(this.onDestroy))
      .subscribe(() => {
        this.filterEmails();
      });

    this.multiEmailField.valueChanges
      .pipe(takeUntil(this.onDestroy))
      .subscribe(value => {
        console.log('hello');
        this.emailsChange.emit(value);
      });
  }

  ngOnDestroy() {
    this.onDestroy.next();
    this.onDestroy.complete();
  }

  private filterEmails() {
    if (!this.autoCompleteSuggestions) {
      return;
    }

    // get the search keyword
    let search = this.emailSearchCtrl.value;
    if (!search) {
      this.filteredEmails$.next(this.autoCompleteSuggestions.slice());
      return;
    } else {
      search = search.toLowerCase();
    }

    // filter the email
    this.filteredEmails$.next(
      this.autoCompleteSuggestions.filter(
        email => email.toLowerCase().indexOf(search) > -1
      )
    );
  }

  writeValue(emails: string[]) {
    this.emails = emails;
    this.multiEmailField.setValue(this.emails, { emitEvent: false });
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
