import { TaskItem, CreateTaskInput, UpdateTaskInput, TaskStatus } from '../types/task';

const initialMockTasks: TaskItem[] = [
  {
    id: 'fe9e239a-9fa9-4537-a016-5f246dc97b49',
    title: 'Deploy pgvector HNSW Index Optimization',
    description: 'Configure HNSW vector index parameter m=16, ef_construction=64 on document_chunks table to achieve sub-15ms vector retrieval SLA.',
    status: 'IN_PROGRESS',
    priority: 'CRITICAL',
    creator: { id: 'usr-1', name: 'Alex Architect', email: 'alex.architect@intelliflow.ai' },
    assignee: { id: 'usr-2', name: 'David Lead', email: 'david.lead@intelliflow.ai' },
    departmentName: 'AI & Data Engineering',
    subtaskCount: 5,
    completedSubtasks: 3,
    dueDate: '2026-08-10',
    createdAt: '2026-08-01T10:00:00Z',
    updatedAt: '2026-08-02T14:30:00Z',
    comments: [
      { id: 'c-1', authorName: 'David Lead', content: 'Index benchmark tests achieved 12ms average latency across 100k vector embeddings.', createdAt: '2026-08-02T12:00:00Z' },
    ],
    attachments: [
      { id: 'att-1', fileName: 'hnsw_benchmark_report.pdf', fileSize: '2.4 MB', fileType: 'application/pdf' },
    ],
  },
  {
    id: '26448e73-ae96-4b7c-822b-d1f3bcdf1596',
    title: 'Validate OWASP Security Headers & RateLimiter Filter',
    description: 'Ensure RateLimitingFilter enforces 10 req/min limit on /api/v1/auth/login and sets HSTS + CSP response headers.',
    status: 'COMPLETED',
    priority: 'HIGH',
    creator: { id: 'usr-1', name: 'Alex Architect', email: 'alex.architect@intelliflow.ai' },
    assignee: { id: 'usr-3', name: 'Elena SecOps', email: 'elena.secops@intelliflow.ai' },
    departmentName: 'Cybersecurity & Infrastructure',
    subtaskCount: 4,
    completedSubtasks: 4,
    dueDate: '2026-08-05',
    createdAt: '2026-07-28T09:15:00Z',
    updatedAt: '2026-08-02T16:00:00Z',
  },
  {
    id: '51d520df-b404-4eb1-9b6b-9abb524fd484',
    title: 'Apache Tika Text Ingestion & Chunking Pipeline',
    description: 'Audit recursive token chunking algorithm (500 tokens / 50 overlap) for multi-part file uploads.',
    status: 'TODO',
    priority: 'HIGH',
    creator: { id: 'usr-4', name: 'Sarah PM', email: 'sarah.pm@intelliflow.ai' },
    assignee: { id: 'usr-2', name: 'David Lead', email: 'david.lead@intelliflow.ai' },
    departmentName: 'Backend Core Team',
    subtaskCount: 3,
    completedSubtasks: 1,
    dueDate: '2026-08-12',
    createdAt: '2026-08-02T08:00:00Z',
    updatedAt: '2026-08-02T08:00:00Z',
  },
  {
    id: '8a12bc90-34ef-412d-98bc-7123ef890123',
    title: 'Distributed Redis Cache Invalidation on Task Mutations',
    description: 'Integrate @CacheEvict(value="tasks", key="#id") on TaskServiceImpl status update triggers.',
    status: 'IN_REVIEW',
    priority: 'MEDIUM',
    creator: { id: 'usr-[1]', name: 'Alex Architect', email: 'alex.architect@intelliflow.ai' },
    assignee: { id: 'usr-1', name: 'Alex Architect', email: 'alex.architect@intelliflow.ai' },
    departmentName: 'Backend Core Team',
    subtaskCount: 2,
    completedSubtasks: 2,
    dueDate: '2026-08-08',
    createdAt: '2026-08-01T11:00:00Z',
    updatedAt: '2026-08-02T15:00:00Z',
  },
  {
    id: '99bf123a-5678-4abc-9012-def345678901',
    title: 'Idempotency Filter Replay Attack Verification',
    description: 'Verify ContentCachingResponseWrapper caches 2xx execution payloads by Idempotency-Key header.',
    status: 'BLOCKED',
    priority: 'CRITICAL',
    creator: { id: 'usr-3', name: 'Elena SecOps', email: 'elena.secops@intelliflow.ai' },
    assignee: { id: 'usr-3', name: 'Elena SecOps', email: 'elena.secops@intelliflow.ai' },
    departmentName: 'Cybersecurity & Infrastructure',
    subtaskCount: 4,
    completedSubtasks: 1,
    dueDate: '2026-08-07',
    createdAt: '2026-08-01T16:00:00Z',
    updatedAt: '2026-08-02T11:00:00Z',
  },
  {
    id: 'c7890123-def4-5678-9012-abc345678901',
    title: 'Legacy REST V0 API Endpoint Deprecation',
    description: 'Move deprecated REST controller mappings to archive status under Spring Boot API Versioning strategy.',
    status: 'ARCHIVED',
    priority: 'LOW',
    creator: { id: 'usr-4', name: 'Sarah PM', email: 'sarah.pm@intelliflow.ai' },
    departmentName: 'API Governance',
    subtaskCount: 1,
    completedSubtasks: 1,
    dueDate: '2026-07-20',
    createdAt: '2026-07-15T10:00:00Z',
    updatedAt: '2026-07-25T12:00:00Z',
  },
  {
    id: 'd1234567-89ab-cdef-0123-456789abcdef',
    title: 'Refactor Keyset Pagination Cursor Strategy',
    description: 'Implement SQL condition (created_at < last_created_at OR (created_at = last_created_at AND id < last_id)) for infinite scroll.',
    status: 'BACKLOG',
    priority: 'MEDIUM',
    creator: { id: 'usr-1', name: 'Alex Architect', email: 'alex.architect@intelliflow.ai' },
    departmentName: 'Backend Core Team',
    subtaskCount: 2,
    completedSubtasks: 0,
    dueDate: '2026-08-20',
    createdAt: '2026-08-02T13:00:00Z',
    updatedAt: '2026-08-02T13:00:00Z',
  },
];

let memoryTasks = [...initialMockTasks];

export const taskApi = {
  fetchTasks: async (): Promise<TaskItem[]> => {
    try {
      const res = await fetch('/api/v1/tasks');
      if (res.ok) {
        const data = await res.json();
        return data.data || data;
      }
    } catch {
      // Fallback to local memory mock data
    }
    return new Promise((resolve) => setTimeout(() => resolve([...memoryTasks]), 200));
  },

  createTask: async (input: CreateTaskInput): Promise<TaskItem> => {
    const newTask: TaskItem = {
      id: crypto.randomUUID(),
      title: input.title,
      description: input.description,
      status: 'TODO',
      priority: input.priority,
      creator: { id: 'usr-1', name: 'Alex Architect', email: 'alex.architect@intelliflow.ai' },
      assignee: input.assigneeId ? { id: input.assigneeId, name: 'Assigned User' } : undefined,
      departmentName: input.departmentId ? 'Engineering' : 'General',
      subtaskCount: 0,
      completedSubtasks: 0,
      dueDate: input.dueDate || new Date(Date.now() + 7 * 86400000).toISOString().split('T')[0],
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
    memoryTasks = [newTask, ...memoryTasks];
    return newTask;
  },

  updateTask: async (id: string, input: UpdateTaskInput): Promise<TaskItem> => {
    memoryTasks = memoryTasks.map((t) => (t.id === id ? { ...t, ...input, updatedAt: new Date().toISOString() } : t));
    const updated = memoryTasks.find((t) => t.id === id);
    if (!updated) throw new Error('Task not found');
    return updated;
  },

  updateTaskStatus: async (id: string, status: TaskStatus): Promise<TaskItem> => {
    memoryTasks = memoryTasks.map((t) => (t.id === id ? { ...t, status, updatedAt: new Date().toISOString() } : t));
    const updated = memoryTasks.find((t) => t.id === id);
    if (!updated) throw new Error('Task not found');
    return updated;
  },

  deleteTask: async (id: string): Promise<void> => {
    memoryTasks = memoryTasks.filter((t) => t.id !== id);
  },
};
