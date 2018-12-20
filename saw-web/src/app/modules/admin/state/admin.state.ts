import { State } from '@ngxs/store';
import { ExportPageState } from '../export';

@State<{}>({
  name: 'admin',
  children: [ExportPageState]
})
export class AdminState {}
