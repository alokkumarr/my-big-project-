import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { ListSubscriberComponent } from './list-subscriber/list-subscriber.component';

const routes: Routes = [
  {
    path: '',
    component: ListSubscriberComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SubscriberRoutingModule {}
