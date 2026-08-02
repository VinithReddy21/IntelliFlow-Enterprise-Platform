export type TaskStatus = 
  | 'BACKLOG' 
  | 'TODO' 
  | 'IN_PROGRESS' 
  | 'BLOCKED' 
  | 'IN_REVIEW' 
  | 'COMPLETED' 
  | 'ARCHIVED';

export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface TaskUser {
  id: string;
  name: string;
  avatarUrl?: string;
  email?: string;
}

export interface TaskComment {
  id: string;
  authorName: string;
  content: string;
  createdAt: string;
}

export interface TaskAttachment {
  id: string;
  fileName: string;
  fileSize: string;
  fileType: string;
}

export interface TaskItem {
  id: string;
  title: string;
  description: string;
  status: TaskStatus;
  priority: TaskPriority;
  creator: TaskUser;
  assignee?: TaskUser;
  departmentId?: string;
  departmentName?: string;
  subtaskCount: number;
  completedSubtasks: number;
  dueDate?: string;
  createdAt: string;
  updatedAt: string;
  comments?: TaskComment[];
  attachments?: TaskAttachment[];
  dependencies?: string[];
}

export interface CreateTaskInput {
  title: string;
  description: string;
  priority: TaskPriority;
  assigneeId?: string;
  departmentId?: string;
  dueDate?: string;
}

export interface UpdateTaskInput extends Partial<CreateTaskInput> {
  status?: TaskStatus;
}

export interface TaskFilterState {
  searchQuery: string;
  status?: TaskStatus | 'ALL';
  priority?: TaskPriority | 'ALL';
  assigneeId?: string | 'ALL';
}
