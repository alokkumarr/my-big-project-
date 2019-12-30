import { Directive, ElementRef, AfterViewInit, OnDestroy } from '@angular/core';
/*
  This is the new version of show/hide password functionality, and should be used
  in favor of older password-toggle component. Since this is a directive, this
  applies directly to the field, and doesn't create a standalone password field.

  This makes is suitable for any form / non-form scenario. There are a few requirements:
  1. Input should be inside a mat-form-field
  2. There should be a matSuffix element with icon-show inside it.
  3. Add directive 'showPassword' to your <input> element.

  Example:
  <mat-form-field>
    <input
      matInput
      showPassword
    />
    <mat-icon
      matSuffix
      fontIcon="icon-show"
    ></mat-icon>
  </mat-form-field>

  This directive will automatically get the suffix element and attach toggle functionality
  to it. All other attributes of material library can be used normally.
*/

@Directive({
  selector: '[showPassword]'
})
export class ShowPasswordDirective implements AfterViewInit, OnDestroy {
  private show = false;
  private toggleSuffix: HTMLElement;

  constructor(private el: ElementRef) {}

  ngAfterViewInit() {
    this.setup();
    this.el.nativeElement.setAttribute('type', 'password');
  }

  ngOnDestroy() {
    this.toggleSuffix.removeEventListener('click', this.onToggleClick);
  }

  onToggleClick() {
    this.toggle();
  }

  setup() {
    try {
      this.toggleSuffix = this.el.nativeElement.parentElement.parentElement.querySelector(
        'div.mat-form-field-suffix'
      );
      if (!this.toggleSuffix) {
        return;
      }

      this.toggleSuffix.style.cursor = 'pointer';
      this.toggleSuffix.addEventListener('click', this.onToggleClick);
    } catch (err) {
      // do nothing;
    }
  }

  toggle() {
    this.show = !this.show;
    const icon = this.toggleSuffix.querySelector('mat-icon');
    if (this.show) {
      this.el.nativeElement.setAttribute('type', 'text');
    } else {
      this.el.nativeElement.setAttribute('type', 'password');
    }

    if (icon) {
      icon.classList.replace(
        this.show ? 'icon-show' : 'icon-hide',
        this.show ? 'icon-hide' : 'icon-show'
      );
    }
  }
}
