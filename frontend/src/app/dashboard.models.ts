export interface DashboardSummary {
  total: number;
  pending: number;
  inProgress: number;
  completed: number;
}

export interface DashboardRecord {
  id: number;
  tally: string;
  vehicle: string;
  rps: string | null;
  weight: number | null;
  packages: number | null;
  assignedEmployee: string | null;
  dockName: string | null;
  vehicleStatus: string | null;
  recordStatus: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED';
  createdAt: string;
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresInSeconds: number;
}
