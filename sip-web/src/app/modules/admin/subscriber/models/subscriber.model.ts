export enum SubscriberChannelType {
  EMAIL = 'email'
}

export interface SIPSubscriber {
  id: string;
  subscriberName: string;
  channelType: SubscriberChannelType;
  channelValue: string;
}
