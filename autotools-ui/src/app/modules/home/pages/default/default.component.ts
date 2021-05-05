import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { JobService } from '../../../../core/services/job.service';
import { ScheduledJob } from '../../../../core/domain/modules';

@Component({
  selector: 'app-default',
  templateUrl: './default.component.html',
  styleUrls: ['./default.component.scss'],
})
export class DefaultComponent implements OnInit, OnDestroy {
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  displayedColumns: string[] = ['name', 'type', 'source', 'target', 'enabled', 'actions'];

  dataSource = new MatTableDataSource();

  subscriptions: Subscription[] = [];

  constructor(private router: Router, private jobService: JobService) {}

  ngOnInit(): void {
    this.subscriptions.push(
      this.jobService.findAll().subscribe((root: ScheduledJob[]) => {
        this.dataSource.data = root;
        this.dataSource.sort = this.sort;
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe());
  }

  addJob(): void {
    this.jobService.create();
  }

  editRule(scheduledJob: ScheduledJob): void {
    this.jobService.update(scheduledJob);
  }

  deleteRule(scheduledJob: ScheduledJob): void {
    this.jobService.delete(scheduledJob);
  }
}
