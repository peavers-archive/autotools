export interface Option {
  backgroundColor: string;
  buttonColor: string;
  headingColor: string;
  label: string;
  value: string;
}

export interface ScheduledJob {
  id: number;
  enabled: boolean;
  name: string;
  jobType: string;
  delay: string;
  targetDirectory: string;
  sourceDirectory: string;
  lessThanThreshold: number;
  greaterThanThreshold: number;
  ignoreWords: string;
  deleteByName: string;
}
