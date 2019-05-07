import { State } from '@ngxs/store';
import { ExportPageState } from '../export';
import { AdminImportPageState } from '../import';

@State<{}>({
  name: 'admin',
  children: [ExportPageState, AdminImportPageState]
})
export class AdminState {}
