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
import { FormControl, Validators } from '@angular/forms';

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
  public userPasswordControl = new FormControl('', Validators.required);
  public showPassword: boolean;
  public placeHolder: string;
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

  ngOnInit() {
    this.showPassword = false;
    if (this.isUserEditMode) {
      this.userPasswordControl.setValue(dummyPassword);
      this.shouldShowIcon = false;
    } else {
      this.shouldShowIcon = true;
    }
    this.userPasswordControl.valueChanges.subscribe(value => {
      if (this.isUserEditMode && value !== '' && value !== dummyPassword) {
        this.shouldShowIcon = true;
      }
    });
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  onPasswordFocus() {
    const userPassword = this.userPasswordControl.value;
    if (this.isUserEditMode && userPassword === dummyPassword) {
      this.shouldShowIcon = false;
      this.userPasswordControl.setValue('');
    }
  }

  onPasswordBlur(event) {
    if (event.target.value === '' && this.isUserEditMode) {
      this.shouldShowIcon = false;
      this.showPassword = false;
      this.userPasswordControl.setValue(dummyPassword);
    }
  }
}
