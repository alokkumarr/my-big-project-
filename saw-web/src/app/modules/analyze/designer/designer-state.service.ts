import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DesignerStateService {
  analysis: any;
  analysisStarter: any;
  designerMode: string;

  constructor() {}
}
