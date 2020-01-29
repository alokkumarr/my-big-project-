import {
  Component,
  Output,
  EventEmitter,
  Input,
  ElementRef,
  Renderer2,
  RendererStyleFlags2,
  OnInit
} from '@angular/core';

import * as forEach from 'lodash/forEach';
import { BehaviorSubject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

const OFFSET_TOP_LIMIT = 300;
@Component({
  selector: 'custom-color-picker',
  templateUrl: './custom-color-picker.component.html',
  styleUrls: ['./custom-color-picker.component.scss']
})
export class CustomColorPickerComponent implements OnInit {
  @Output() public change: EventEmitter<any> = new EventEmitter();

  public config;
  public colorEvent$: BehaviorSubject<any> = new BehaviorSubject('');
  public toggleEvent$: BehaviorSubject<any> = new BehaviorSubject('');

  @Input('config') set setConfig(data) {
    this.config = data;
  }

  constructor(private elRef: ElementRef, private renderer: Renderer2) {}

  ngOnInit() {
    this.colorEvent$
      .pipe(
        debounceTime(500),
        distinctUntilChanged()
      )
      .subscribe(res => {
        if (res) {
          this.change.emit({
            name: 'colorPicker',
            data: res
          });
        }
      });

    /**
     * Here setting the position of color picker because for first metric the color picker height is reduced due to
     * 'auto' property set in config object.
     * Delaying the subscription to make the color picker elems available for styling.
     */
    this.toggleEvent$.pipe(debounceTime(0)).subscribe(result => {
      if (result && this.config.iscustomStyleNeeded) {
        if (this.elRef.nativeElement.offsetTop < OFFSET_TOP_LIMIT) {
          const colorPickerDiv = this.elRef.nativeElement.querySelector(
            'div.color-picker'
          );
          this.renderer.setStyle(
            colorPickerDiv,
            'top',
            '0px',
            RendererStyleFlags2.Important
          );
        }
        this.setColorPickerStyles();
        const div = this.elRef.nativeElement.querySelector('div.arrow');
        this.renderer.setStyle(div, 'display', 'none');
      }
    });
  }

  setColorPickerStyles() {
    const colorDiv = this.elRef.nativeElement.querySelector(
      'div.saturation-lightness'
    );

    const presetColorDiv = this.elRef.nativeElement.querySelectorAll(
      'div.preset-color'
    );

    forEach(presetColorDiv, div => {
      this.renderer.setStyle(div, 'height', '12px');
      this.renderer.setStyle(div, 'width', '12px');
    });

    const typeDiv = this.elRef.nativeElement.querySelector('div.type-policy');
    const hueDiv = this.elRef.nativeElement.querySelector('div.hue-alpha');
    const cmykDiv = this.elRef.nativeElement.querySelector('div.cmyk-text');
    const hslaDiv = this.elRef.nativeElement.querySelector('div.hsla-text');
    const hexDiv = this.elRef.nativeElement.querySelector('div.hex-text');
    const rgbaDiv = this.elRef.nativeElement.querySelector('div.rgba-text');

    this.renderer.setStyle(
      hueDiv,
      'height',
      '60px',
      RendererStyleFlags2.Important
    );

    this.renderer.setStyle(
      cmykDiv,
      'height',
      '55px',
      RendererStyleFlags2.Important
    );
    this.renderer.setStyle(
      hslaDiv,
      'height',
      '55px',
      RendererStyleFlags2.Important
    );
    this.renderer.setStyle(
      hexDiv,
      'height',
      '55px',
      RendererStyleFlags2.Important
    );
    this.renderer.setStyle(
      rgbaDiv,
      'height',
      '55px',
      RendererStyleFlags2.Important
    );

    this.renderer.setStyle(
      colorDiv,
      'height',
      '100px',
      RendererStyleFlags2.Important
    );

    this.renderer.setStyle(
      typeDiv,
      'top',
      '166px',
      RendererStyleFlags2.Important
    );
  }
}
