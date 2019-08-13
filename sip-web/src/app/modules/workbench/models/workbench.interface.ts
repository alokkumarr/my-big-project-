export interface DetailFormable {
  valid: boolean;
  value: any;
}

export enum CHANNEL_OPERATION {
  CREATE = 'create',
  UPDATE = 'update'
}

export enum CHANNEL_ACCESS {
  READ = 'R',
  READ_WRITE = 'RW'
}

export enum HTTP_METHODS {
  POST = 'POST',
  GET = 'GET',
  PUT = 'PUT',
  DELETE = 'DELETE',
  PATCH = 'PATCH'
}

export interface ChannelObject {
  createdBy: string;
  productCode: string;
  projectCode: string;
  customerCode: string;
  channelType: string;
  channelMetadata: string;
  status?: number;
}

export interface SFTPChannelMetadata {
  channelName: string;
  hostName: string;
  portNo: string;
  userName: string;
  password: string;
  description: string;
  accessType: string;
}
