import { Component, OnInit } from '@angular/core';
import { SIPSubscriber } from '../models/subscriber.model';
import { Observable } from 'rxjs';
import { SubscriberService } from '../subscriber.service';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { map, first } from 'rxjs/operators';
import { AddSubscriberComponent } from '../add-subscriber/add-subscriber.component';
import { ConfirmDialogComponent } from 'src/app/common/components/confirm-dialog';
import { ConfirmDialogData } from 'src/app/common/types';

@Component({
  selector: 'list-subscriber',
  templateUrl: './list-subscriber.component.html',
  styleUrls: ['./list-subscriber.component.scss']
})
export class ListSubscriberComponent implements OnInit {
  DEFAULT_PAGE_SIZE = 25;

  subscribers$: Observable<SIPSubscriber[]> = this.allSubscribers();
  enablePaging$ = this.subscribers$.pipe(
    map(items => items.length > this.DEFAULT_PAGE_SIZE)
  );

  constructor(
    private subscriberService: SubscriberService,
    private dialog: MatDialog
  ) {}

  ngOnInit() {}

  allSubscribers() {
    return this.subscriberService.getAllSubscribers();
  }

  edit(subscriber: SIPSubscriber) {
    const dialogRef = this.openSubscriberDialog(subscriber);
    dialogRef.afterClosed().subscribe(data => {
      if (!data) {
        return;
      }

      this.subscribers$ = this.allSubscribers();
    });
  }

  addSubscriber() {
    const dialogRef = this.openSubscriberDialog();

    dialogRef.afterClosed().subscribe(data => {
      if (!data) {
        return;
      }
      this.subscribers$ = this.allSubscribers();
    });
  }

  delete(subscriberId: string) {
    this.openDeleteDialog()
      .afterClosed()
      .subscribe(isDeleteSuccess => {
        if (!isDeleteSuccess) {
          return;
        }
        this.subscriberService
          .deleteSubscriber(subscriberId)
          .pipe(first())
          .subscribe(() => {
            this.subscribers$ = this.allSubscribers();
          });
      });
  }

  openDeleteDialog(): MatDialogRef<ConfirmDialogComponent> {
    return this.dialog.open(ConfirmDialogComponent, {
      data: <ConfirmDialogData>{
        title: 'Delete',
        content: 'Are you sure you want to delete this subscriber?',
        positiveActionLabel: 'Delete',
        negativeActionLabel: 'Cancel',
        primaryColor: 'warn'
      }
    });
  }

  openSubscriberDialog(subscriber: SIPSubscriber = null) {
    return this.dialog.open(AddSubscriberComponent, {
      data: {
        subscriber
      }
    });
  }
}
