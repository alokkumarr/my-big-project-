import { Component, OnInit } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';

@Component({
  selector: 'alerts-configuration',
  templateUrl: './alerts-configuration.component.html',
  styleUrls: ['./alerts-configuration.component.scss']
})
export class AlertsConfigurationComponent implements OnInit {
  addAlertPanelMode: 'side' | 'over' = 'side';
  isInTabletMode = false;

  constructor(breakpointObserver: BreakpointObserver) {
    breakpointObserver
      .observe([Breakpoints.Medium, Breakpoints.Small])
      .subscribe(result => {
        this.isInTabletMode = result.matches;
        if (result.matches) {
          this.addAlertPanelMode = 'over';
        } else {
          this.addAlertPanelMode = 'side';
        }
      });
  }

  ngOnInit() {}
}
