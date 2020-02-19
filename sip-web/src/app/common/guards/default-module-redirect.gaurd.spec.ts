import { TestBed, async } from '@angular/core/testing';
import { DefaultModuleGuard } from './default-module-redirect.guard';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { JwtService } from './../../common/services/jwt.service';

const auth_token = {
  "sub": "sawadmin@synchronoss.com",
  "ticket": {
    "ticketId": "28_1581577052113_959440744",
    "windowId": "28_1581577052113_621184445",
    "masterLoginId": "sawadmin@synchronoss.com",
    "userFullName": "system sncr admin",
    "defaultProdID": "4",
    "roleCode": "SYNCHRONOSS_ADMIN_USER",
    "roleType": "ADMIN",
    "createdTime": 1581577052113,
    "error": null,
    "custID": "1",
    "custCode": "SYNCHRONOSS",
    "isJvCustomer": 1,
    "filterByCustomerCode": 0,
    "userId": 1,
    "products": [
      {
        "productName": "SAW Demo",
        "productDesc": "SAW Demo",
        "productCode": "SAWD000001",
        "productID": "4",
        "privilegeCode": 128,
        "productModules": [
          {
            "prodCode": "SAWD000001",
            "productModName": "ANALYZE",
            "productModDesc": "Analyze Module",
            "productModCode": "ANLYS00001",
            "productModID": "1",
            "moduleURL": "/",
            "defaultMod": "1",
            "privilegeCode": 128,
            "prodModFeature": [
              {
                "prodCode": "SAWD000001",
                "prodModCode": "ANLYS00001",
                "prodModFeatureName": "My Analysis",
                "prodModFeatureDesc": "My Analysis",
                "defaultURL": "/",
                "privilegeCode": 0,
                "prodModFeatureID": 3,
                "prodModFeatureType": "PARENT_F0000000002",
                "defaultFeature": "0",
                "prodModFeatrCode": "F0000000002",
                "systemCategory": false,
                "roleId": null,
                "productModuleSubFeatures": [
                  {
                    "prodCode": "SAWD000001",
                    "prodModCode": "ANLYS00001",
                    "prodModFeatureName": "DRAFTS",
                    "prodModFeatureDesc": "Drafts",
                    "defaultURL": "/",
                    "privilegeCode": 128,
                    "prodModFeatureID": 5,
                    "prodModFeatureType": "CHILD_F0000000002",
                    "defaultFeature": "0",
                    "prodModFeatrCode": "F0000000004",
                    "systemCategory": false,
                    "roleId": 1,
                    "productModuleSubFeatures": null
                  }
                ]
              }
            ]
          }
        ]
      }
    ],
    "validUpto": 1581584252113,
    "valid": true,
    "validityReason": "User Authenticated Successfully",
    "validMins": null,
    "sipDskAttribute": null,
    "customConfig": [
      "es-analysis-auto-refresh"
    ]
  },
  "iat": 1581577052
}
class JwtServiceStub {
  get findDefaultCategoryId() {
    return 4;
  }

  getTokenObj() {
    return auth_token;
  }
}

describe('Default Module Redirect Gaurd', () => {
​
  let defaultModuleGuard: DefaultModuleGuard;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [DefaultModuleGuard,
        { provide: JwtService, useClass: JwtServiceStub }]
    }).compileComponents();
  }));

  beforeEach(() => {
    defaultModuleGuard = TestBed.get(DefaultModuleGuard);
  });
​
  it('check if user has privilige to observe module', () => {
    expect(
      defaultModuleGuard.checkObservePrivilege(4)
    ).toEqual(false);
  });
});
