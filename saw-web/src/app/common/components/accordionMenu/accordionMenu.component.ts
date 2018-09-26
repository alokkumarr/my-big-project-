import { Component, Input } from '@angular/core';

const template = require('./accordionMenu.component.html');
require('./accordionMenu.component.scss');

@Component({
  selector: 'accordion-menu',
  template
})

export class AccordionMenuComponent {

  @Input() public source: any;
}

