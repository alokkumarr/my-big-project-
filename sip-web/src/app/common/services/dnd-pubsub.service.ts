import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export type DndEvent = 'dragStart' | 'dragEnd';

@Injectable()
export class DndPubsubService {

  private _subject = new Subject<DndEvent>();

  subscribe(fn: (event: DndEvent) => void) {
    return this._subject.subscribe(fn);
  }

  emit(event: DndEvent) {
    this._subject.next(event);
  }
}
