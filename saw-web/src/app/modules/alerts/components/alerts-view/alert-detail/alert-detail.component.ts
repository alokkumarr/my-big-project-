import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { AlertConfig, AlertIds } from '../../../alerts.interface';
import { AlertsService } from '../../../services/alerts.service';
import { SubscriptionLike } from 'rxjs';

@Component({
  selector: 'alert-detail',
  templateUrl: './alert-detail.component.html',
  styleUrls: ['./alert-detail.component.scss']
})
export class AlertDetailComponent implements OnInit, OnDestroy {
  alertRuleDetails: AlertConfig;
  subscriptions: SubscriptionLike[] = [];

  constructor(public _alertService: AlertsService) {}

  @Input('alertIds')
  set alertIds(alertIds: AlertIds) {
    if (alertIds) {
      this.getalertRuleDetails(alertIds.alertRulesSysId);
    }
  }

  ngOnInit() {}

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  getalertRuleDetails(id) {
    const ruleDetailsSub = this._alertService
      .getAlertRuleDetails(id)
      .subscribe((data: any) => {
        this.alertRuleDetails = data;
      });

    this.subscriptions.push(ruleDetailsSub);
  }
}
