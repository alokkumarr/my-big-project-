import * as split from 'lodash/split';
import * as every from 'lodash/every';
import * as last from 'lodash/last';
import * as filter from 'lodash/filter';
import * as reject from 'lodash/reject';
import * as join from 'lodash/join';
import * as fpJoin from 'lodash/fp/join';
import * as map from 'lodash/map';
import * as toNumber from 'lodash/toNumber';
import * as isNaN from 'lodash/isNaN';
import * as dropRight from 'lodash/dropRight';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpReverse from 'lodash/fp/reverse';
import * as reduce from 'lodash/reduce';

const timeBlockLimitsMap = new Map();
// week
timeBlockLimitsMap.set('w', Infinity);
// day
timeBlockLimitsMap.set('d', 7);
// hour
timeBlockLimitsMap.set('h', 24);
// minute
timeBlockLimitsMap.set('m', 60);

interface TimeBlock {
  identifier: 'w' | 'd' | 'h' | 'm';
  value: number;
  carry: number;
  limit: number;
}
/**
 * Return the time interval string corrected, if it is not a valid time interval string, return empty string
 * ex:
 * correctTimeInterval('2w 3d') -> '2w 3d'
 * correctTimeInterval('2w 16d') -> '5w 2d'
 * correctTimeInterval('2w sgsd') -> '2w'
 * correctTimeInterval('2w 16.3d') -> '2w'
 * correctTimeInterval('2w 16 3d') -> '2w 3d'
 */
export function correctTimeInterval(str: string) {
  const individulaBlocks = split(str, ' ');
  const validBlocks = filter(individulaBlocks, checkTimeIntervalBlock);
  if (validBlocks.length > 1) {
    const blocksMap = getBlocksObject(validBlocks);
    return fpPipe(
      m => Array.from(m.entries()),
      entries =>
        map(entries, ([identifier, limit]) => {
          const value = blocksMap[identifier] || 0;
          const limitedValue = value % limit;
          const carry = Math.trunc(value / limit);
          const timeBlock: TimeBlock = {
            identifier,
            limit,
            value: limitedValue,
            carry
          };
          return timeBlock;
        }),
      carryOverflows,
      blocks => reject(blocks, ({ value }) => value === 0),
      blocks =>
        map(
          blocks,
          ({ value, identifier }: TimeBlock) => `${value}${identifier}`
        ),
      fpJoin(' ')
    )(timeBlockLimitsMap);
  }
  return join(validBlocks, ' ');
}

/**
 * Generate a map frowm the list time blocks, values with the same identifiers should be added
 */
function getBlocksObject(blocks) {
  return reduce(
    blocks,
    (accumulator, block) => {
      const identifier = last(block);
      const value = toNumber(join(dropRight(block), ''));
      const valueAlreadyInAccumulator = accumulator[identifier];
      if (valueAlreadyInAccumulator) {
        accumulator[identifier] = value + valueAlreadyInAccumulator;
      } else {
        accumulator[identifier] = value;
      }
      return accumulator;
    },
    {}
  );
}

/**
 * Return the timeInterval block if it's correct, if not, return an empty string
 */
function checkTimeIntervalBlock(block: string) {
  const identifier = last(block);
  const isValueValid = areAllCharactersNumbers(dropRight(block));
  const isIdentifierValid = timeBlockLimitsMap.has(identifier);
  return isIdentifierValid && isValueValid;
}

function areAllCharactersNumbers(arrayOfCharacters: string[]) {
  return every(arrayOfCharacters, char => !isNaN(toNumber(char)));
}

function carryOverflows(blocks: TimeBlock[]) {
  return fpPipe(
    fpReverse,
    blks => {
      const length = blks.length;
      for (let i = 1; i < length; i++) {
        const { carry: carryFromPrevious }: TimeBlock = blks[i - 1];
        const block: TimeBlock = blks[i];
        const { value, limit, carry } = block;
        const valueWithCarry = carryFromPrevious + value;
        const newValue = valueWithCarry % limit;
        const newCarry = Math.trunc(valueWithCarry / limit);
        block.value = newValue;
        block.carry = carry + newCarry;
      }
      return blks;
    },
    fpReverse
  )(blocks);
}
