import { Component } from '@angular/core';
import { BrandingService } from './branding.service';
import { ToastService } from '../../../common/services';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { DEFAULT_BRANDING_COLOR } from './../../../common/consts';
import { JwtService } from './../../../common/services/jwt.service';
import * as isEmpty from 'lodash/isEmpty';

@Component({
  selector: 'admin-branding',
  templateUrl: './branding.component.html',
  styleUrls: ['./branding.component.scss']
})
export class AdminBrandingComponent {
  primaryColor = '';
  ticket: string = '';
  filesToUpload = [];
  binaryFormatImage: any;
  brandImage: any;
  public saveForm: FormGroup;
  fileUploadState: boolean;

  constructor(
    private _brandingService: BrandingService,
    public _toastMessage: ToastService,
    public fb: FormBuilder,
    private _jwtService: JwtService
  ) {}

  ngOnInit(): void {
    // this.fb.group({
    //   color: [this.primaryColor, [Validators.required,
    //     Validators.maxLength(30)]],
    //   logo: ['', []]
    // });

    this.saveForm = new FormGroup({
      color: new FormControl(this.primaryColor, [
        Validators.required
      ]),
      logo: new FormControl({value: '', disabled: true})

    });

    const token = this._jwtService.getTokenObj();
    this.ticket = token.ticket;
    console.log(this.ticket);

    this._brandingService.getBrandingDetails().subscribe(data => {
      this.primaryColor = isEmpty(data.brandColor) ? DEFAULT_BRANDING_COLOR : data.brandColor;
      this.brandImage = 'data:image/gif;base64,' + data.brandImage;
      this._brandingService.savePrimaryColor(this.primaryColor);
      this.saveForm.get('color').setValue(this.primaryColor);
    });
  }

  prepareBrandingForUpload(event) {
    this.filesToUpload = event.srcElement.files;
    this.fileUploadState = isEmpty(this.filesToUpload);
    this.binaryFormatImage = this.filesToUpload[0].name;
  }

  saveBrandingDetails() {
    this.fileUploadState = isEmpty(this.filesToUpload);
    if (isEmpty(this.filesToUpload) || isEmpty(this.saveForm.get('color').value)) {
      return false;
    }
    this._brandingService.uploadFile(this.filesToUpload, this.saveForm.get('color').value).subscribe(data => {
      this._toastMessage.success(data.message);
      window.location.reload();
    });
  }

  resetBrandingData() {
    this._brandingService.reset().subscribe(data => {
      window.location.reload();
    });
  }
}



