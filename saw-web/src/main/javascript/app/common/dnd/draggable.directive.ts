import {
  Directive,
  EventEmitter,
  Output,
  Input,
  ElementRef,
  HostListener,
  HostBinding
} from '@angular/core';

import {
  IDragPayload,
  IDraggableDragEndData
} from './types';
import { dndClasses } from './consts';
import {DragnDropService} from './dnd.service';

@Directive({ selector: '[dndDraggable]' })
export class DndDraggableDirective {
  @Output() dndOnDrag?: EventEmitter<null> = new EventEmitter<null>();
  @Output() dndOnDragEnd?: EventEmitter<IDraggableDragEndData> = new EventEmitter<IDraggableDragEndData>();

  @Input() dndZones?: string[] = [];

  @Input('dndDraggable')
  set dndOptions(data: any) {
    if (data) {
      this._data = data;
    }
  }

  @HostBinding(`class.${dndClasses.draggedOldPlace}`) private _isDragged = false;

  private _data: any = null;

  constructor(
    private _dragDropService: DragnDropService,
    private _elemRef: ElementRef
  ) {}

  @HostListener('dragstart', ['$event'])
  onDragStart(event) {
    this._isDragged = true;
    this._dragDropService.startDrag({
      data: this._data,
      allowedZones: this.dndZones
    }, this._elemRef.nativeElement);
    this.dndOnDrag.emit();
  }

  @HostListener('dragend', ['$event'])
  onDragEnd(event) {
    this._isDragged = false;
    this._dragDropService.onDragEnd();
    const isDropSuccessful = event.dataTransfer.dropEffect !== 'none';
    const draggableDragEndObj: IDraggableDragEndData = {
      isDropSuccessful
    };
    this.dndOnDragEnd.emit(draggableDragEndObj);
  }

  @HostBinding('draggable')
  get draggable() {
    return true;
  }
}
