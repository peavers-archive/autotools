import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ScheduledJob } from '../domain/modules';
import { environment } from '../../../environments/environment';
import {
  ScheduledJobDialogComponent,
  ScheduledJobDialogData,
} from '../../shared/components/scheduled-job-dialog/scheduled-job-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class JobService {
  private readonly endpoint;

  private scheduledJobs$: BehaviorSubject<ScheduledJob[]> = new BehaviorSubject([]);

  constructor(private httpClient: HttpClient, private dialog: MatDialog, private snackbar: MatSnackBar) {
    this.endpoint = `${environment.api}/job`;

    this.httpClient
      .get<ScheduledJob[]>(`${this.endpoint}`)
      .subscribe((scheduledJob: ScheduledJob[]) => this.scheduledJobs$.next(scheduledJob));
  }

  findAll(): Observable<ScheduledJob[]> {
    return this.scheduledJobs$.asObservable();
  }

  create(): void {
    const data: ScheduledJobDialogData = {
      scheduledJob: {
        id: null,
        enabled: false,
        name: '',
        jobType: '',
        delay: '',
        targetDirectory: '',
        sourceDirectory: '',
        lessThanThreshold: null,
        greaterThanThreshold: null,
        ignoreWords: '',
        deleteByName: '',
      },
    };

    this.dialog
      .open(ScheduledJobDialogComponent, { data, disableClose: true })
      .afterClosed()
      .subscribe((scheduledJob: ScheduledJob) => {
        if (scheduledJob !== undefined) {
          this.save(scheduledJob);
        }
      });
  }

  update(scheduledJob: ScheduledJob): void {
    const data: ScheduledJobDialogData = {
      scheduledJob,
    };

    this.dialog
      .open(ScheduledJobDialogComponent, { data, disableClose: true })
      .afterClosed()
      .subscribe((updatedScheduledJob: ScheduledJob) => {
        if (updatedScheduledJob !== undefined) {
          this.save(updatedScheduledJob);
        }
      });
  }

  save(scheduledJob: ScheduledJob): void {
    this.httpClient.post<ScheduledJob>(this.endpoint, scheduledJob).subscribe((result) => {
      this.updateSubject(this.scheduledJobs$, scheduledJob);

      this.snackbar.open('Success');
    });
  }

  delete(scheduledJob: ScheduledJob): void {
    this.httpClient.request('delete', this.endpoint, { body: scheduledJob }).subscribe(() => {
      this.scheduledJobs$.next(this.scheduledJobs$.value.filter((s) => s.id !== scheduledJob.id));
      this.snackbar.open('Success');
    });
  }

  /**
   * Either update the object if its already in the Subject, otherwise add it to the front of the Subject.
   */
  private updateSubject(input: BehaviorSubject<ScheduledJob[]>, job: ScheduledJob): void {
    const index = input.value.findIndex((value: ScheduledJob) => value.id === job.id);

    if (index !== -1) {
      const data = input.value;

      data.forEach((item, i) => {
        if (item.id === job.id) {
          data[i] = job;
        }
      });

      input.next(data);
    } else {
      input.next([job, ...input.value]);
    }
  }
}
