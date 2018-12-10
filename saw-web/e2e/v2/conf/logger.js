'use-strict'
const {createLogger, format,transports} = require('winston');
const colorizer = format.colorize();
const Constants = require('../helpers/Constants')

let level = process.env.LOGGER_LEVEL || 'info';
level = (level in Constants.LOG_LEVELS) ? level : 'info';

module.exports = (tag)=>createLogger({
  level:level,
  format: format.combine(
    format.timestamp(),
   format.simple(),
    format.printf(msg =>
      colorizer.colorize(msg.level, `${msg.timestamp} - ${msg.level.toUpperCase()}: ${(tag)?tag:''}: ${msg.message}`)
    )
  ),
  transports: [
    new transports.Console()
  ]
});
