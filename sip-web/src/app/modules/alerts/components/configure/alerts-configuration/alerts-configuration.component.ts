import { Component, ViewChild, OnDestroy } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { MatSidenav, MatDialog } from '@angular/material';
import { SubscriptionLike } from 'rxjs';
import CustomStore from 'devextreme/data/custom_store';

import { ConfirmActionDialogComponent } from '../confirm-action-dialog/confirm-action-dialog.component';

import { AlertDefinition, AlertConfig } from '../../../alerts.interface';
import { ToastService } from '../../../../../common/services/toastMessage.service';
import { ConfigureAlertService } from '../../../services/configure-alert.service';

@Component({
  selector: 'alerts-configuration',
  templateUrl: './alerts-configuration.component.html',
  styleUrls: ['./alerts-configuration.component.scss']
})
export class AlertsConfigurationComponent implements OnDestroy {
  public data;
  subscriptions: SubscriptionLike[] = [];
  addAlertPanelMode: 'side' | 'over' = 'side';
  isInTabletMode = false;
  configuredAlerts$;
  navTitle = 'Add Alert';
  alertDefInput: AlertDefinition = {
    action: 'create'
  };

  constructor(
    breakpointObserver: BreakpointObserver,
    public _configureAlertService: ConfigureAlertService,
    public dialog: MatDialog,
    private _notify: ToastService
  ) {
    const breakpointObserverSub = breakpointObserver
      .observe([Breakpoints.Medium, Breakpoints.Small])
      .subscribe(result => {
        this.isInTabletMode = result.matches;
        if (result.matches) {
          this.addAlertPanelMode = 'over';
        } else {
          this.addAlertPanelMode = 'side';
        }
      });
    this.subscriptions.push(breakpointObserverSub);
    this.setAlertLoaderForGrid();
  }

  @ViewChild('alertSidenav') sidenav: MatSidenav;

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  cancelAddalert() {
    this.sidenav.close();
    this.resetAlertDefInput();
  }

  onAddAlert() {
    this.setAlertLoaderForGrid();
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

  deleteAlert(alertConfig: AlertConfig) {
    const dialogRef = this.dialog.open(ConfirmActionDialogComponent, {
      width: '400px',
      data: {
        typeTitle: 'Alert Name',
        typeName: alertConfig.alertRuleName
      }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        const delConfirmSubscription = this._configureAlertService
          .deleteAlert(alertConfig.alertRulesSysId)
          .subscribe((data: any) => {
            this._notify.success(data.message);
            this.setAlertLoaderForGrid();
          });
        this.subscriptions.push(delConfirmSubscription);
      }
    });
  }

  setAlertLoaderForGrid() {
    const alertsDataLoader = options => {
      return this._configureAlertService.getAllAlerts(options).then(result => ({
        data: result.alertRuleDetailsList,
        totalCount: result.numberOfRecords
      }));
    };

    this.data = new CustomStore({
      load: options => alertsDataLoader(options)
    });
  }
}
