import { Inject } from '@angular/core';
import * as get from 'lodash/get';
import * as has from 'lodash/has';
import * as padStart from 'lodash/padStart';
import * as find from 'lodash/find';
import * as flatMap from 'lodash/flatMap';
import * as fpGet from 'lodash/fp/get';

import APP_CONFIG from '../../../../../appConfig';

const PRIVILEGE_CODE_LENGTH = 16;

const PRIVILEGE_INDEX = {
  ACCESS: 0,
  CREATE: 1,
  EXECUTE: 2,
  PUBLISH: 3,
  FORK: 4,
  EDIT: 5,
  EXPORT: 6,
  DELETE: 7,
  ALL: 8
};

export class JwtService {
  //private api = fpGet('api.url', APP_CONFIG);
  private jwtKey = fpGet('login.jwtKey', APP_CONFIG);
  constructor() {}

  initialise() {
    this._refreshTokenKey = `${this.jwtKey}Refresh`;
  }


  set(accessToken, refreshToken) {
    window.localStorage[this.jwtKey] = accessToken;
    window.localStorage[this.jwtKey] = refreshToken;
  }

  get() {
    return window.localStorage[this.jwtKey];
    //return 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTQVdBRE1JTkBTWU5DSFJPTk9TUy5DT00iLCJ0aWNrZXQiOnsidGlja2V0SWQiOiIzMl8xNTI2MDM0MDIxMTU2XzE5NzI3OTE1OTkiLCJ3aW5kb3dJZCI6IjMyXzE1MjYwMzQwMjExNTZfMzc0MTQ0MzUiLCJtYXN0ZXJMb2dpbklkIjoiU0FXQURNSU5AU1lOQ0hST05PU1MuQ09NIiwidXNlckZ1bGxOYW1lIjoic3lzdGVtIHNuY3IgYWRtaW4iLCJkZWZhdWx0UHJvZElEIjoiNCIsInJvbGVDb2RlIjoiU1lOQ0hST05PU1NfQURNSU5fVVNFUiIsInJvbGVUeXBlIjoiQURNSU4iLCJjcmVhdGVkVGltZSI6MTUyNjAzNDAyMTE1NiwiZGF0YVNlY3VyaXR5S2V5IjpbXSwiZXJyb3IiOm51bGwsImN1c3RJRCI6IjEiLCJjdXN0Q29kZSI6IlNZTkNIUk9OT1NTIiwidXNlcklkIjoxLCJwcm9kdWN0cyI6W3sicHJvZHVjdE5hbWUiOiJTQVcgRGVtbyIsInByb2R1Y3REZXNjIjoiU0FXIERlbW8iLCJwcm9kdWN0Q29kZSI6IlNBV0QwMDAwMDEiLCJwcm9kdWN0SUQiOiI0IiwicHJpdmlsZWdlQ29kZSI6MTI4LCJwcm9kdWN0TW9kdWxlcyI6W3sicHJvZENvZGUiOiJTQVdEMDAwMDAxIiwicHJvZHVjdE1vZE5hbWUiOiJBTkFMWVpFIiwicHJvZHVjdE1vZERlc2MiOiJBbmFseXplIE1vZHVsZSIsInByb2R1Y3RNb2RDb2RlIjoiQU5MWVMwMDAwMSIsInByb2R1Y3RNb2RJRCI6IjEiLCJtb2R1bGVVUkwiOiIvIiwiZGVmYXVsdE1vZCI6IjEiLCJwcml2aWxlZ2VDb2RlIjoxMjgsInByb2RNb2RGZWF0dXJlIjpbeyJwcm9kQ29kZSI6IlNBV0QwMDAwMDEiLCJwcm9kTW9kQ29kZSI6IkFOTFlTMDAwMDEiLCJwcm9kTW9kRmVhdHVyZU5hbWUiOiJDYW5uZWQgQW5hbHlzaXMiLCJwcm9kTW9kRmVhdHVyZURlc2MiOiJTdGFuZGFyZCBQcmUtQnVpbHQgUmVwb3J0cyBDYXRlZ29yeSIsImRlZmF1bHRVUkwiOiIvIiwicHJpdmlsZWdlQ29kZSI6MCwicHJvZE1vZEZlYXR1cmVJRCI6MiwicHJvZE1vZEZlYXR1cmVUeXBlIjoiUEFSRU5UX0NBTk5FREFOQUxZU0lTMSIsImRlZmF1bHRGZWF0dXJlIjoiMSIsInByb2RNb2RGZWF0ckNvZGUiOiJDQU5ORURBTkFMWVNJUzEiLCJyb2xlSWQiOm51bGwsInByb2R1Y3RNb2R1bGVTdWJGZWF0dXJlcyI6W3sicHJvZENvZGUiOiJTQVdEMDAwMDAxIiwicHJvZE1vZENvZGUiOiJBTkxZUzAwMDAxIiwicHJvZE1vZEZlYXR1cmVOYW1lIjoiT3B0aW1pemF0aW9uIiwicHJvZE1vZEZlYXR1cmVEZXNjIjoiT3B0aW1pemF0aW9uIHN1Yi1jYXRlZ29yeSIsImRlZmF1bHRVUkwiOiIvIiwicHJpdmlsZWdlQ29kZSI6MTI4LCJwcm9kTW9kRmVhdHVyZUlEIjo0LCJwcm9kTW9kRmVhdHVyZVR5cGUiOiJDSElMRF9DQU5ORURBTkFMWVNJUzEiLCJkZWZhdWx0RmVhdHVyZSI6IjAiLCJwcm9kTW9kRmVhdHJDb2RlIjoiT1BUSU1JWkFUSU9ONCIsInJvbGVJZCI6MSwicHJvZHVjdE1vZHVsZVN1YkZlYXR1cmVzIjpudWxsfSx7InByb2RDb2RlIjoiU0FXRDAwMDAwMSIsInByb2RNb2RDb2RlIjoiQU5MWVMwMDAwMSIsInByb2RNb2RGZWF0dXJlTmFtZSI6Ik1DVCBUZW1wbGF0ZXMiLCJwcm9kTW9kRmVhdHVyZURlc2MiOiJjYW5uZWQgc3ViLWNhdCBmb3IgaW50ZXJuYWwgdGVzdGVycyIsImRlZmF1bHRVUkwiOiIvIiwicHJpdmlsZWdlQ29kZSI6MTI4LCJwcm9kTW9kRmVhdHVyZUlEIjozMCwicHJvZE1vZEZlYXR1cmVUeXBlIjoiQ0hJTERfQ0FOTkVEQU5BTFlTSVMxIiwiZGVmYXVsdEZlYXR1cmUiOiIwIiwicHJvZE1vZEZlYXRyQ29kZSI6Ik1DVFRFTVBMQVRFUzEiLCJyb2xlSWQiOjEsInByb2R1Y3RNb2R1bGVTdWJGZWF0dXJlcyI6bnVsbH0seyJwcm9kQ29kZSI6IlNBV0QwMDAwMDEiLCJwcm9kTW9kQ29kZSI6IkFOTFlTMDAwMDEiLCJwcm9kTW9kRmVhdHVyZU5hbWUiOiJNYXN0ZXIgQm9va2luZ3MgVGVtcGxhdGVzIiwicHJvZE1vZEZlYXR1cmVEZXNjIjoibXN0ciBib29rIHByZS1kZWZpbmVkIHRlbXBsYXRlcyIsImRlZmF1bHRVUkwiOiIvIiwicHJpdmlsZWdlQ29kZSI6MzMwMjQsInByb2RNb2RGZWF0dXJlSUQiOjM4LCJwcm9kTW9kRmVhdHVyZVR5cGUiOiJDSElMRF9DQU5ORURBTkFMWVNJUzEiLCJkZWZhdWx0RmVhdHVyZSI6IjAiLCJwcm9kTW9kRmVhdHJDb2RlIjoiTUFTVEVSQk9PS0lOR1NURU1QTEFURVMxIiwicm9sZUlkIjoxLCJwcm9kdWN0TW9kdWxlU3ViRmVhdHVyZXMiOm51bGx9XX0seyJwcm9kQ29kZSI6IlNBV0QwMDAwMDEiLCJwcm9kTW9kQ29kZSI6IkFOTFlTMDAwMDEiLCJwcm9kTW9kRmVhdHVyZU5hbWUiOiJUZXN0IENhc2VzIiwicHJvZE1vZEZlYXR1cmVEZXNjIjpudWxsLCJkZWZhdWx0VVJMIjoiLyIsInByaXZpbGVnZUNvZGUiOjAsInByb2RNb2RGZWF0dXJlSUQiOjc3LCJwcm9kTW9kRmVhdHVyZVR5cGUiOiJQQVJFTlRfVEVTVENBU0VTMSIsImRlZmF1bHRGZWF0dXJlIjoiMCIsInByb2RNb2RGZWF0ckNvZGUiOiJURVNUQ0FTRVMxIiwicm9sZUlkIjpudWxsLCJwcm9kdWN0TW9kdWxlU3ViRmVhdHVyZXMiOlt7InByb2RDb2RlIjoiU0FXRDAwMDAwMSIsInByb2RNb2RDb2RlIjoiQU5MWVMwMDAwMSIsInByb2RNb2RGZWF0dXJlTmFtZSI6IkJ1ZyBUaWNrZXRzIiwicHJvZE1vZEZlYXR1cmVEZXNjIjoibG9uZyB0ZXJtIHN0b3JhZ2UgZm9yIHJlcHJvZHVjaWJsZSBjYXNlcyBwYXN0IGFuZCBwcmVzZW50IiwiZGVmYXVsdFVSTCI6Ii8iLCJwcml2aWxlZ2VDb2RlIjoxMjgsInByb2RNb2RGZWF0dXJlSUQiOjc4LCJwcm9kTW9kRmVhdHVyZVR5cGUiOiJDSElMRF9URVNUQ0FTRVMxIiwiZGVmYXVsdEZlYXR1cmUiOiIwIiwicHJvZE1vZEZlYXRyQ29kZSI6IkJVR1RJQ0tFVFMxIiwicm9sZUlkIjoxLCJwcm9kdWN0TW9kdWxlU3ViRmVhdHVyZXMiOm51bGx9LHsicHJvZENvZGUiOiJTQVdEMDAwMDAxIiwicHJvZE1vZENvZGUiOiJBTkxZUzAwMDAxIiwicHJvZE1vZEZlYXR1cmVOYW1lIjoiR2VuZXJhbCBSZWdyZXNzaW9uIFRlc3RzIiwicHJvZE1vZEZlYXR1cmVEZXNjIjoiR2VuZXJhbCBmdW5jdGlvbmFsaXR5IHRlc3QgY2FzZXMiLCJkZWZhdWx0VVJMIjoiLyIsInByaXZpbGVnZUNvZGUiOjEyOCwicHJvZE1vZEZlYXR1cmVJRCI6NzksInByb2RNb2RGZWF0dXJlVHlwZSI6IkNISUxEX1RFU1RDQVNFUzEiLCJkZWZhdWx0RmVhdHVyZSI6IjAiLCJwcm9kTW9kRmVhdHJDb2RlIjoiR0VORVJBTFJFR1JFU1NJT05URVNUUzEiLCJyb2xlSWQiOjEsInByb2R1Y3RNb2R1bGVTdWJGZWF0dXJlcyI6bnVsbH0seyJwcm9kQ29kZSI6IlNBV0QwMDAwMDEiLCJwcm9kTW9kQ29kZSI6IkFOTFlTMDAwMDEiLCJwcm9kTW9kRmVhdHVyZU5hbWUiOiJDdXN0b21lciBFeGFtcGxlcyIsInByb2RNb2RGZWF0dXJlRGVzYyI6InNwZWNpZmljIGN1c3RvbWVyIHJlcXVpcmVkIHRlc3QgY2FzZXMgb3IgY29tcGxleCBleGFtcGxlcyBjb3BpZWQgZnJvbSB2YXJpb3VzIGN1c3RvbWVycyIsImRlZmF1bHRVUkwiOiIvIiwicHJpdmlsZWdlQ29kZSI6MTI4LCJwcm9kTW9kRmVhdHVyZUlEIjo4MCwicHJvZE1vZEZlYXR1cmVUeXBlIjoiQ0hJTERfVEVTVENBU0VTMSIsImRlZmF1bHRGZWF0dXJlIjoiMCIsInByb2RNb2RGZWF0ckNvZGUiOiJDVVNUT01FUkVYQU1QTEVTMSIsInJvbGVJZCI6MSwicHJvZHVjdE1vZHVsZVN1YkZlYXR1cmVzIjpudWxsfSx7InByb2RDb2RlIjoiU0FXRDAwMDAwMSIsInByb2RNb2RDb2RlIjoiQU5MWVMwMDAwMSIsInByb2RNb2RGZWF0dXJlTmFtZSI6IlFBIFNhdmVkIFJlcG9ydHMiLCJwcm9kTW9kRmVhdHVyZURlc2MiOiJRQSBTYXZlZCBSZXBvcnRzIiwiZGVmYXVsdFVSTCI6Ii8iLCJwcml2aWxlZ2VDb2RlIjoxMjgsInByb2RNb2RGZWF0dXJlSUQiOjgyLCJwcm9kTW9kRmVhdHVyZVR5cGUiOiJDSElMRF9URVNUQ0FTRVMxIiwiZGVmYXVsdEZlYXR1cmUiOiIwIiwicHJvZE1vZEZlYXRyQ29kZSI6IlFBU0FWRURSRVBPUlRTMSIsInJvbGVJZCI6MSwicHJvZHVjdE1vZHVsZVN1YkZlYXR1cmVzIjpudWxsfSx7InByb2RDb2RlIjoiU0FXRDAwMDAwMSIsInByb2RNb2RDb2RlIjoiQU5MWVMwMDAwMSIsInByb2RNb2RGZWF0dXJlTmFtZSI6IkRhdGUgRmlsdGVycyIsInByb2RNb2RGZWF0dXJlRGVzYyI6IlNBVy0xNTc5IEFuYWx5c2lzIEZpbHRlcnMgc2hvdWxkIHN1cHBvcnQgZW51bWVyYXRlZCBkYXRlIHJhbmdlcyBhcyBmaWx0ZXIiLCJkZWZhdWx0VVJMIjoiLyIsInByaXZpbGVnZUNvZGUiOjEyOCwicHJvZE1vZEZlYXR1cmVJRCI6ODcsInByb2RNb2RGZWF0dXJlVHlwZSI6IkNISUxEX1RFU1RDQVNFUzEiLCJkZWZhdWx0RmVhdHVyZSI6IjAiLCJwcm9kTW9kRmVhdHJDb2RlIjoiREFURUZJTFRFUlMxIiwicm9sZUlkIjoxLCJwcm9kdWN0TW9kdWxlU3ViRmVhdHVyZXMiOm51bGx9XX0seyJwcm9kQ29kZSI6IlNBV0QwMDAwMDEiLCJwcm9kTW9kQ29kZSI6IkFOTFlTMDAwMDEiLCJwcm9kTW9kRmVhdHVyZU5hbWUiOiJNeSBBbmFseXNpcyIsInByb2RNb2RGZWF0dXJlRGVzYyI6IlNwZWNpYWwgZGVmYXVsdCBjYXRlZ29yeSB0aGF0IHNob3VsZCBiZSBmaWx0ZXJlZCB0byBvbmx5IHNob3cgYSB1c2VyJ3MgY3JlYXRlZCBhbmFseXNlcyIsImRlZmF1bHRVUkwiOiIvIiwicHJpdmlsZWdlQ29kZSI6MCwicHJvZE1vZEZlYXR1cmVJRCI6MywicHJvZE1vZEZlYXR1cmVUeXBlIjoiUEFSRU5UX01ZQU5BTFlTSVMxIiwiZGVmYXVsdEZlYXR1cmUiOiIwIiwicHJvZE1vZEZlYXRyQ29kZSI6Ik1ZQU5BTFlTSVMxIiwicm9sZUlkIjpudWxsLCJwcm9kdWN0TW9kdWxlU3ViRmVhdHVyZXMiOlt7InByb2RDb2RlIjoiU0FXRDAwMDAwMSIsInByb2RNb2RDb2RlIjoiQU5MWVMwMDAwMSIsInByb2RNb2RGZWF0dXJlTmFtZSI6IkRyYWZ0cyIsInByb2RNb2RGZWF0dXJlRGVzYyI6IkRyYWZ0cyBkZXNjIiwiZGVmYXVsdFVSTCI6Ii8iLCJwcml2aWxlZ2VDb2RlIjoxMjgsInByb2RNb2RGZWF0dXJlSUQiOjUsInByb2RNb2RGZWF0dXJlVHlwZSI6IkNISUxEX01ZQU5BTFlTSVMxIiwiZGVmYXVsdEZlYXR1cmUiOiIwIiwicHJvZE1vZEZlYXRyQ29kZSI6IkRSQUZUUzUiLCJyb2xlSWQiOjEsInByb2R1Y3RNb2R1bGVTdWJGZWF0dXJlcyI6bnVsbH0seyJwcm9kQ29kZSI6IlNBV0QwMDAwMDEiLCJwcm9kTW9kQ29kZSI6IkFOTFlTMDAwMDEiLCJwcm9kTW9kRmVhdHVyZU5hbWUiOiJSZXZpZXciLCJwcm9kTW9kRmVhdHVyZURlc2MiOm51bGwsImRlZmF1bHRVUkwiOiIvIiwicHJpdmlsZWdlQ29kZSI6MTI4LCJwcm9kTW9kRmVhdHVyZUlEIjo0NywicHJvZE1vZEZlYXR1cmVUeXBlIjoiQ0hJTERfTVlBTkFMWVNJUzEiLCJkZWZhdWx0RmVhdHVyZSI6IjAiLCJwcm9kTW9kRmVhdHJDb2RlIjoiUkVWSUVXMSIsInJvbGVJZCI6MSwicHJvZHVjdE1vZHVsZVN1YkZlYXR1cmVzIjpudWxsfSx7InByb2RDb2RlIjoiU0FXRDAwMDAwMSIsInByb2RNb2RDb2RlIjoiQU5MWVMwMDAwMSIsInByb2RNb2RGZWF0dXJlTmFtZSI6IkRhc2hib2FyZCBDaGFydHMiLCJwcm9kTW9kRmVhdHVyZURlc2MiOiJDaGFydHMgdG8gYmUgdXNlZCB3aXRoIERhc2hib2FyZCBCdWlsZGVyIiwiZGVmYXVsdFVSTCI6Ii8iLCJwcml2aWxlZ2VDb2RlIjoxMjgsInByb2RNb2RGZWF0dXJlSUQiOjgxLCJwcm9kTW9kRmVhdHVyZVR5cGUiOiJDSElMRF9NWUFOQUxZU0lTMSIsImRlZmF1bHRGZWF0dXJlIjoiMCIsInByb2RNb2RGZWF0ckNvZGUiOiJEQVNIQk9BUkRDSEFSVFMxIiwicm9sZUlkIjoxLCJwcm9kdWN0TW9kdWxlU3ViRmVhdHVyZXMiOm51bGx9LHsicHJvZENvZGUiOiJTQVdEMDAwMDAxIiwicHJvZE1vZENvZGUiOiJBTkxZUzAwMDAxIiwicHJvZE1vZEZlYXR1cmVOYW1lIjoiVGVzdCBSZXBvcnRzIiwicHJvZE1vZEZlYXR1cmVEZXNjIjoiVGVzdCBSZXBvcnRzIiwiZGVmYXVsdFVSTCI6Ii8iLCJwcml2aWxlZ2VDb2RlIjoxMjgsInByb2RNb2RGZWF0dXJlSUQiOjEwMywicHJvZE1vZEZlYXR1cmVUeXBlIjoiQ0hJTERfTVlBTkFMWVNJUzEiLCJkZWZhdWx0RmVhdHVyZSI6IjAiLCJwcm9kTW9kRmVhdHJDb2RlIjoiVEVTVFJFUE9SVFMxIiwicm9sZUlkIjoxLCJwcm9kdWN0TW9kdWxlU3ViRmVhdHVyZXMiOm51bGx9LHsicHJvZENvZGUiOiJTQVdEMDAwMDAxIiwicHJvZE1vZENvZGUiOiJBTkxZUzAwMDAxIiwicHJvZE1vZEZlYXR1cmVOYW1lIjoiS09ESUFLIiwicHJvZE1vZEZlYXR1cmVEZXNjIjoiS09ESUFLIEFUVCBWWlcgUmVwb3J0cyIsImRlZmF1bHRVUkwiOiIvIiwicHJpdmlsZWdlQ29kZSI6MTI4LCJwcm9kTW9kRmVhdHVyZUlEIjoxMTEsInByb2RNb2RGZWF0dXJlVHlwZSI6IkNISUxEX01ZQU5BTFlTSVMxIiwiZGVmYXVsdEZlYXR1cmUiOiIwIiwicHJvZE1vZEZlYXRyQ29kZSI6IktPRElBSzEiLCJyb2xlSWQiOjEsInByb2R1Y3RNb2R1bGVTdWJGZWF0dXJlcyI6bnVsbH0seyJwcm9kQ29kZSI6IlNBV0QwMDAwMDEiLCJwcm9kTW9kQ29kZSI6IkFOTFlTMDAwMDEiLCJwcm9kTW9kRmVhdHVyZU5hbWUiOiJTcHJpbnRSZXZpZXciLCJwcm9kTW9kRmVhdHVyZURlc2MiOiJTcHJpbnQgSmFuIDI1IFJldmlldyIsImRlZmF1bHRVUkwiOiIvIiwicHJpdmlsZWdlQ29kZSI6MTI4LCJwcm9kTW9kRmVhdHVyZUlEIjoxMTYsInByb2RNb2RGZWF0dXJlVHlwZSI6IkNISUxEX01ZQU5BTFlTSVMxIiwiZGVmYXVsdEZlYXR1cmUiOiIwIiwicHJvZE1vZEZlYXRyQ29kZSI6IlNQUklOVFJFVklFVzExIiwicm9sZUlkIjoxLCJwcm9kdWN0TW9kdWxlU3ViRmVhdHVyZXMiOm51bGx9XX0seyJwcm9kQ29kZSI6IlNBV0QwMDAwMDEiLCJwcm9kTW9kQ29kZSI6IkFOTFlTMDAwMDEiLCJwcm9kTW9kRmVhdHVyZU5hbWUiOiJBTkFMWVpFIERFTU9TIiwicHJvZE1vZEZlYXR1cmVEZXNjIjoiU2FtcGxlIGFuYWx5c2lzIGZvciBkZW1vIHB1cnBvc2VzIiwiZGVmYXVsdFVSTCI6Ii8iLCJwcml2aWxlZ2VDb2RlIjowLCJwcm9kTW9kRmVhdHVyZUlEIjoxMTksInByb2RNb2RGZWF0dXJlVHlwZSI6IlBBUkVOVF9BTkFMWVpFREVNT1MxMSIsImRlZmF1bHRGZWF0dXJlIjoiMCIsInByb2RNb2RGZWF0ckNvZGUiOiJBTkFMWVpFREVNT1MxMSIsInJvbGVJZCI6bnVsbCwicHJvZHVjdE1vZHVsZVN1YkZlYXR1cmVzIjpbeyJwcm9kQ29kZSI6IlNBV0QwMDAwMDEiLCJwcm9kTW9kQ29kZSI6IkFOTFlTMDAwMDEiLCJwcm9kTW9kRmVhdHVyZU5hbWUiOiJSZXBvcnRzIiwicHJvZE1vZEZlYXR1cmVEZXNjIjpudWxsLCJkZWZhdWx0VVJMIjoiLyIsInByaXZpbGVnZUNvZGUiOjEyOCwicHJvZE1vZEZlYXR1cmVJRCI6MTIwLCJwcm9kTW9kRmVhdHVyZVR5cGUiOiJDSElMRF9BTkFMWVpFREVNT1MxMSIsImRlZmF1bHRGZWF0dXJlIjoiMCIsInByb2RNb2RGZWF0ckNvZGUiOiJSRVBPUlRTMTEiLCJyb2xlSWQiOjEsInByb2R1Y3RNb2R1bGVTdWJGZWF0dXJlcyI6bnVsbH0seyJwcm9kQ29kZSI6IlNBV0QwMDAwMDEiLCJwcm9kTW9kQ29kZSI6IkFOTFlTMDAwMDEiLCJwcm9kTW9kRmVhdHVyZU5hbWUiOiJQaXZvdHMiLCJwcm9kTW9kRmVhdHVyZURlc2MiOm51bGwsImRlZmF1bHRVUkwiOiIvIiwicHJpdmlsZWdlQ29kZSI6MTI4LCJwcm9kTW9kRmVhdHVyZUlEIjoxMjEsInByb2RNb2RGZWF0dXJlVHlwZSI6IkNISUxEX0FOQUxZWkVERU1PUzExIiwiZGVmYXVsdEZlYXR1cmUiOiIwIiwicHJvZE1vZEZlYXRyQ29kZSI6IlBJVk9UUzExIiwicm9sZUlkIjoxLCJwcm9kdWN0TW9kdWxlU3ViRmVhdHVyZXMiOm51bGx9LHsicHJvZENvZGUiOiJTQVdEMDAwMDAxIiwicHJvZE1vZENvZGUiOiJBTkxZUzAwMDAxIiwicHJvZE1vZEZlYXR1cmVOYW1lIjoiQ2hhcnRzIiwicHJvZE1vZEZlYXR1cmVEZXNjIjpudWxsLCJkZWZhdWx0VVJMIjoiLyIsInByaXZpbGVnZUNvZGUiOjEyOCwicHJvZE1vZEZlYXR1cmVJRCI6MTIyLCJwcm9kTW9kRmVhdHVyZVR5cGUiOiJDSElMRF9BTkFMWVpFREVNT1MxMSIsImRlZmF1bHRGZWF0dXJlIjoiMCIsInByb2RNb2RGZWF0ckNvZGUiOiJDSEFSVFMxMSIsInJvbGVJZCI6MSwicHJvZHVjdE1vZHVsZVN1YkZlYXR1cmVzIjpudWxsfV19XX0seyJwcm9kQ29kZSI6IlNBV0QwMDAwMDEiLCJwcm9kdWN0TW9kTmFtZSI6Ik9CU0VSVkUiLCJwcm9kdWN0TW9kRGVzYyI6Ik9ic2VydmUgTW9kdWxlIiwicHJvZHVjdE1vZENvZGUiOiJPQlNSMDAwMDAxIiwicHJvZHVjdE1vZElEIjoiMiIsIm1vZHVsZVVSTCI6Ii8iLCJkZWZhdWx0TW9kIjoiMCIsInByaXZpbGVnZUNvZGUiOjEyOCwicHJvZE1vZEZlYXR1cmUiOlt7InByb2RDb2RlIjoiU0FXRDAwMDAwMSIsInByb2RNb2RDb2RlIjoiT0JTUjAwMDAwMSIsInByb2RNb2RGZWF0dXJlTmFtZSI6IlFBIFNhdmVkIERhc2hib3JhZCIsInByb2RNb2RGZWF0dXJlRGVzYyI6IlFBIFNhdmVkIERhc2hib3JhZCIsImRlZmF1bHRVUkwiOiIvIiwicHJpdmlsZWdlQ29kZSI6MCwicHJvZE1vZEZlYXR1cmVJRCI6MTA2LCJwcm9kTW9kRmVhdHVyZVR5cGUiOiJQQVJFTlRfUUFTQVZFRERBU0hCT1JBRDEiLCJkZWZhdWx0RmVhdHVyZSI6IjAiLCJwcm9kTW9kRmVhdHJDb2RlIjoiUUFTQVZFRERBU0hCT1JBRDEiLCJyb2xlSWQiOm51bGwsInByb2R1Y3RNb2R1bGVTdWJGZWF0dXJlcyI6W3sicHJvZENvZGUiOiJTQVdEMDAwMDAxIiwicHJvZE1vZENvZGUiOiJPQlNSMDAwMDAxIiwicHJvZE1vZEZlYXR1cmVOYW1lIjoiUUEgVXNlQ2FzZXMiLCJwcm9kTW9kRmVhdHVyZURlc2MiOiJRQSBVc2VDYXNlcyIsImRlZmF1bHRVUkwiOiIvIiwicHJpdmlsZWdlQ29kZSI6MTI4LCJwcm9kTW9kRmVhdHVyZUlEIjoxMDcsInByb2RNb2RGZWF0dXJlVHlwZSI6IkNISUxEX1FBU0FWRUREQVNIQk9SQUQxIiwiZGVmYXVsdEZlYXR1cmUiOiIwIiwicHJvZE1vZEZlYXRyQ29kZSI6IlFBVVNFQ0FTRVMxIiwicm9sZUlkIjoxLCJwcm9kdWN0TW9kdWxlU3ViRmVhdHVyZXMiOm51bGx9LHsicHJvZENvZGUiOiJTQVdEMDAwMDAxIiwicHJvZE1vZENvZGUiOiJPQlNSMDAwMDAxIiwicHJvZE1vZEZlYXR1cmVOYW1lIjoiUUEgQW5hbHlzaXMiLCJwcm9kTW9kRmVhdHVyZURlc2MiOiJRQSBBbmFseXNpcyIsImRlZmF1bHRVUkwiOiIvIiwicHJpdmlsZWdlQ29kZSI6NDkxNTIsInByb2RNb2RGZWF0dXJlSUQiOjEwOCwicHJvZE1vZEZlYXR1cmVUeXBlIjoiQ0hJTERfUUFTQVZFRERBU0hCT1JBRDEiLCJkZWZhdWx0RmVhdHVyZSI6IjAiLCJwcm9kTW9kRmVhdHJDb2RlIjoiUUFBTkFMWVNJUzEiLCJyb2xlSWQiOjEsInByb2R1Y3RNb2R1bGVTdWJGZWF0dXJlcyI6bnVsbH1dfSx7InByb2RDb2RlIjoiU0FXRDAwMDAwMSIsInByb2RNb2RDb2RlIjoiT0JTUjAwMDAwMSIsInByb2RNb2RGZWF0dXJlTmFtZSI6Ik15IERhc2hib2FyZHMiLCJwcm9kTW9kRmVhdHVyZURlc2MiOiJNeSBEYXNoYm9hcmRzIiwiZGVmYXVsdFVSTCI6Ii8iLCJwcml2aWxlZ2VDb2RlIjowLCJwcm9kTW9kRmVhdHVyZUlEIjoxMTIsInByb2RNb2RGZWF0dXJlVHlwZSI6IlBBUkVOVF9NWURBU0hCT0FSRFMyMSIsImRlZmF1bHRGZWF0dXJlIjoiMCIsInByb2RNb2RGZWF0ckNvZGUiOiJNWURBU0hCT0FSRFMyMSIsInJvbGVJZCI6bnVsbCwicHJvZHVjdE1vZHVsZVN1YkZlYXR1cmVzIjpbeyJwcm9kQ29kZSI6IlNBV0QwMDAwMDEiLCJwcm9kTW9kQ29kZSI6Ik9CU1IwMDAwMDEiLCJwcm9kTW9kRmVhdHVyZU5hbWUiOiJEcmFmdHMiLCJwcm9kTW9kRmVhdHVyZURlc2MiOiJEcmFmdHMiLCJkZWZhdWx0VVJMIjoiLyIsInByaXZpbGVnZUNvZGUiOjEyOCwicHJvZE1vZEZlYXR1cmVJRCI6MTEzLCJwcm9kTW9kRmVhdHVyZVR5cGUiOiJDSElMRF9NWURBU0hCT0FSRFMyMSIsImRlZmF1bHRGZWF0dXJlIjoiMCIsInByb2RNb2RGZWF0ckNvZGUiOiJEUkFGVFMyMSIsInJvbGVJZCI6MSwicHJvZHVjdE1vZHVsZVN1YkZlYXR1cmVzIjpudWxsfSx7InByb2RDb2RlIjoiU0FXRDAwMDAwMSIsInByb2RNb2RDb2RlIjoiT0JTUjAwMDAwMSIsInByb2RNb2RGZWF0dXJlTmFtZSI6IktPRElBSyIsInByb2RNb2RGZWF0dXJlRGVzYyI6IktPRElBSyBEQVNIQk9BUkRTIiwiZGVmYXVsdFVSTCI6Ii8iLCJwcml2aWxlZ2VDb2RlIjoxMjgsInByb2RNb2RGZWF0dXJlSUQiOjExNSwicHJvZE1vZEZlYXR1cmVUeXBlIjoiQ0hJTERfTVlEQVNIQk9BUkRTMjEiLCJkZWZhdWx0RmVhdHVyZSI6IjAiLCJwcm9kTW9kRmVhdHJDb2RlIjoiS09ESUFLMjEiLCJyb2xlSWQiOjEsInByb2R1Y3RNb2R1bGVTdWJGZWF0dXJlcyI6bnVsbH1dfV19XX1dLCJ2YWxpZFVwdG8iOjE1MjYwNDEyMjExNTYsInZhbGlkIjp0cnVlLCJ2YWxpZGl0eVJlYXNvbiI6IlVzZXIgQXV0aGVudGljYXRlZCBTdWNjZXNzZnVsbHkiLCJ2YWxpZE1pbnMiOm51bGx9LCJpYXQiOjE1MjYwMzQwMjF9.1eeFCStzOnlCiTTb_p8W1dxkXk53u9rnWBnyZdhDTOc';
  }

  getCategories(moduleName = 'ANALYZE') {
    const token = this.getTokenObj();
    const analyzeModule = find(
      get(token, 'ticket.products[0].productModules'),
      mod => mod.productModName === moduleName
    );

    return get(analyzeModule, 'prodModFeature', []) || [];
  }

  getAccessToken() {
    return this.get();
  }

  getRefreshToken() {
    return window.localStorage[this._refreshTokenKey];
  }

  validity() {
    return new Date(this.getTokenObj().ticket.validUpto);
  }

  destroy() {
    window.localStorage.removeItem(this.jwtKey);
    window.localStorage.removeItem(this._refreshTokenKey);
  }

  parseJWT(jwt) {
    if (!jwt) {
      return null;
    }
    const base64Url = jwt.split('.')[1];
    const base64 = base64Url.replace('-', '+').replace('_', '/');
    return angular.fromJson(window.atob(base64));
  }

  /* Returs the parsed json object from the jwt token */
  getTokenObj() {
    
    const token = this.get();

    if (!token) {
      return null;
    }
    return this.parseJWT(this.get());
  }

  get customerCode() {
    const token = this.getTokenObj();
    if (!token) {
      return '';
    }

    return get(token, 'ticket.custCode', 'ATT');
  }

  isValid(token) {
    return (
      get(token, 'ticket.valid', false) &&
      get(token, 'ticket.validUpto', 0) >= Date.now()
    );
  }

  /* Bootstraps request structure with necessary auth data */
  getRequestParams() {
    const token = this.getTokenObj();
    return {
      contents: {
        keys: [
          {
            customerCode: get(token, 'ticket.custCode', 'ATT')
            // dataSecurityKey: get(token, 'ticket.dataSecurityKey')
          }
        ]
      }
    };
  }

  getValidityReason(token = this.getTokenObj()) {
    return token.ticket.validityReason;
  }

  getUserId() {
    return get(this.getTokenObj(), 'ticket.userId').toString();
  }

  _isRole(token, role) {
    const roleType = get(token, 'ticket.roleType');
    return roleType === role;
  }

  isAdmin(token) {
    return this._isRole(token, 'ADMIN');
  }

  isOwner(token, creatorId) {
    creatorId = creatorId || '';
    return creatorId.toString() === get(token, 'ticket.userId').toString();
  }

  _isSet(code, bitIndex) {
    const fullCode = padStart(
      (code >>> 0).toString(2),
      PRIVILEGE_CODE_LENGTH,
      '0'
    );
    /* If index of 'All' privileges is set, it is considered same as if the
       requested privilege bit is set */
    return fullCode[bitIndex] === '1' || fullCode[PRIVILEGE_INDEX.ALL] === '1';
  }

  /* This is the umbrella method for checking privileges on different features
     @name String
     @opts Object

     @opts should have either categoryId or subCategoryId field set.
     */
  hasPrivilege(name, opts) {
    /* eslint-disable */
    if (!has(PRIVILEGE_INDEX, name)) {
      throw new Error(`Privilige ${name} is not supported!`);
    }
    opts.module = opts.module || 'ANALYZE';

    const token = this.getTokenObj();
    const module =
      find(
        get(token, 'ticket.products.[0].productModules'),
        module => module.productModName === opts.module
      ) || [];

    const code = this.getCode(opts, module);

    /* prettier-ignore */
    switch (name) {
    case 'ACCESS':
      return this._isSet(code, PRIVILEGE_INDEX.ACCESS);
    case 'CREATE':
      return this._isSet(code, PRIVILEGE_INDEX.CREATE);
    case 'EXECUTE':
      return this._isSet(code, PRIVILEGE_INDEX.EXECUTE);
    case 'PUBLISH':
      return this._isSet(code, PRIVILEGE_INDEX.PUBLISH);
    case 'FORK':
      return this._isSet(code, PRIVILEGE_INDEX.FORK);
    case 'EDIT':
      return (
        this._isSet(code, PRIVILEGE_INDEX.EDIT) ||
        (this.isOwner(token, opts.creatorId) || this.isAdmin(token))
      );
    case 'EXPORT':
      return this._isSet(code, PRIVILEGE_INDEX.EXPORT);
    case 'DELETE':
      return (
        this._isSet(code, PRIVILEGE_INDEX.DELETE) ||
        (this.isOwner(token, opts.creatorId) || this.isAdmin(token))
      );
    default:
      return false;
    }
    /* eslint-enable */
  }

  getCode(opts, module) {
    if (opts.subCategoryId) {
      const subCategories = flatMap(
        module.prodModFeature,
        feature => feature.productModuleSubFeatures
      );
      const subCategory =
        find(
          subCategories,
          subFeature =>
            subFeature.prodModFeatureID.toString() ===
            opts.subCategoryId.toString()
        ) || {};

      return subCategory.privilegeCode || 0;
    }

    if (opts.categoryId) {
      const category =
        find(
          module.prodModFeature,
          feature =>
            feature.prodModFeatureID.toString() === opts.categoryId.toString()
        ) || {};

      return category.privilegeCode || 0;
    }
    // No privilege
    return 0;
  }
}
