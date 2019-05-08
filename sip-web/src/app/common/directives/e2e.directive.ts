import {
  Directive,
  Input
} from '@angular/core';

@Directive({
  selector: '[e2e]'
})
export class E2eDirective {
  @Input('e2e') e2e;
}
