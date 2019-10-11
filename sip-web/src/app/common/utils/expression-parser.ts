import * as mjs from 'mathjs';
import { MathNode, parse } from 'mathjs';
import * as isNil from 'lodash/isNil';
import * as isEmpty from 'lodash/isEmpty';
import * as lowerCase from 'lodash/lowerCase';

/*
 ████████╗██╗   ██╗██████╗ ███████╗███████╗
 ╚══██╔══╝╚██╗ ██╔╝██╔══██╗██╔════╝██╔════╝
    ██║    ╚████╔╝ ██████╔╝█████╗  ███████╗
    ██║     ╚██╔╝  ██╔═══╝ ██╔══╝  ╚════██║
    ██║      ██║   ██║     ███████╗███████║
    ╚═╝      ╚═╝   ╚═╝     ╚══════╝╚══════╝
*/

/* Definitions for node constructors haven't been updated in @types/mathjs.
   Providing those definitions here.
*/
interface IMathJS extends mjs.MathJsStatic {
  readonly ConstantNode: new (value: number) => MathNode;
  readonly FunctionNode: new (
    fn: MathNode | string,
    args: MathNode[]
  ) => MathNode;
  readonly OperatorNode: new (
    op: string,
    fn: string,
    args: MathNode[]
  ) => MathNode;
  readonly ParenthesisNode: new (content: MathNode) => MathNode;
  readonly SymbolNode: new (name: string) => MathNode;
}

const { SymbolNode, OperatorNode, ConstantNode, FunctionNode } = mjs as IMathJS;

export enum Operator {
  Add = '+',
  Subtract = '-',
  Multiply = '*',
  Divide = '/'
}

interface ConstantExpression {
  value: number;
}

interface ColumnExpression {
  aggregate?: string;
  column: string;
}

interface OperatorExpression {
  operator: Operator;
  operand1: Expression;
  operand2: Expression;
}

type Expression = OperatorExpression | ColumnExpression | ConstantExpression;

export enum ExpressionErrorType {
  StringParsingFailed,
  JsonParsingFailed
}
export class ExpressionError extends Error {
  constructor(public type: ExpressionErrorType, public message: string) {
    super(message);
    Object.setPrototypeOf(this, new.target.prototype);
  }

  toString() {
    return this.message;
  }
}

export const SUPPORTED_AGGREGATES = ['SUM', 'AVG'];

export class ExpressionParser {
  private supportedColumns: string[]; // used to validate column names in expression

  constructor(supportedColumns: string[] = null) {
    if (!isEmpty(supportedColumns)) {
      this.supportedColumns = supportedColumns.map(colName =>
        lowerCase(colName)
      );
    }
  }
  /*
 ██╗   ██╗ █████╗ ██╗     ██╗██████╗  █████╗ ████████╗ ██████╗ ██████╗ ███████╗
 ██║   ██║██╔══██╗██║     ██║██╔══██╗██╔══██╗╚══██╔══╝██╔═══██╗██╔══██╗██╔════╝
 ██║   ██║███████║██║     ██║██║  ██║███████║   ██║   ██║   ██║██████╔╝███████╗
 ╚██╗ ██╔╝██╔══██║██║     ██║██║  ██║██╔══██║   ██║   ██║   ██║██╔══██╗╚════██║
  ╚████╔╝ ██║  ██║███████╗██║██████╔╝██║  ██║   ██║   ╚██████╔╝██║  ██║███████║
   ╚═══╝  ╚═╝  ╚═╝╚══════╝╚═╝╚═════╝ ╚═╝  ╚═╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝╚══════╝
*/

  validateOperator(operator: MathNode) {
    if (!Object.values(Operator).includes(<Operator>operator.op)) {
      throw new ExpressionError(
        ExpressionErrorType.StringParsingFailed,
        `Operator not supported: ${operator.op}`
      );
    }
  }

  validateLoneColumn(columnNode: MathNode) {
    throw new ExpressionError(
      ExpressionErrorType.StringParsingFailed,
      `No aggregate applied to column: ${columnNode.name}.
    Example usage: ${SUPPORTED_AGGREGATES[0]}(${columnNode.name})`
    );
  }

  validateAggregate(aggregate: MathNode) {
    /* If no argument is provided for aggregate */
    if (isEmpty(aggregate.args)) {
      throw new ExpressionError(
        ExpressionErrorType.StringParsingFailed,
        `Missing column name for aggregate: ${aggregate.name}().
      Example usage: ${aggregate.name}(column_name)`
      );
    }

    /* If more than one argument is provided for aggregate */
    if (aggregate.args.length > 1) {
      throw new ExpressionError(
        ExpressionErrorType.StringParsingFailed,
        `Multiple arguments not supported in aggregate: ${aggregate.toString()}.
      Example usage: ${aggregate.name}(column_name)`
      );
    }

    /* If something other than a column name is provided. For example, SUM(a + b).
     Here, a + b is not a column name. It's an expression. Not supported.
  */
    if (aggregate.args[0].type !== 'SymbolNode') {
      throw new ExpressionError(
        ExpressionErrorType.StringParsingFailed,
        `Invalid arguments for aggregate: ${aggregate.name}.
      Replace '${aggregate.args[0].toString()}' with a column name.
      Example usage: ${aggregate.name}(column_name)`
      );
    }

    /* If aggregate is not one of the supported aggregates */
    const columnNode = aggregate.args[0];
    if (
      !SUPPORTED_AGGREGATES.some(agg => agg === aggregate.name.toUpperCase())
    ) {
      throw new ExpressionError(
        ExpressionErrorType.StringParsingFailed,
        `Aggregate not supported: ${aggregate.name} for column: ${columnNode.name}`
      );
    }

    /* If column is not supported */
    if (
      this.supportedColumns &&
      !this.supportedColumns.includes(lowerCase(columnNode.name))
    ) {
      throw new ExpressionError(
        ExpressionErrorType.StringParsingFailed,
        `Column with name '${columnNode.name}' does not exist.`
      );
    }
  }

  validateConstant(constant: MathNode) {
    if (typeof constant.value !== 'number' || isNil(constant.value)) {
      throw new ExpressionError(
        ExpressionErrorType.StringParsingFailed,
        `Invalid constant: ${constant.value}`
      );
    }
  }

  /*
  ██████╗ ██████╗ ███╗   ██╗██╗   ██╗███████╗██████╗ ████████╗███████╗██████╗ ███████╗
 ██╔════╝██╔═══██╗████╗  ██║██║   ██║██╔════╝██╔══██╗╚══██╔══╝██╔════╝██╔══██╗██╔════╝
 ██║     ██║   ██║██╔██╗ ██║██║   ██║█████╗  ██████╔╝   ██║   █████╗  ██████╔╝███████╗
 ██║     ██║   ██║██║╚██╗██║╚██╗ ██╔╝██╔══╝  ██╔══██╗   ██║   ██╔══╝  ██╔══██╗╚════██║
 ╚██████╗╚██████╔╝██║ ╚████║ ╚████╔╝ ███████╗██║  ██║   ██║   ███████╗██║  ██║███████║
  ╚═════╝ ╚═════╝ ╚═╝  ╚═══╝  ╚═══╝  ╚══════╝╚═╝  ╚═╝   ╚═╝   ╚══════╝╚═╝  ╚═╝╚══════╝
*/

  /**
   * Transforms the mathjs expression tree to sip
   * compatible expression tree.
   *
   * Expression Tree Reference: https://mathjs.org/docs/expressions/expression_trees.html
   *
   * @param {MathNode} node
   * @returns {Expression}
   */
  toJSON(node: MathNode): Expression {
    switch (node.type) {
      case 'OperatorNode':
        this.validateOperator(node);
        return {
          operator: <Operator>node.op,
          operand1: this.toJSON(node.args[0]),
          operand2: this.toJSON(node.args[1])
        };
      case 'ConstantNode':
        this.validateConstant(node);
        return { value: node.value };
      case 'FunctionNode':
        this.validateAggregate(node);
        return {
          aggregate: node.name,
          column: node.args[0].name
        };
      case 'SymbolNode':
        this.validateLoneColumn(node);
        return { column: node.name };
      case 'ParenthesisNode':
        return this.toJSON(node['content']);
    }
  }

  /**
   * Converts operator symbol to its function name. This function
   * name is used in mathjs to establish precedence. Without
   * function names, mathjs would consider + and * as just characters
   * and won't know how to apply precedence rules.
   * Function reference: https://mathjs.org/docs/reference/functions.html
   *
   * @param {string} operator
   * @returns {string}
   */
  operatorFunction(operator: Operator): string {
    switch (operator) {
      case Operator.Add:
        return 'add';
      case Operator.Subtract:
        return 'subtract';
      case Operator.Multiply:
        return 'multiply';
      case Operator.Divide:
        return 'divide';
      default:
        return '';
    }
  }

  /**
   * Converts a SIP compatible expression json to Mathjs
   * compatible expression tree.
   *
   * Expression Tree Reference: https://mathjs.org/docs/expressions/expression_trees.html
   *
   * @param {Expression} json
   * @returns {MathNode}
   */
  fromJSON(json: Expression): MathNode {
    if ((<OperatorExpression>json).operator) {
      /* If the json is an operator object */
      const operatorJSON = json as OperatorExpression;
      return new OperatorNode(
        operatorJSON.operator,
        this.operatorFunction(operatorJSON.operator),
        [
          this.fromJSON(operatorJSON.operand1),
          this.fromJSON(operatorJSON.operand2)
        ]
      );
    } else if ((<ColumnExpression>json).column) {
      /* If the json is a column object */
      const columnJSON = json as ColumnExpression;
      if (columnJSON.aggregate) {
        /* If the json is a column object with aggregate */
        return new FunctionNode(columnJSON.aggregate, [
          new SymbolNode(columnJSON.column)
        ]);
      } else {
        /* If the json is a column object without aggregate */
        return new SymbolNode(columnJSON.column);
      }
    } else {
      /* If json is a simple constant */
      const constantJSON = json as ConstantExpression;
      return new ConstantNode(constantJSON.value);
    }
  }

  /*
 ██████╗ ██╗   ██╗██████╗ ██╗     ██╗ ██████╗     █████╗ ██████╗ ██╗
 ██╔══██╗██║   ██║██╔══██╗██║     ██║██╔════╝    ██╔══██╗██╔══██╗██║
 ██████╔╝██║   ██║██████╔╝██║     ██║██║         ███████║██████╔╝██║
 ██╔═══╝ ██║   ██║██╔══██╗██║     ██║██║         ██╔══██║██╔═══╝ ██║
 ██║     ╚██████╔╝██████╔╝███████╗██║╚██████╗    ██║  ██║██║     ██║
 ╚═╝      ╚═════╝ ╚═════╝ ╚══════╝╚═╝ ╚═════╝    ╚═╝  ╚═╝╚═╝     ╚═╝
*/

  /**
   * Converts formula string to sip compatible expression json.
   *
   * @param {string} expr
   * @returns {Expression}
   */
  parseExpression(expr: string): Expression {
    try {
      return this.toJSON(parse(expr));
    } catch (e) {
      throw new ExpressionError(
        ExpressionErrorType.StringParsingFailed,
        e.message
      );
    }
  }

  /**
   * Converts sip compatible expression json to formula string.
   *
   * @param {Expression} json
   * @returns {string}
   */
  parseJSON(json: Expression): string {
    try {
      const expression = this.fromJSON(json).toString();
      this.parseExpression(expression); // try to parse the expression again to make sure it's valid
      return expression;
    } catch (e) {
      throw new ExpressionError(
        ExpressionErrorType.JsonParsingFailed,
        e.message
      );
    }
  }
}
