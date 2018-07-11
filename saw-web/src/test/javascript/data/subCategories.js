var appRoot = require('app-root-path');
const globalVariables = require(appRoot + '/src/test/javascript/helpers/globalVariables');

const description = 'Sub-category created for e2e testing';
const nullValue = 'NULL';

const subCategories = {
  all: {
    name: 'All ' + globalVariables.e2eId,
    description: description,
    id: nullValue
  },
  create: {
    name: 'Create ' + globalVariables.e2eId,
    description: description,
    id: nullValue
  },
  edit: {
    name: 'Edit ' + globalVariables.e2eId,
    description: description,
    id: nullValue
  },
  fork: {
    name: 'Fork ' + globalVariables.e2eId,
    description: description,
    id: nullValue
  },
  publish: {
    name: 'Publish ' + globalVariables.e2eId,
    description: description,
    id: nullValue
  },
  execute: {
    name: 'Execute ' + globalVariables.e2eId,
    description: description,
    id: nullValue
  },
  export: {
    name: 'Export ' + globalVariables.e2eId,
    description: description,
    id: nullValue
  },
  delete: {
    name: 'Delete ' + globalVariables.e2eId,
    description: description,
    id: nullValue
  },
  multiple: {
    name: 'Multiple ' + globalVariables.e2eId,
    description: description,
    id: nullValue
  },
  createAndFork: {
    name: 'Create and Fork ' + globalVariables.e2eId,
    description: description,
    id: nullValue
  },
  createAndExecute: {
    name: 'Create and Execute ' + globalVariables.e2eId,
    description: description,
    id: nullValue
  },
  createAndPublish: {
    name: 'Create and Publish ' + globalVariables.e2eId,
    description: description,
    id: nullValue
  },
  createAndExport: {
    name: 'Create and Export ' + globalVariables.e2eId,
    description: description,
    id: nullValue
  },
  createAndDelete: {
    name: 'Create and Delete ' + globalVariables.e2eId,
    description: description,
    id: nullValue
  },
  noPrivileges: {
    name: 'No Privileges ' + globalVariables.e2eId,
    description: description,
    id: nullValue
  },
  changePrivileges: {
    name: 'Change Privileges ' + globalVariables.e2eId,
    description: description,
    id: nullValue
  },
  view: {
    name: 'View ' + globalVariables.e2eId,
    description: description,
    id: nullValue
  },
  createAnalysis: {
    name: 'Create Analysis ' + globalVariables.e2eId,
    description: description,
    id: nullValue
  }
};

module.exports = subCategories;
