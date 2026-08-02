export type Role = 'ROLE_ADMIN' | 'ROLE_MANAGER' | 'ROLE_MEMBER' | 'ROLE_GUEST';
export type UserStatus = 'ACTIVE' | 'SUSPENDED' | 'LOCKED' | 'PENDING_VERIFICATION';

export interface User {
  id: string;
  username: string;
  email: string;
  role: Role;
  status: UserStatus;
  departmentId?: string;
  avatarUrl?: string;
}

export interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}

export type TaskStatus = 'BACKLOG' | 'TODO' | 'IN_PROGRESS' | 'IN_REVIEW' | 'COMPLETED' | 'ARCHIVED';
export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface Task {
  id: string;
  title: string;
  description: string;
  status: TaskStatus;
  priority: TaskPriority;
  creatorId: string;
  assigneeId?: string;
  assigneeName?: string;
  departmentId?: string;
  createdAt: string;
  updatedAt: string;
}

export type DocumentStatus = 'UPLOADED' | 'PARSING' | 'CHUNKED' | 'EMBEDDED' | 'ACTIVE' | 'FAILED';

export interface DocumentItem {
  id: string;
  fileName: string;
  fileKey: string;
  checksum: string;
  mimeType: string;
  sizeBytes: number;
  status: DocumentStatus;
  uploaderId: string;
  createdAt: string;
}

export interface NavItem {
  title: string;
  href: string;
  icon: string;
  badge?: string;
}
