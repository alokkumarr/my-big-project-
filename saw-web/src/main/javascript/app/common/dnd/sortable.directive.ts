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
  ISortableDragEndData
} from './types';
import { dndClasses } from './consts';
import {DragnDropService} from './dnd.service';
import {DndSortableContainerDirective} from './sortable-container.directive';

@Directive({ selector: '[dndSortable]' })
export class DndSortableDirective {
  @Output() dndOnDrag?: EventEmitter<null> = new EventEmitter<null>();
  @Output() dndOnDragEnd?: EventEmitter<ISortableDragEndData> = new EventEmitter<ISortableDragEndData>();

  @Input() dndZones?: string[] = [];
  @Input() dndSortableIndex: number;

  @Input('dndSortable')
  set dndOptions(data: any) {
    if (data) {
      this._data = data;
    }
  }

  @HostBinding(`class.${dndClasses.draggedSortingOldPlace}`) private _isDragged = false;

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
    this._dragDropService.startDrag({
      data: this._data,
      allowedZones: this.dndZones
    }, this._elemRef.nativeElement);
    this.dndOnDrag.emit();
  }

  @HostListener('dragend', ['$event'])
  onDragEnd(event: DragEvent) {
    this._isDragged = false;
    this._dragDropService.onDragEnd();
    const isDropSuccessful = event.dataTransfer.dropEffect !== 'none';
    const sortableDragEndObj: ISortableDragEndData = {
      isDropSuccessful
    };
    this.dndOnDragEnd.emit(sortableDragEndObj);
  }

  @HostBinding('draggable')
  get draggable() {
    return true;
  }
}
