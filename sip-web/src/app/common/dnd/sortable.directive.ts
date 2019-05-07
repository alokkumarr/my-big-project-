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
  @HostBinding(`class.${dndClasses.draggedSortingOldPlace}`) public _isDragged = false;
  // tslint:enable

  public _data: any = null;
  // counter for the enter and leave events of the containers children that bubble up
  public _counter = 0;
  public _enableEventPropagation = false;

  constructor(
    public _dragDropService: DragnDropService,
    public _elemRef: ElementRef,
    @Host() public _sortableContainer: DndSortableContainerDirective
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
  onDragStart(event: Event) {
    // this timeout is needed so that the browser makes the img if the dragged element while it's still visible
    // as the _isDragged property will apply a class, that makes the element invisible

    // this setdata event is inorder to make sure DnD is working as expected on FireFox browser as per ticket SIP-5330
    (<any>event).dataTransfer.setData('application/node type', this._data);
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
