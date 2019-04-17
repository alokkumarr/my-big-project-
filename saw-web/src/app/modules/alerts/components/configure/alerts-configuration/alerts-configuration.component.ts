import { Component, OnInit, ViewChild } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { MatSidenav } from '@angular/material';
import { AlertDefinition, AlertConfig } from '../../../alerts.interface';

import { ConfigureAlertService } from '../../../services/configure-alert.service';

@Component({
  selector: 'alerts-configuration',
  templateUrl: './alerts-configuration.component.html',
  styleUrls: ['./alerts-configuration.component.scss']
})
export class AlertsConfigurationComponent implements OnInit {
  addAlertPanelMode: 'side' | 'over' = 'side';
  isInTabletMode = false;
  configuredAlerts$;
  navTitle = 'Add Alert';
  alertDefInput: AlertDefinition = {
    action: 'create'
  };

  constructor(
    breakpointObserver: BreakpointObserver,
    public _configureAlertService: ConfigureAlertService
  ) {
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
    this.configuredAlerts$ = this._configureAlertService.getAllAlerts();
  }

  @ViewChild('alertSidenav') sidenav: MatSidenav;

  ngOnInit() {}

  cancelAddalert() {
    this.sidenav.close();
    this.resetAlertDefInput();
  }

  onAddAlert() {
    this.configuredAlerts$ = this._configureAlertService.getAllAlerts();
    this.sidenav.close();
    this.resetAlertDefInput();
  }

  editAlert(data: AlertConfig) {
    this.navTitle = 'Edit Alert';
    this.alertDefInput.action = 'update';
    this.alertDefInput.alertConfig = data;
    this.sidenav.open();
  }

  resetAlertDefInput() {
    this.alertDefInput = {
      action: 'create'
    };
  }
}
