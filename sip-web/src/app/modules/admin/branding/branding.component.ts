import { Component } from '@angular/core';
import  tinyColor  from 'tinyColor2';

export interface Color {
  name: string;
  hex: string;
  darkContrast: boolean;
}

@Component({
  selector: 'admin-branding',
  templateUrl: './branding.component.html',
  styleUrls: ['./branding.component.scss']
})
export class AdminBrandingComponent {

  primaryColor = '#bb0000';

  primaryColorPalette: Color[] = [];

  secondaryColor = '#0000aa';

  secondaryColorPalette: Color[] = [];

  constructor() {
  }

  ngOnInit(): void {
    this.savePrimaryColor();
  }

  savePrimaryColor() {
    this.primaryColorPalette = this.computeColors(this.primaryColor);
    for (const color of this.primaryColorPalette) {
      const key1 = `--theme-primary-${color.name}`;
      const value1 = color.hex;
      const key2 = `--theme-primary-contrast-${color.name}`;
      const value2 = color.darkContrast ? 'rgba(black, 0.87)' : 'white';
      document.documentElement.style.setProperty(key1, value1);
      document.documentElement.style.setProperty(key2, value2);

    }
  }

  computeColors(hex: string): Color[] {
    return [
      this.getColorObject(tinyColor(hex).lighten(52), '50'),
      this.getColorObject(tinyColor(hex).lighten(37), '100'),
      this.getColorObject(tinyColor(hex).lighten(26), '200'),
      this.getColorObject(tinyColor(hex).lighten(12), '300'),
      this.getColorObject(tinyColor(hex).lighten(6), '400'),
      this.getColorObject(tinyColor(hex), '500'),
      this.getColorObject(tinyColor(hex).darken(6), '600'),
      this.getColorObject(tinyColor(hex).darken(12), '700'),
      this.getColorObject(tinyColor(hex).darken(18), '800'),
      this.getColorObject(tinyColor(hex).darken(24), '900'),
      this.getColorObject(tinyColor(hex).lighten(50).saturate(30), 'A100'),
      this.getColorObject(tinyColor(hex).lighten(30).saturate(30), 'A200'),
      this.getColorObject(tinyColor(hex).lighten(10).saturate(15), 'A400'),
      this.getColorObject(tinyColor(hex).lighten(5).saturate(5), 'A700')
    ];
  }

  getColorObject(value, name): Color {
    const c = tinyColor(value);
    return {
      name: name,
      hex: c.toHexString(),
      darkContrast: c.isLight()
    };
  }
}


