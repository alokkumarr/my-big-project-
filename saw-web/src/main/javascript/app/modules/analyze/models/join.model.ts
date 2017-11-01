import JoinCriterion from './join-criterion.model';

export default interface Join {
  criteria: JoinCriterion[];
  type:     string;
}
