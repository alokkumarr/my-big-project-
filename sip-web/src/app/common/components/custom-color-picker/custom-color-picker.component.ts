import {
  Component,
  Output,
  EventEmitter,
  Input,
  ElementRef,
  Renderer2,
  RendererStyleFlags2
} from '@angular/core';

const OFFSET_TOP_LIMIT = 300;

@Component({
  selector: 'custom-color-picker',
  templateUrl: './custom-color-picker.component.html',
  styleUrls: ['./custom-color-picker.component.scss']
})
export class CustomColorPickerComponent {
  public config;
  @Output() public change: EventEmitter<any> = new EventEmitter();

  @Input('config') set setConfig(data) {
    this.config = data;
  }

  constructor(private elRef: ElementRef, private renderer: Renderer2) {}

  chooseColor(event) {
    setTimeout(() => {
      this.change.emit({
        name: 'colorPicker',
        data: event.color ? event.color : event
      });
    }, 500);
  }

  /**
   * Here setting the position of color picker because for first metric the color picker height is reduced due to
   * 'auto' property set in config object.
   */
  clickOnInput() {
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

  setColorPickerStyles() {
    const colorDiv = this.elRef.nativeElement.querySelector(
      'div.saturation-lightness'
    );

    const typeDiv = this.elRef.nativeElement.querySelector('div.type-policy');

    this.renderer.setStyle(
      colorDiv,
      'height',
      '100px',
      RendererStyleFlags2.Important
    );

    this.renderer.setStyle(
      typeDiv,
      'top',
      '190px',
      RendererStyleFlags2.Important
    );
  }
}
