import { Component, Input } from '@angular/core';

const template = require('./accordionMenu.component.html');

@Component({
  selector: 'accordion-menu',
  template,
  styles: [`:host {display: block;}`]
})

export class AccordionMenuComponent {

  @Input() public source: any;
}

