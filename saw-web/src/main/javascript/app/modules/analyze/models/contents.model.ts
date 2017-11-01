import Key from './key.model';
import Analyze from './analyze.model';

export interface Contents {
  analyze: Analyze[];
  action:  string;
  keys:    Key[];
}
