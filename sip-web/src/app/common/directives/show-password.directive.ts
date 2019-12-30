import { Directive, ElementRef, AfterViewInit, OnDestroy } from '@angular/core';

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
