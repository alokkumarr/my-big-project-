import { Component, Input, Output, EventEmitter } from '@angular/core';
import { ENTER, COMMA } from '@angular/cdk/keycodes';
import { FormControl, Validators } from '@angular/forms';
import * as filter from 'lodash/filter';
import { EMAIL_REGEX } from '../consts';

const SEMICOLON = 186;

@Component({
  selector: 'email-list',
  templateUrl: 'email-list.component.html',
  styleUrls: ['email-list.component.scss']
})
export class EmailListComponent {
  @Output() emailsChange = new EventEmitter<string[]>();
  @Input() emails: string[];

  public emailField = new FormControl('', Validators.pattern(EMAIL_REGEX));
  separatorKeys = [ENTER, COMMA, SEMICOLON];

  constructor() {}

  addEmail(email) {
    const trimmed = (email || '').trim();
    // Reset the input value
    this.emailField.setValue('');
    this.emailsChange.emit([...this.emails, trimmed]);
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
      this.emailsChange.emit(
        filter(this.emails, (_, index) => index === targetIndex)
      );
    }
  }

  trackByIndex(index) {
    return index;
  }
}
