import React from 'react';
import Link from 'next/link';
import { BookOpen, Users, Bookmark, Calendar, FileText, Settings, BookCopy, Clock, Home, LogOut, DollarSign, Star } from 'lucide-react';

const Sidebar: React.FC = () => {
  const menuItems = [
    { icon: Home, name: 'Dashboard', href: '/' },
    { icon: BookOpen, name: 'Buku', href: '/books' },
    { icon: Users, name: 'Anggota', href: '/members' },
    { icon: BookCopy, name: 'Peminjaman', href: '/transactions' },
    { icon: Clock, name: 'Pengembalian', href: '/returns' },
    { icon: DollarSign, name: 'Denda', href: '/fines' },
    { icon: Star, name: 'Ulasan', href: '/reviews' },
    { icon: Calendar, name: 'Penerbit', href: '/publishers' },
    { icon: Bookmark, name: 'Kategori', href: '/categories' },
    { icon: FileText, name: 'Laporan', href: '/reports' },
    { icon: Settings, name: 'Pengaturan', href: '/settings' },
  ];

  return (
    <aside className="hidden md:flex md:flex-shrink-0">
      <div className="flex flex-col w-64 bg-gray-800 text-white">
        <div className="flex flex-col flex-1 overflow-y-auto">
          <nav className="flex-1 px-2 py-4 space-y-1">
            <div className="flex items-center h-16 px-4">
            <Link href="/" className="flex items-center">
              <BookOpen className="h-8 w-8 text-blue-400" />
              <span className="ml-2 text-xl font-bold">PERPUSTAKAAN</span>
            </Link>
            </div>
            {menuItems.map((item) => (
              <Link
                key={item.name}
                href={item.href}
                className="flex items-center px-4 py-2 text-gray-300 hover:bg-gray-700 hover:text-white rounded-md group"
              >
                <item.icon className="h-5 w-5 mr-3" />
                <span className="flex-1">{item.name}</span>
              </Link>
            ))}
            <div className="">
            <button className="flex items-center w-full px-4 py-2 text-gray-300 hover:bg-gray-700 hover:text-white rounded-md group">
              <LogOut />
              <span className='px-2'>Keluar</span>
            </button>
          </div>
          </nav>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;