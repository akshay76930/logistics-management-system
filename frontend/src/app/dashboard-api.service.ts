import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { DashboardRecord, DashboardSummary, LoginResponse } from './dashboard.models';

const TOKEN_KEY = 'logistics-dashboard-token';

@Injectable({ providedIn: 'root' })
export class DashboardApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api';

  get hasToken(): boolean {
    return localStorage.getItem(TOKEN_KEY) !== null;
  }

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.baseUrl}/auth/login`, { username, password })
      .pipe(
        catchError((error: HttpErrorResponse) =>
          throwError(() => new Error(this.getErrorMessage(error, 'Sign in failed.')))
        )
      );
  }

  saveToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token);
  }

  clearToken(): void {
    localStorage.removeItem(TOKEN_KEY);
  }

  getSummary(): Observable<DashboardSummary> {
    return this.getWithToken<DashboardSummary>('/dashboard/summary');
  }

  getAllRecords(): Observable<DashboardRecord[]> {
    return this.getWithToken<DashboardRecord[]>('/dashboard/details');
  }

  private getWithToken<T>(path: string): Observable<T> {
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) {
      return throwError(() => new Error('Sign in to load your dashboard.'));
    }

    return this.http
      .get<T>(`${this.baseUrl}${path}`, {
        headers: new HttpHeaders({ Authorization: `Bearer ${token}` }),
      })
      .pipe(
        catchError((error: HttpErrorResponse) => {
          if (error.status === 401) {
            this.clearToken();
            return throwError(() => new Error('Your session expired. Please sign in again.'));
          }
          return throwError(() => new Error(this.getErrorMessage(error, 'Could not load dashboard.')));
        })
      );
  }

  private getErrorMessage(error: HttpErrorResponse, fallback: string): string {
    if (error.status === 0) {
      return 'Cannot connect to the API. Make sure the Spring Boot backend is running.';
    }
    if (typeof error.error?.error === 'string') {
      return error.error.error;
    }
    if (typeof error.error?.message === 'string') {
      return error.error.message;
    }
    return fallback;
  }
}
