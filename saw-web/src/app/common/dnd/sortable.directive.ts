import {
  Directive,
  EventEmitter,
  Output,
  Input,
  ElementRef,
  HostListener,
  HostBinding,
  Host
} from '@angular/core';

import {
  ISortableDragEndEvent,
  SortableCallback
} from './types';
import { dndClasses } from './consts';
import {DragnDropService} from './dnd.service';
import {DndSortableContainerDirective} from './sortable-container.directive';

@Directive({ selector: '[dndSortable]' })
export class DndSortableDirective {
  @Output() dndOnDrag?: EventEmitter<null> = new EventEmitter<null>();
  @Output() dndOnDragEnd?: EventEmitter<ISortableDragEndEvent> = new EventEmitter<ISortableDragEndEvent>();

  @Input() dndRemoveFromCallback: SortableCallback;
  @Input() dndZones?: string[] = [];
  @Input() dndSortableIndex: number;
  @Input() dndContainer: any;

  @Input('dndSortable')
  set dndOptions(data: any) {
    if (data) {
      this._data = data;
    }
  }
  // tslint:disable
  @HostBinding(`class.${dndClasses.draggedSortingOldPlace}`) private _isDragged = false;
  // tslint:enable

  private _data: any = null;
  // counter for the enter and leave events of the containers children that bubble up
  private _counter = 0;
  private _enableEventPropagation = false;

  constructor(
    private _dragDropService: DragnDropService,
    private _elemRef: ElementRef,
    @Host() private _sortableContainer: DndSortableContainerDirective
  ) {}

  @HostListener('dragenter', ['$event'])
  onDragEnter() {
    this._counter ++;
    if (this._counter === 1) {
      this._enableEventPropagation = true;
    }
  }

  @HostListener('dragleave', ['$event'])
  onDragLeave() {
    this._counter --;
    if (this._counter === 0) {
      this._enableEventPropagation = false;
    }
  }

  @HostListener('dragover', ['$event'])
  onDragOver(event) {
    const elem = this._elemRef.nativeElement;
    if (this._enableEventPropagation && event.target === elem) {
      this._sortableContainer.onElementDragOver(
        event,
        elem,
        this.dndSortableIndex);
    }
  }

  @HostListener('dragstart', ['$event'])
  onDragStart() {
    // this timeout is needed so that the browser makes the img if the dragged element while it's still visible
    // as the _isDragged property will apply a class, that makes the element invisible
    setTimeout(() => {
      this._isDragged = true;
    }, 50);
    this._dragDropService.setPayload({
      data: this._data,
      allowedZones: this.dndZones
    });
    this._dragDropService.startDrag(this._elemRef.nativeElement);
    this.dndOnDrag.emit();
  }

  @HostListener('dragend', ['$event'])
  onDragEnd(event: Event) { // Using Event. DragEvent doesn't exist in safari and fails unit tests
    this._isDragged = false;
    const {data} = this._dragDropService.getPayload();
    const isDropSuccessful = event['dataTransfer'].dropEffect !== 'none';
    const sortableDragEndObj: ISortableDragEndEvent = {
      isDropSuccessful,
      payload: data,
      container: this.dndContainer,
      index: this.dndSortableIndex,
      removeFromCallback: this.dndRemoveFromCallback
    };
    this._dragDropService.onDragEnd(sortableDragEndObj);
    this.dndOnDragEnd.emit(sortableDragEndObj);
  }

  @HostBinding('draggable')
  get draggable() {
    return true;
  }
}
