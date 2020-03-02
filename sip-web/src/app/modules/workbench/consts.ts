export {
  DATAPOD_CATEGORIES,
  DATASET_CATEGORIES_TYPE
} from '../../common/consts';

/**
 * User should be able to add underscore in DS name while adding datasets in workbench module.
 * Added as part of SIP-9001.
 */
export const DS_NAME_PATTERN = '[A-Za-z0-9_]+';

export const DS_NAME_PATTERN_HINT_ERROR =
  'Only underscore, alphabets and numbers are allowed.';
