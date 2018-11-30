import { Pipe, PipeTransform } from '@angular/core';
import * as filter from 'lodash/filter';
import * as map from 'lodash/map';

@Pipe({
  name: 'checkedArtifactColumnFilter'
})
export class CheckedArtifactColumnFilterPipe implements PipeTransform {
  transform(artifacts: any): any {
    return map(artifacts, artifact => {
      const columns = filter(artifact.columns, 'checked');
      return {
        ...artifact,
        columns
      };
    });
  }
}
