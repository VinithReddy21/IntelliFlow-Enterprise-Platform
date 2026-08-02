export type NotificationType = 'TASK_ASSIGNED' | 'DOCUMENT_EMBEDDED' | 'SECURITY_ALERT' | 'SYSTEM_MAINTENANCE';

export interface NotificationItem {
  id: string;
  title: string;
  message: string;
  type: NotificationType;
  isRead: boolean;
  timestamp: string;
}
