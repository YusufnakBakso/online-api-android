import { Inter } from 'next/font/google';
import Navbar from '@/components/Navbar';
import Sidebar from '@/components/Sidebar';
import type { Metadata } from 'next';
import type { ReactNode } from 'react';

const inter = Inter({ subsets: ['latin'] });

export const metadata: Metadata = {
  title: 'Sistem Perpustakaan',
  description: 'Aplikasi Manajemen Perpustakaan Modern',
};

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <div className="flex h-screen bg-gray-100">
        <Sidebar />
        <div className="flex flex-col flex-1 overflow-hidden">
        <Navbar />
        <main className="flex-1 overflow-y-auto p-4 md:p-6">
            {children}
        </main>
        </div>
    </div>
  );
}   