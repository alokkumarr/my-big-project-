import {
  Input,
  Output,
  Component,
  EventEmitter,
  OnInit,
  ViewChild,
  ElementRef,
  OnDestroy
} from '@angular/core';
import * as debounce from 'lodash/debounce';

const template = require('./search-box.component.html');
require('./search-box.component.scss');

@Component({
  selector: 'search-box',
  template
})
export class SearchBoxComponent implements OnInit, OnDestroy {

  @Output() searchTermChange: EventEmitter<string> = new EventEmitter();
  @Input() value: string;
  @Input() placeholder: string;
  @Input() delay: number;

  @ViewChild('searchInput') searchInput: ElementRef;

  states = {
    hovered: false,
    focused: false
  };

  onChangeDebounced: Function;
  constructor(private _elemRef: ElementRef) {
    this.onBodyClick = this.onBodyClick.bind(this);
  }

  ngOnInit() {
    this.delay = Number(this.delay) || 250;
    this.onChangeDebounced = debounce(this.onChange, this.delay);

    document.body.addEventListener('click', this.onBodyClick);
  }

  ngOnDestroy() {
    document.body.removeEventListener('click', this.onBodyClick);
  }

  onBodyClick(e) {
    const target = e.target;
    const elem = this._elemRef.nativeElement;
    const clickOutside = !elem.contains(target);
    if (clickOutside) {
      this.states.focused = false;
    }
  }

  onChange(value) {
    this.searchTermChange.emit(value);
  }

  doAutoFocus() {
    this.states.focused = true;
    this.searchInput.nativeElement.focus();
  }

  onClose() {
    this.states.focused = false;
    this.onChange('');
  }
}
