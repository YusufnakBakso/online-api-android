export type UserRole = 'admin' | 'librarian' | 'member';

export interface User {
  id: number;
  full_name: string;
  email: string;
  role: UserRole;
  created_at: string;
  updated_at: string;
}

export interface Category {
  id: number;
  name: string;
  description?: string;
  created_at: string;
  updated_at: string;
}

export interface Author {
  id: number;
  name: string;
  bio?: string;
  created_at: string;
  updated_at: string;
}

export interface Publisher {
  id: number;
  name: string;
  address?: string;
  created_at: string;
  updated_at: string;
}

export interface Book {
  id: number;
  title: string;
  author_id?: number;
  publisher_id?: number;
  category_id?: number;
  isbn: string;
  published_year: number;
  stock: number;
  created_at: string;
  updated_at: string;
  // Populated fields
  author?: Author;
  publisher?: Publisher;
  category?: Category;
}

export type TransactionStatus = 'borrowed' | 'returned' | 'overdue';

export interface Transaction {
  id: number;
  user_id: number;
  book_id: number;
  borrowed_at: string;
  due_date: string;
  returned_at?: string;
  status: TransactionStatus;
  created_at: string;
  updated_at: string;
  // Populated fields
  user?: User;
  book?: Book;
  fine?: Fine;
}

export interface Fine {
  id: number;
  transaction_id: number;
  amount: number;
  paid: boolean;
  created_at: string;
  updated_at: string;
  // Populated fields
  transaction?: Transaction;
}

export interface Review {
  id: number;
  user_id: number;
  book_id: number;
  rating: number;
  comment?: string;
  created_at: string;
  updated_at: string;
  // Populated fields
  user?: User;
  book?: Book;
}

export interface Notification {
  id: number;
  user_id: number;
  message: string;
  is_read: boolean;
  created_at: string;
  // Populated fields 
  user?: User;
}

export interface ActivityLog {
  id: number;
  user_id: number;
  action: string;
  timestamp: string;
  // Populated fields
  user?: User;
}

export interface TableColumn {
  key: string;
  label: string;
}

export interface ActionButton {
  label: string;
  color: string;
  onClick: (item: any) => void;
}

export interface FormField {
  id: string;
  label: string;
  type: 'text' | 'email' | 'number' | 'select' | 'textarea' | 'date';
  placeholder?: string;
  options?: { value: string | number; label: string }[];
  required?: boolean;
}