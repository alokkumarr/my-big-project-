/*
  This is a legacy component. A new version in form of a directive exists at
  src/app/common/directives/show-password.directive.ts
  Visit that file for details and examples on how to use it.

  This is marked legacy, because it removes the flexibility of form field.
  If the password field is to be inside a reactive form, it becomes impossible to do so
  using this component. Another example is add appearance="outline" to password field in some forms,
  and keeping normal appearance in others. All attributes have to be taken as inputs, making it
  more effort than it's worth.
*/
import {
  Component,
  OnInit,
  Output,
  EventEmitter,
  Input,
  ViewChild,
  ElementRef
} from '@angular/core';

const dummyPassword = '**********';

@Component({
  selector: 'password-toggle',
  templateUrl: 'password-toggle.component.html',
  styleUrls: ['./password-toggle.component.scss']
})
export class PasswordToggleComponent implements OnInit {
  // @ViewChild('passwords') passwords;
  @ViewChild('passwordHide', { static: false }) passwordHide: ElementRef;
  @ViewChild('passwordShow', { static: false }) passwordShow: ElementRef;
  public showPassword: boolean;
  public userPassword: String;
  public placeHolder: String;
  public isUserEditMode: boolean;
  public shouldShowIcon: boolean;

  @Output() public change: EventEmitter<string> = new EventEmitter();

  @Input('state')
  set setState(state) {
    if (!state) {
      if (this.passwordHide) {
        this.passwordHide.nativeElement.focus();
      }

      if (this.passwordShow) {
        this.passwordShow.nativeElement.focus();
      }
    }
  }

  @Input('placeholder')
  set setPlaceHolder(data) {
    this.placeHolder = data;
  }
  @Input('isUserEditMode')
  set setUserEditMode(data) {
    this.isUserEditMode = data;
  }
  constructor() {}

  ngOnInit() {
    this.showPassword = false;
    if (this.isUserEditMode) {
      this.userPassword = dummyPassword;
      this.shouldShowIcon = false;
    } else {
      this.shouldShowIcon = true;
    }
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  onPasswordFocus() {
    if (this.isUserEditMode && this.userPassword === dummyPassword) {
      this.shouldShowIcon = false;
      this.userPassword = '';
    }
  }

  onPasswordBlur(event) {
    if (event.target.value === '' && this.isUserEditMode) {
      this.shouldShowIcon = false;
      this.showPassword = false;
      this.userPassword = dummyPassword;
    }
  }

  passwordChange(event) {
    if (
      this.isUserEditMode &&
      event.target.value !== '' &&
      event.target.value !== dummyPassword
    ) {
      this.shouldShowIcon = true;
    }
  }
}
