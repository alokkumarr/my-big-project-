import { Component, Input } from '@angular/core';

const template = require('./accordionMenu.component.html');
const style = require('./accordionMenu.component.scss');

@Component({
  selector: 'accordion-menu',
  template,
  styles: [style]
})

export class AccordionMenuComponent {

  @Input() public source: any;
}

