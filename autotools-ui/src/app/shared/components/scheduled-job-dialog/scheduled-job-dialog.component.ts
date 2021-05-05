import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ScheduledJob } from '../../../core/domain/modules';

export interface ScheduledJobDialogData {
  scheduledJob: ScheduledJob;
}

@Component({
  selector: 'app-scheduled-job-dialog',
  templateUrl: './scheduled-job-dialog.component.html',
  styleUrls: ['./scheduled-job-dialog.component.scss'],
})
export class ScheduledJobDialogComponent {
  scheduledJob: ScheduledJob;

  customDelay: boolean;

  delays = [
    {
      name: '10 Seconds (Development)',
      value: 10000,
    },
    {
      name: '1 Hour',
      value: 3600000,
    },
    {
      name: '3 Hour',
      value: 10800000,
    },
    {
      name: '6 Hour',
      value: 21600000,
    },
    {
      name: '24 Hour',
      value: 86400000,
    },
    {
      name: 'Custom',
      value: '',
    },
  ];

  jobs = [
    {
      type: 'DELETE_FILES_BY_SIZE',
      name: 'Delete files by size',
    },
    {
      type: 'DELETE_FILES_BY_NAME',
      name: 'Delete files by name',
    },
    {
      type: 'DELETE_EMPTY_DIRECTORIES',
      name: 'Delete empty directories',
    },
    {
      type: 'COPY_MEDIA_FILES',
      name: 'Copy media files',
    },
    {
      type: 'DUPLICATE_MEDIA_ADVANCE',
      name: 'Find duplicate media (advance)',
    },
    {
      type: 'DUPLICATE_MEDIA_BASIC',
      name: 'Find duplicate media (basic)',
    },
    {
      type: 'UNRAR_FILES',
      name: 'Unrar files',
    },
  ];

  constructor(
    private dialogRef: MatDialogRef<ScheduledJobDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: ScheduledJobDialogData
  ) {
    this.scheduledJob = data.scheduledJob;
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onCustom(value): void {
    this.customDelay = value === '';
  }
}
