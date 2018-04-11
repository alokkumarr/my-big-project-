import {
  Directive,
  EventEmitter,
  Output,
  Input,
  HostListener,
  HostBinding
} from '@angular/core';

import { IDroppableOptions } from './types';
import { dndClasses } from './consts';
import {DragnDropService} from './dnd.service';

@Directive({
  selector: '[dndDroppable]'
})
export class DndDroppableDirective {
  @Output() onSuccessfulDrop: EventEmitter<any> = new EventEmitter<any>();

  @Input('dndDroppable')
  set dndOptions(options: IDroppableOptions) {
    if (options) {
      this._droppableOptions = options;
    }
  }

  // apply the classes needed for the droppable container to not
  // take dragenter and dragleave events from it's children
  // tslint:disable
  @HostBinding(`class.${dndClasses.droppableContainer}`) private _droppableContainer = true;
  @HostBinding(`class.${dndClasses.droppableContainerAfter}`) private _droppableContainerAfter = true;

  @HostBinding(`class.${dndClasses.dropAreaDragOver}`) private _isDropAllowed = false;
  // tslint:enable
  private _droppableOptions: IDroppableOptions;

  constructor(
    private _dragDropService: DragnDropService
  ) {}

  @HostListener('dragenter', ['$event'])
  onDragEnter(event) {
    const payload = this._dragDropService.getPayload();
    this._isDropAllowed = this._dragDropService.shouldAllowDrop(payload, this._droppableOptions);
  }

  @HostListener('dragleave', ['$event'])
  onDragLeave(event) {
    this._isDropAllowed = false;
  }

  @HostListener('dragover', ['$event'])
  onDragOver(event) {
    if (this._isDropAllowed) {
      event.preventDefault();
    }
  }

  @HostListener('drop', ['$event'])
  onDrop(event) {
    const payload = this._dragDropService.getPayload();
    this._isDropAllowed = false;
    this.onSuccessfulDrop.emit(payload.data);
  }
}
