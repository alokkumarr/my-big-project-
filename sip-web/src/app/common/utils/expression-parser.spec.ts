import { ExpressionParser, ExpressionError } from './expression-parser';

const expressionExamples: any[] = [
  {
    expression: '(SUM(column_name1) + AVG(column_name2)) * SUM(column_name3)',
    json: {
      operator: '*',
      operand1: {
        operator: '+',
        operand1: { aggregate: 'SUM', column: 'column_name1' },
        operand2: { aggregate: 'AVG', column: 'column_name2' }
      },
      operand2: {
        aggregate: 'SUM',
        column: 'column_name3'
      }
    }
  },

  {
    expression: '1',
    json: {
      value: 1
    }
  },
  {
    expression: 'SUM(column_name)',
    json: {
      aggregate: 'SUM',
      column: 'column_name'
    }
  },
  {
    expression: 'SUM(column_name) / 100',
    json: {
      operator: '/',
      operand1: {
        aggregate: 'SUM',
        column: 'column_name'
      },
      operand2: {
        value: 100
      }
    }
  }
];

const faultyExpressionStrings: string[] = [
  'SUM(', // unterminated parenthesis
  '1 +', // missing second operand
  'STUPEFY(column_name)', // unsupported aggregate function
  'column_name' // no aggregate applied
];

const faultyExpressionJSON: any[] = [
  {
    // Unsupported operator
    operator: '~/',
    operand1: {
      value: 1
    },
    operand2: {
      value: 2
    }
  },
  {
    // Missing second operand
    operator: '+',
    operand1: {
      value: 1
    }
  },
  {
    // Unsupported aggregate function
    aggregate: 'STUPEFY',
    column: 'column_name'
  }
];

describe('Expression Parser', () => {
  let parser: ExpressionParser;
  beforeEach(() => {
    parser = new ExpressionParser();
  });

  describe('parseExpression', () => {
    it('should parse complex expression', () => {
      expressionExamples.forEach(({ expression, json }, i) => {
        expect(parser.parseExpression(expression)).toEqual(
          json,
          `Example at index ${i} failed.`
        );
      });
    });

    it('should throw error for invalid expression string', () => {
      faultyExpressionStrings.forEach(expr => {
        expect(() => parser.parseExpression(expr)).toThrowError(
          ExpressionError
        );
      });
    });
  });

  describe('parseJSON', () => {
    it('should throw error for invalid expression json', () => {
      faultyExpressionJSON.forEach(expr => {
        expect(() => parser.parseJSON(expr)).toThrowError(ExpressionError);
      });
    });

    it('should convert expression json back to string', () => {
      expressionExamples.forEach(({ expression, json }, i) => {
        expect(parser.parseJSON(json)).toEqual(
          expression,
          `Example at index ${i} failed.`
        );
      });
    });
  });
});
