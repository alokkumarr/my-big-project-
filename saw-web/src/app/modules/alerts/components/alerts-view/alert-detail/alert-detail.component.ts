import { Component, OnInit, Input } from '@angular/core';
import { AlertIds } from '../../../alerts.interface';
@Component({
  selector: 'alert-detail',
  templateUrl: './alert-detail.component.html',
  styleUrls: ['./alert-detail.component.scss']
})
export class AlertDetailComponent implements OnInit {
  constructor() {}

  @Input('alertIds')
  set alertIds(alertIds: AlertIds) {
    if (alertIds) {
      this.getAlertDetails(alertIds.alertRulesSysId);
    }
  }

  ngOnInit() {}

  getAlertDetails(id: number) {}
}
