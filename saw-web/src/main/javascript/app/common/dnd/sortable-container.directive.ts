import {
  Directive,
  EventEmitter,
  Output,
  Input,
  HostListener,
  HostBinding
} from '@angular/core';

import {
  IDroppableOptions
} from './types';
import {
  arrayMove
} from './utils';
import { dndClasses } from './consts';
import {DragnDropService} from './dnd.service';

@Directive({
  selector: '[dndSortableContainer]'
})
export class DndSortableContainerDirective {
  @Input('dndSortableContainer')
  set dndOptions(options: IDroppableOptions) {
    if (options) {
      this._droppableOptions = options;
    }
  }
  @Input() public dndCollection: Array<any>;
  @Output() public dndOnDrop: EventEmitter<any> = new EventEmitter<any>();

  @HostBinding(`class.${dndClasses.dropAreaDragOver}`) private _isDropAllowed = false;

  // counter for the enter and leave events of the containers children that bubble up
  private _counter = 0;
  private _droppableOptions: IDroppableOptions;
  private _newSortableIndex: number;
  private _insertionPlaceholder: HTMLElement;
  private _placeholderPlace: any;

  constructor(
    private _dragDropService: DragnDropService
  ) {}

  @HostListener('dragenter', ['$event'])
  onDragEnter(event) {
    this._counter ++;
    if (this._counter === 1) {
      // if the container is empty, add the placeholder, and set the index
      // if not, then this will be handled in the onElementDragOver event
      const payload = this._dragDropService.getPayload();
      this._isDropAllowed = this._dragDropService.shouldAllowDrop(payload, this._droppableOptions);

      if (this._isDropAllowed &&
        this.dndCollection &&
        this.dndCollection.length === 0) {
        this.addPlaceholder(event.target, 'inside');
        this._newSortableIndex = 0;
      }
    }
  }

  @HostListener('dragleave', ['$event'])
  onDragLeave() {
    this._counter --;
    if (this._counter === 0) {
      this._isDropAllowed = false;
      this.removePlaceholder();
    }
  }

  @HostListener('dragover', ['$event'])
  onDragOver(event) {
    if (this._isDropAllowed) {
      // allow the drop event
      event.preventDefault();
    }
  }

  @HostListener('drop', ['$event'])
  onDrop() {
    this._isDropAllowed = false;
    const {data} = this._dragDropService.getPayload();
    this._counter = 0;
    this.removePlaceholder();
    this.dndOnDrop.emit({
      index: this._newSortableIndex,
      data
    });
  }

  onElementDragOver(event, element, index) {
    if (!this._isDropAllowed) {
      return;
    }
    const height = element.clientHeight;
    const offset = event.offsetY;
    const pivot = height / 2;
    const buffer = pivot / 2;
    if (offset < pivot - buffer) {
      const newPlaceholderPlace = `${index}-before`;
      if (newPlaceholderPlace !== this._placeholderPlace) {
        this.addPlaceholder(element, 'before');
        this._newSortableIndex = index;
        this._placeholderPlace = newPlaceholderPlace;
      }
    } else if (offset > pivot + buffer) {
      const newPlaceholderPlace = `${index}-after`;
      if (newPlaceholderPlace !== this._placeholderPlace) {
        this.addPlaceholder(element, 'after');
        this._newSortableIndex = index + 1;
        this._placeholderPlace = newPlaceholderPlace;
      }
    }
  }

  removePlaceholder() {
    if (this._insertionPlaceholder) {
      this._insertionPlaceholder.remove();
      this._insertionPlaceholder = null;
    }
  }

  addPlaceholder(element, where: 'before' | 'after' | 'inside') {
    this._insertionPlaceholder = this._dragDropService.getElement();
    const targetElem = event.target;
    switch (where) {
    case 'inside':
      element.append(this._insertionPlaceholder);
      break;
    case 'before':
      element.before(this._insertionPlaceholder);
      break;
    case 'after':
      element.after(this._insertionPlaceholder);
      break;
    }
  }
}
