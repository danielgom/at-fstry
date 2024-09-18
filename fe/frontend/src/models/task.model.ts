export interface Task {
  id?: string;
  title: string;
  status: string;
  description: string;
  due_date: string; // You might want to use Date type if you need date manipulation
}

export interface TaskFilter {
  orderBy: string;
  orderType: 'ASC' | 'DESC';
  pageNumber: number;
  pageSize: number;
  status: string | null;
}
