import { TruncatePipe } from './truncate.pipe';

describe('Truncate Pipe', () => {
  const pipe: TruncatePipe = new TruncatePipe();

  it('should truncate strings greater than given length', () => {
    expect(pipe.transform('abcdefghi', 5)).toEqual('abcde...');
  });

  it('should not truncate strings shorter than given length', () => {
    expect(pipe.transform('abcdefghi', 100)).toEqual('abcdefghi');
  });

  it('should be able to handle non-string input values', () => {
    expect(pipe.transform('', 100)).toEqual('');
    expect(pipe.transform(6 as any, 100)).toEqual('6');
    expect(pipe.transform({} as any, 100)).toEqual('[object Object]');
    expect(pipe.transform(null, 100)).toEqual('');
    expect(pipe.transform(undefined, 100)).toEqual('');
  });

  it('should be able to handle invalid length', () => {
    expect(pipe.transform('abcdefghi', 0)).toEqual('...');
    expect(pipe.transform('abcdefghi', -4)).toEqual('...');
    expect(pipe.transform('abcdefghi', 'abc' as any)).toEqual('...');
    expect(pipe.transform('abcdefghi', {} as any)).toEqual('...');
    expect(pipe.transform('abcdefghi', null as any)).toEqual('...');
    expect(pipe.transform('abcdefghi', undefined as any)).toEqual('abcdefghi'); // uses the default value of length
  });
});
