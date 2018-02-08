
import { NgModule } from '@angular/core';
import {DndDraggableDirective} from './draggable.directive';
import {DndDroppableDirective} from './droppable.directive';
import {DndSortableDirective} from './sortable.directive';
import {DndSortableContainerDirective} from './sortable-container.directive';
import {DragnDropService} from './dnd.service';

declare var require: any;
require('./styles.scss');
@NgModule({
  declarations: [
    DndDraggableDirective,
    DndDroppableDirective,
    DndSortableDirective,
    DndSortableContainerDirective
  ],
  providers: [DragnDropService],
  exports: [
    DndDraggableDirective,
    DndDroppableDirective,
    DndSortableDirective,
    DndSortableContainerDirective
  ]
})
export class DndModule {

}
