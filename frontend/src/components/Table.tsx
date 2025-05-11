import React from 'react';
import { TableColumn, ActionButton } from '@/types';

interface TableProps {
  columns: TableColumn[];
  data: any[];
  actionButtons?: ActionButton[];
}

const Table: React.FC<TableProps> = ({ columns, data, actionButtons }) => {
  return (
    <div className="bg-white shadow overflow-hidden sm:rounded-lg">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-lime-200">
            <tr>
              {columns.map((column) => (
                <th
                  key={column.key}
                  scope="col"
                  className="px-6 py-3 text-center text-xs font-bold text-w uppercase tracking-wider"
                >
                  {column.label}
                </th>
              ))}
              {actionButtons && (
                <th scope="col" className="px-6 py-3 text-center text-xs font-bold text-w uppercase tracking-wider">
                  Action
                </th>
              )}
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {data.map((row, rowIndex) => (
              <tr key={rowIndex}>
                {columns.map((column) => (
                  <td key={column.key} className="px-6 py-4 whitespace-nowrap text-sm text-center text-black">
                    {column.key === 'no' ? rowIndex + 1 : row[column.key]}
                  </td>
                ))}
                {actionButtons && (
                  <td className="px-6 py-4 whitespace-nowrap text-center text-sm font-medium">
                    <div className="flex items-center justify-center space-x-2">
                      {actionButtons.map((button, index) => (
                        <button
                          key={index}
                          onClick={() => button.onClick(row)}
                          className={`px-3 py-1 rounded-md text-sm text-white ${button.color}`}
                        >
                          {button.label}
                        </button>
                      ))}
                    </div>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Table;