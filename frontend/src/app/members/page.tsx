'use client'
import Navbar from '@/components/Navbar';
import Sidebar from '@/components/Sidebar';
import Table from '@/components/Table';
import { useState, useEffect } from 'react';
import { User, TableColumn, ActionButton } from '@/types';
import type { ReactNode } from 'react';

export default function RootLayout({ children }: { children: ReactNode }) {
  const [users, setUsers] = useState<User[]>([]);
  
  // Definisikan kolom untuk tabel
  const columns: TableColumn[] = [
    { key: 'no', label: 'No' },
    { key: 'full_name', label: 'Full Name' },
    { key: 'email', label: 'Email' },
    { key: 'role', label: 'Role' }
  ];

  // Tombol aksi (opsional)
  const actionButtons: ActionButton[] = [
    {
      label: 'Tambah',
      color: 'bg-green-500 hover:bg-blue-600',
      onClick: (user) => alert(`Edit user: ${user.full_name}`)
    },
    {
      label: 'Edit',
      color: 'bg-blue-500 hover:bg-red-600',
      onClick: (user) => alert(`Delete user: ${user.full_name}`)
    }
  ];

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await fetch('http://localhost:5000/api/users');
        if (response.ok) {
          const data = await response.json();
          setUsers(data.data);
        } else {
          console.error('Failed to fetch users:', response.statusText);
        }
      } catch (error) {
        console.error('Error fetching users:', error);
      }
    };

    fetchUsers();
  }, []);

  return (
    <div className="flex h-screen bg-gray-100">
      <Sidebar />
      <div className="flex flex-col flex-1 overflow-hidden">
        <Navbar />
        <main className="flex-1 overflow-y-auto p-4 md:p-6">
          <Table columns={columns} data={users} actionButtons={actionButtons} />
          {children}
        </main>
      </div>
    </div>
  );
}
