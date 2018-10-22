import { Component, OnInit, Input } from '@angular/core';
import { MatHorizontalStepper } from '@angular/material/stepper';

@Component({
  selector: 'widget-header',
  templateUrl: './widget-header.component.html',
  styleUrls: ['./widget-header.component.scss']
})
export class WidgetHeaderComponent implements OnInit {
  @Input() stepper: MatHorizontalStepper;
  @Input() container;
  @Input() title: string;
  @Input() showBack: boolean;

  constructor() {}

  ngOnInit() {}

  back() {
    this.stepper.previous();
  }

  close() {
    try {
      this.container.close();
    } catch (error) {
      throw new Error(
        'Container is either missing or does not support close method.'
      );
    }
  }
}
