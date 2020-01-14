import {
  Component,
  Output,
  EventEmitter,
  Input,
  ElementRef,
  Renderer2,
  RendererStyleFlags2
} from '@angular/core';

const OFFSET_TOP_LIMIT = 250;

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
    if (event.color) {
      setTimeout(() => {
        this.change.emit({ name: 'colorPicker', data: event });
      }, 500);
    }
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
        '5px',
        RendererStyleFlags2.Important
      );
    }

    const div = this.elRef.nativeElement.querySelector('div.arrow');
    this.renderer.setStyle(div, 'display', 'none');
  }
}
