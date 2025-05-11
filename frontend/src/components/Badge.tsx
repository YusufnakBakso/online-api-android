import React from 'react';

interface BadgeProps {
  text: string;
  type: 'success' | 'warning' | 'danger' | 'info' | 'default';
}

const Badge: React.FC<BadgeProps> = ({ text, type }) => {
  const colorClasses = {
    success: 'bg-green-100 text-green-800',
    warning: 'bg-yellow-100 text-yellow-800',
    danger: 'bg-red-100 text-red-800',
    info: 'bg-blue-100 text-blue-800',
    default: 'bg-gray-100 text-gray-800',
  };

  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${colorClasses[type]}`}>
      {text}
    </span>
  );
};

export default Badge;