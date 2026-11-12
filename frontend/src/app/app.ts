import { DatePipe, DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize, forkJoin, Subscription } from 'rxjs';
import { DashboardApiService } from './dashboard-api.service';
import { DashboardRecord, DashboardSummary } from './dashboard.models';

@Component({
  selector: 'app-root',
  imports: [DatePipe, DecimalPipe, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit, OnDestroy {
  private readonly api = inject(DashboardApiService);
  private readonly changeDetector = inject(ChangeDetectorRef);
  private refreshSubscription?: Subscription;
  private completionSubscription?: Subscription;

  readonly dateToday = new Date();
  readonly avatarColors = ['#e9d9c9', '#d2e0d4', '#e7d3d5', '#d5d9ec', '#e9e2ca'];
  username = '';
  password = '';
  filter = '';
  loading = false;
  loginLoading = false;
  error = '';
  signedIn = this.api.hasToken;
  summary: DashboardSummary | null = null;
  records: DashboardRecord[] = [];
  selectedRecord: DashboardRecord | null = null;
  completingRecordId: number | null = null;
  loadingConfirmed = false;
  loadingError = '';

  ngOnInit(): void {
    if (this.signedIn) this.loadDashboard();
  }

  ngOnDestroy(): void {
    this.refreshSubscription?.unsubscribe();
    this.completionSubscription?.unsubscribe();
  }

  get filteredRecords(): DashboardRecord[] {
    const query = this.filter.trim().toLowerCase();
    if (!query) return this.records;
    return this.records.filter((record) =>
      [
        record.tally,
        record.vehicle,
        record.rps,
        record.assignedEmployee,
        record.dockName,
        record.vehicleStatus,
        record.recordStatus,
      ].some((value) => value?.toLowerCase().includes(query))
    );
  }

  get timeGreeting(): string {
    const hour = new Date().getHours();
    if (hour < 5) return 'Good night';
    if (hour < 12) return 'Good morning';
    if (hour < 17) return 'Good afternoon';
    if (hour < 21) return 'Good evening';
    return 'Good night';
  }

  signIn(event: Event): void {
    event.preventDefault();
    this.error = '';
    this.loginLoading = true;
    this.api
      .login(this.username.trim(), this.password)
      .pipe(
        finalize(() => {
          this.loginLoading = false;
          this.changeDetector.markForCheck();
        })
      )
      .subscribe({
        next: (response) => {
          this.api.saveToken(response.accessToken);
          this.password = '';
          this.signedIn = true;
          this.loadDashboard();
        },
        error: (error: Error) => (this.error = error.message),
      });
  }

  signOut(): void {
    this.completionSubscription?.unsubscribe();
    this.completionSubscription = undefined;
    this.api.clearToken();
    this.signedIn = false;
    this.summary = null;
    this.records = [];
    this.selectedRecord = null;
    this.completingRecordId = null;
    this.loadingConfirmed = false;
    this.loadingError = '';
    this.error = '';
  }

  openLoadingProcess(record: DashboardRecord): void {
    if (record.recordStatus === 'COMPLETED') return;
    this.selectedRecord = record;
    this.loadingConfirmed = false;
    this.loadingError = '';
  }

  closeLoadingProcess(): void {
    if (this.completingRecordId !== null) return;
    this.selectedRecord = null;
    this.loadingConfirmed = false;
    this.loadingError = '';
  }

  markComplete(): void {
    const record = this.selectedRecord;
    if (!record || !this.loadingConfirmed || this.completingRecordId !== null) return;

    this.completingRecordId = record.id;
    this.loadingError = '';
    this.completionSubscription = this.api
      .markComplete(record.id)
      .pipe(
        finalize(() => {
          this.completingRecordId = null;
          this.completionSubscription = undefined;
          this.changeDetector.markForCheck();
        })
      )
      .subscribe({
        next: () => {
          this.selectedRecord = null;
          this.loadingConfirmed = false;
          this.loadDashboard();
        },
        error: (error: Error | HttpErrorResponse) => {
          this.loadingError = error.message || 'Could not complete the loading process.';
          if (this.loadingError.includes('sign in again')) {
            this.signedIn = false;
            this.summary = null;
            this.records = [];
            this.selectedRecord = null;
            this.loadingConfirmed = false;
          }
        },
      });
  }

  loadDashboard(): void {
    this.refreshSubscription?.unsubscribe();
    this.loading = true;
    this.error = '';
    this.refreshSubscription = forkJoin({
      summary: this.api.getSummary(),
      records: this.api.getAllRecords(),
    })
      .pipe(
        finalize(() => {
          this.loading = false;
          this.changeDetector.markForCheck();
        })
      )
      .subscribe({
        next: ({ summary, records }) => {
          this.summary = summary;
          this.records = records;
        },
        error: (error: Error | HttpErrorResponse) => {
          this.error = error.message || 'Could not load dashboard data.';
          if (this.error.includes('sign in again')) {
            this.signedIn = false;
            this.summary = null;
            this.records = [];
          }
        },
      });
  }

  statusClass(status: string | null): string {
    return `status-pill ${((status ?? 'unknown').toLowerCase().replace(/[\s_]+/g, '-'))}`;
  }

  trackById(_index: number, record: DashboardRecord): number {
    return record.id;
  }
}
