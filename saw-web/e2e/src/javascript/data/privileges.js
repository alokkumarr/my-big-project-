const privileges = {
  all: {
    privilegeCode: 128,
    privilegeDesc: 'All'
  },
  create: {
    privilegeCode: 49152,
    privilegeDesc: 'View,Create'
  },
  edit: {
    privilegeCode: 33792,
    privilegeDesc: 'View,Edit'
  },
  fork: {
    privilegeCode: 34816,
    privilegeDesc: 'View,Fork'
  },
  execute: {
    privilegeCode: 40960,
    privilegeDesc: 'View,Execute'
  },
  publish: {
    privilegeCode: 36864,
    privilegeDesc: 'View,Publish'
  },
  export: {
    privilegeCode: 33280,
    privilegeDesc: 'View,Export'
  },
  delete: {
    privilegeCode: 33024,
    privilegeDesc: 'View,Delete'
  },
  multiple: {
    privilegeCode: 55296,
    privilegeDesc: 'View,Create,Publish,Fork'
  },
  noAccess: {
    privilegeCode: 0,
    privilegeDesc: 'No Access'
  },
  view: {
    privilegeCode: 32768,
    privilegeDesc: 'View'
  }
};

module.exports = privileges;
