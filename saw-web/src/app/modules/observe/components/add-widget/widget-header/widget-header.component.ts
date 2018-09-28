import { Component, OnInit, Input } from '@angular/core';
import { MatHorizontalStepper } from '@angular/material/stepper';

const style = require('./widget-header.component.scss');

@Component({
  selector: 'widget-header',
  templateUrl: './widget-header.component.html',
  styles: [
    `:host {
      'class': 'widget-header'
    }`,
    style
  ]
})

export class WidgetHeaderComponent implements OnInit {
  @Input() stepper: MatHorizontalStepper;
  @Input() container;
  @Input() title: string;
  @Input() showBack: boolean;

  constructor() { }

  ngOnInit() { }

  back() {
    this.stepper.previous();
  }

  close() {
    try {
      this.container.close();
    } catch (error) {
      throw new Error('Container is either missing or does not support close method.');
    }
  }
}
