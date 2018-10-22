import { Component, Input } from '@angular/core';

@Component({
  selector: 'accordion-menu',
  templateUrl: './accordionMenu.component.html',
  styles: [`:host {display: block;}`]
})

export class AccordionMenuComponent {

  @Input() public source: any;
}

